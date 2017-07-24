package droolsclient;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import testkie.MyObject;
import com.outcomehub.qwanda.message.QEventMessage;

public class Service extends AbstractVerticle {
	
	MessageProducer<JsonObject> msgToFrontEnd;

	@Override
	public void start() {
		Future<Void> fut = Future.future();
		runRouters().compose(i->{
			fut.complete();
		},fut);
//		registerLocalAddresses();
		
	}

	private static final String URL = "http://localhost:8230/kie-server/services/rest/server";
	private static final String USER = "kieserver";
	private static final String PASSWORD = "kieserver1!";
	
	private static final MarshallingFormat FORMAT =  MarshallingFormat.JSON;
	
	private KieServicesConfiguration conf;
	private KieServicesClient kieServicesClient;
	
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

	public Future<Void> runRouters() {
		Future<Void> fut = Future.future();
		Router router = Router.router(vertx);	
		router.route("/eventbus/*").handler(createBridge());
		router.route().handler(getCorsHandler());
		vertx.createHttpServer().requestHandler(router::accept).listen(8082);
		fut.complete();
		return fut;
	}

	public CorsHandler getCorsHandler() {
		System.out.println("CORs handler");
		return CorsHandler.create("/*")
				.allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.OPTIONS)
				.allowedMethod(HttpMethod.PATCH).allowedMethod(HttpMethod.CONNECT).allowedHeader("X-PINGARUNER")
				.allowedHeader("Access-Control-Allow-Origin").allowCredentials(true).allowedHeader("Authorization")
				.allowedHeader("Accept").allowedHeader("crossdomain").maxAgeSeconds(3600).allowedHeader("Content-Type");
	}

	public SockJSHandler createBridge() {
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
		PermittedOptions inboundPermitted1 = new PermittedOptions().setAddress("address.inbound");
		PermittedOptions outboundPermitted2 = new PermittedOptions().setAddressRegex("address.outbound");
		BridgeOptions options = new BridgeOptions();
		options.setMaxAddressLength(10000);
		options.addInboundPermitted(inboundPermitted1);
		options.addOutboundPermitted(outboundPermitted2);
		sockJSHandler.bridge(options, this::bridgeHandler);
		return sockJSHandler;
	}

	public void bridgeHandler(BridgeEvent bE) {
		vertx.executeBlocking(future -> {
			if (bE.type() == BridgeEventType.PUBLISH || bE.type() == BridgeEventType.SEND) {	
				System.out.println(bE.getRawMessage());
				vertx.eventBus().publish("address.outbound", "oki");
				initialize();
				listCapabilities();
				listContainers();
				executeCommands();
			}
			bE.complete(true);
		}, res -> {
			if (res.succeeded()) {

			}
		});
		
	}
}
