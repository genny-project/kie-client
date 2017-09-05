package life.genny.kieclient;

import org.bitsofinfo.hazelcast.discovery.docker.swarm.SwarmAddressPicker;
import org.bitsofinfo.hazelcast.discovery.docker.swarm.SystemPrintLogger;
import org.kie.server.api.exception.KieServicesHttpException;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.DefaultNodeContext;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeContext;

//import ch.qos.logback.core.pattern.parser.Node;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
//import org.slf4j.LoggerFactory;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import rx.Observable;

public class Service extends AbstractVerticle {
	
	private EventBus eventBus = null;
	private Observable<Message<Object>> fromBridgecmd;
	private static final Logger logger = LoggerFactory.getLogger(Service.class);

	@Override
	public void start() {
		setupCluster();
	}

//	public void setupCluster() {
//		Future<Void> startFuture = Future.future();
//		createCluster().compose(v -> {
//			fromBridgecmd = eventBus.consumer("rules.kieclient").toObservable();
//			fromBridgecmd.subscribe(arg -> {
////				BusinessProcessController bpctrl = new BusinessProcessController();
//				JsonObject ob = Buffer.buffer(arg.body().toString()).toJsonObject();
//				Test executeConditions = Test.getKieClient();
//				executeConditions.initialize();
//				executeConditions.conditions(ob);
////				bpctrl.businessReceptor(ob);
////				System.out.println(ob);
//			});
//			startFuture.complete();
//		}, startFuture);
//	}
//
//	public Future<Void> createCluster() {
//		Future<Void> startFuture = Future.future();
//
//		ClusterManager mgr = new HazelcastClusterManager();
//		VertxOptions options = new VertxOptions().setClusterManager(mgr);
//		
//		
//		Vertx.clusteredVertx(options, res -> {
//			if (res.succeeded()) {
//				eventBus = res.result().eventBus();
//				System.out.println("Cluster Started!");
//				startFuture.complete();
//			} else {
//				// failed!
//				
//			}
//		});
//		return startFuture;
//	}
	public void setupCluster() {
		Future<Void> startFuture = Future.future();
		createCluster().compose(v -> {
			System.out.println("+++  jdkjskjfdkj sdjfksd  +++");
			fromBridgecmd = eventBus.consumer("rules.kieclient").toObservable();
			fromBridgecmd.subscribe(arg -> {
				System.out.println("jdkjskjfdkj sdjfksd f+++++++++++++");
//				BusinessProcessController bpctrl = new BusinessProcessController();
				JsonObject ob = Buffer.buffer(arg.body().toString()).toJsonObject();
				Test executeConditions = Test.getKieClient();
				System.out.println(arg.body());
//				executeConditions.initialize();
				vertx.executeBlocking(ok -> {
					try {executeConditions.conditions(ob);}catch(KieServicesHttpException e) {}
				}, ex -> {

				});
//				bpctrl.businessReceptor(ob);
//				System.out.println(ob);
			});
			startFuture.complete();
		}, startFuture);
	}

	public Future<Void> createCluster() {
		Future<Void> startFuture = Future.future();

		vertx.<HazelcastInstance>executeBlocking(future -> {

			if (System.getenv("SWARM") != null) {

				Config conf = new ClasspathXmlConfig("cluster.xml");
				System.out.println("Starting hazelcast DISCOVERY!!!!!");
				NodeContext nodeContext = new DefaultNodeContext() {
					@Override
					public AddressPicker createAddressPicker(Node node) {
						return new SwarmAddressPicker(new SystemPrintLogger());
					}
				};

				HazelcastInstance hazelcastInstance = HazelcastInstanceFactory.newHazelcastInstance(conf, "kieclient",
						nodeContext);
				System.out.println("Done hazelcast DISCOVERY");
				future.complete(hazelcastInstance);
			} else {
				future.complete(null);
			}
		}, res -> {
			if (res.succeeded()) {
				System.out.println("RESULT SUCCEEDED");
				HazelcastInstance hazelcastInstance = (HazelcastInstance) res.result();
				ClusterManager mgr = null;
				if (hazelcastInstance != null) {
					mgr = new HazelcastClusterManager(hazelcastInstance);
				} else {
					mgr = new HazelcastClusterManager(); // standard docker
				}
				System.out.println("Starting Clustered Vertx");
				VertxOptions options = new VertxOptions().setClusterManager(mgr);

				if (System.getenv("SWARM") == null) {
					if (System.getenv("GENNYDEV") == null) {
						System.out.println("setClusterHost etc");
						options.setClusterHost("kieclient").setClusterPublicHost("kieclient").setClusterPort(15701);
					} else {
						logger.info("Running DEV mode, no cluster");
						options.setBlockedThreadCheckInterval(200000000);
						options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
					}

				}

				Vertx.clusteredVertx(options, res2 -> {
					if (res2.succeeded()) {
						eventBus = res2.result().eventBus();
						// handler.setEventBus(eventBus);
						System.out.println("rules Cluster Started!");
						startFuture.complete();
					} else {
						// failed!
					}
				});
			}
		});

		return startFuture;
	}

}
