package com.outcomehub.kieclient;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.drools.core.command.runtime.rule.GetObjectsCommand;
//import org.jboss.resteasy.client.ClientRequest;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.outcomehub.qwanda.message.QEventMessage;

import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
//import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import rx.Observable;

public class Service extends AbstractVerticle {
	
	private static final Logger logger = LoggerFactory.getLogger(Service.class);

	private static final String URL = System.getenv("KIE_SERVER_URL")!=null?System.getenv("KIE_SERVER_URL"):"http://localhost:8480/kie-server/services/rest/server";
	private static final String USER = System.getenv("KIE_USERNAME")!=null?System.getenv("KIE_USERNAME"):"kieserver";
	private static final String PASSWORD = System.getenv("KIE_PASSWORD")!=null?System.getenv("KIE_PASSWORD"):"kieserver1!";
	
	private static final MarshallingFormat FORMAT =  MarshallingFormat.JSON;
	
	private KieServicesConfiguration conf;
	private KieServicesClient kieServicesClient;

	private EventBus eventBus = null;
	Observable<Message<Object>> events;
	Observable<Message<Object>> cmds;
	Observable<Message<Object>> data;

	JsonObject keycloakJson;
	String token ;

	Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
		@Override
		public LocalDateTime deserialize(JsonElement json, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime();
		}

		public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // "yyyy-mm-dd"
		}
	}).create();
	
	@Override
	public void start() {
		setupCluster();
	}
	



	
	public void setupCluster() {
		Future<Void> startFuture = Future.future();
		createCluster().compose(v -> {
			eventListeners();
			eventsInOutFromCluster();

			startFuture.complete();
		}, startFuture);
	}

	public Future<Void> createCluster() {
		Future<Void> startFuture = Future.future();

		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		if (System.getenv("GENNYDEV") == null) {
			options.setClusterHost("127.0.0.1").setClusterPublicHost("127.0.0.1").setClusterPort(15701);
			options.setBlockedThreadCheckInterval(200000000);
			options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
		} else {
			logger.info("Running DEV mode, no cluster");
		}

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				eventBus = res.result().eventBus();
				System.out.println("KIE-CLient Cluster Started!");
				startFuture.complete();
			} else {
				// failed!
			}
		});
		return startFuture;
	}

	public void eventListeners() {
		events = eventBus.consumer("events").toObservable();
		cmds = eventBus.consumer("cmds").toObservable();
		data = eventBus.consumer("data").toObservable();
	}


	/*
	 * Write any cmds or data out to the frontend
	 */
	public void eventsInOutFromCluster() {
		events.subscribe(arg -> {
			String incomingEvent = arg.body().toString();
			logger.info(incomingEvent);
			if (!incomingEvent.contains("<body>Unauthorized</body>")) {
				JsonObject json = new JsonObject(incomingEvent);     
				logger.info("GOT EVENT IN KIE-CLIENT:"+json);
				
				if (json.getString("code").equals("GEN_KIE_TEST")) {
					initialize();
					listCapabilities();
					listContainers();
					executeCommands();

				}
				
			} else {
				logger.error("Cmd with Unauthorised data recieved");
			}
		});

		cmds.subscribe(arg -> {
			String incomingCmd = arg.body().toString();
			logger.info(incomingCmd);
			if (!incomingCmd.contains("<body>Unauthorized</body>")) {
				JsonObject json = new JsonObject(incomingCmd);     
				logger.info("GOT CMD IN KIE-CLIENT:"+json);
			} else {
				logger.error("Cmd with Unauthorised data recieved");
			}
		});
		data.subscribe(arg -> {
			String incomingData = arg.body().toString();
			logger.info(incomingData);
			JsonObject json = new JsonObject(incomingData);     
			logger.info("GOT DATA IN KIE-CLIENT:"+json);
		});
	}


	
	public void initialize() {
		conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
		conf.setMarshallingFormat(FORMAT);
		kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
	}
	
	public void listCapabilities() {
		KieServerInfo serverInfo = kieServicesClient.getServerInfo().getResult();
		System.out.print("Service capabilities");
		for(String capability: serverInfo.getCapabilities()) {
			System.out.print(" --" + capability);
		}
		System.out.println();
		String j="nf";
		j.toUpperCase();
	}
	
	public void listContainers() {
	    KieContainerResourceList containersList = kieServicesClient.listContainers().getResult();
	    List<KieContainerResource> kieContainers = containersList.getContainers();
	    System.out.println("Available containers: ");
	    for (KieContainerResource container : kieContainers) {
	        System.out.println("\t" + container.getContainerId() + " (" + container.getReleaseId() + ")");
	    }
	}
	
	public void executeCommands() {
		QEventMessage msg = new QEventMessage(null, null);
		msg.getData().setCode("Layout1");
		System.out.println("the data is " + msg.getData().getCode());
		
	    System.out.println("== Sending commands to the server ==");
	    RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
//	    QueryServicesClient queryClient = kieServicesClient.getServicesClient(QueryServicesClient.class);
	    KieCommands commandsFactory = KieServices.Factory.get().getCommands();
	    QueryResults results;
	    GetObjectsCommand getObjectsCommand = new GetObjectsCommand();
	    getObjectsCommand.setOutIdentifier("name");
//	    Command<?> query = commandsFactory.newQuery("ru", "people over the age of 30");//.newInsert(person,"Person");
	    Command<?> insert = commandsFactory.newInsert("d","pe");
	    Command<?> fireAllRules = commandsFactory.newFireAllRules();
	    Command<?> batchCommand = commandsFactory.newBatchExecution(Arrays.asList(insert, fireAllRules));
	    ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults("hello", batchCommand);//	    if(executeResponse.getType() == ResponseType.SUCCESS) {
        System.out.println("kadkjs"+executeResponse.getResult().getValue("pe")+"fgdfgdg");
	}



//	public CorsHandler getCorsHandler() {
//		System.out.println("CORs handler");
//		return CorsHandler.create("/*")
//				.allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.OPTIONS)
//				.allowedMethod(HttpMethod.PATCH).allowedMethod(HttpMethod.CONNECT).allowedHeader("X-PINGARUNER")
//				.allowedHeader("Access-Control-Allow-Origin").allowCredentials(true).allowedHeader("Authorization")
//				.allowedHeader("Accept").allowedHeader("crossdomain").maxAgeSeconds(3600).allowedHeader("Content-Type");
//	}
//
//	public SockJSHandler createBridge() {
//		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
//		PermittedOptions inboundPermitted1 = new PermittedOptions().setAddress("address.inbound");
//		PermittedOptions outboundPermitted2 = new PermittedOptions().setAddressRegex("address.outbound");
//		BridgeOptions options = new BridgeOptions();
//		options.setMaxAddressLength(10000);
//		options.addInboundPermitted(inboundPermitted1);
//		options.addOutboundPermitted(outboundPermitted2);
//		sockJSHandler.bridge(options, this::bridgeHandler);
//		return sockJSHandler;
//	}

//	public void bridgeHandler(BridgeEvent bE) {
//		vertx.executeBlocking(future -> {
//			if (bE.type() == BridgeEventType.PUBLISH || bE.type() == BridgeEventType.SEND) {	
//				System.out.println(bE.getRawMessage());
//				vertx.eventBus().publish("address.outbound", "oki");
//				initialize();
//				listCapabilities();
//				listContainers();
//				executeCommands();
//			}
//			bE.complete(true);
//		}, res -> {
//			if (res.succeeded()) {
//
//			}
//		});
//		
//	}
}
