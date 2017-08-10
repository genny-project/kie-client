package life.genny.kieclient;



import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bitsofinfo.hazelcast.discovery.docker.swarm.SwarmAddressPicker;
import org.bitsofinfo.hazelcast.discovery.docker.swarm.SystemPrintLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.DefaultNodeContext;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeContext;

import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.core.eventbus.MessageProducer;
import io.vertx.rxjava.ext.auth.oauth2.AccessToken;
import io.vertx.rxjava.ext.auth.oauth2.OAuth2Auth;
import io.vertx.rxjava.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import rx.Observable;

public class ServiceVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);

	private EventBus eventBus = null;
	MessageProducer<JsonObject> msgToFrontEnd;
	Observable<Message<Object>> events;
	Observable<Message<Object>> cmds;
	Observable<Message<Object>> data;

	JsonObject keycloakJson;
	AccessToken tokenAccessed;

	private OAuth2Auth oauth2;
	String token;
	
	private Observable<Message<Object>> fromBridgecmd;


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
			fromBridgecmd = eventBus.consumer("rules.kieclient").toObservable();
			fromBridgecmd.subscribe(arg -> {
				BusinessProcessController g = new BusinessProcessController();
				JsonObject ob = Buffer.buffer(arg.body().toString()).toJsonObject();
				g.businessReceptor(ob);
			});

			startFuture.complete();
		}, startFuture);
	}

	public Future<Void> createCluster() {
		Future<Void> startFuture = Future.future();

		vertx.executeBlocking(future -> {
			VertxOptions options = null;
			ClusterManager mgr = null;

			if (System.getenv("SWARM") != null) {

				Config conf = new ClasspathXmlConfig("hazelcast-genny.xml");
				System.out.println("Starting hazelcast DISCOVERY!!!!!");
				NodeContext nodeContext = new DefaultNodeContext() {
					@Override
					public AddressPicker createAddressPicker(Node node) {
						return new SwarmAddressPicker(new SystemPrintLogger());
					}
				};

				HazelcastInstance hazelcastInstance = HazelcastInstanceFactory.newHazelcastInstance(conf,
						"hazelcast-genny", nodeContext);
				System.out.println("Done hazelcast DISCOVERY");

				mgr = new HazelcastClusterManager(hazelcastInstance);
			} else {
				mgr = new HazelcastClusterManager();
				options = new VertxOptions().setClusterManager(mgr);

				if (System.getenv("GENNYDEV") == null) {
					System.out.println("setClusterHost etc");
					options.setClusterHost("kieclient").setClusterPublicHost("kieclient").setClusterPort(15701);
				} else {
					logger.info("Running DEV mode, no cluster");
					options.setBlockedThreadCheckInterval(200000000);
					options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);

				}
			}

			System.out.println("Starting Clustered Vertx");
			Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					eventBus = res.result().eventBus();
					// handler.setEventBus(eventBus);
					System.out.println("kie-Client Cluster Started!");
					startFuture.complete();
				} else {
					// failed!
				}
			});
		}, res -> {
			if (res.succeeded()) {

			}
		});

		return startFuture;
	}


	public void eventListeners() {
		events = eventBus.consumer("events").toObservable();
		cmds = eventBus.consumer("cmds").toObservable();
		data = eventBus.consumer("data").toObservable();
	}




}
