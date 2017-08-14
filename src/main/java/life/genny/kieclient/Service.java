package life.genny.kieclient;

import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
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
	
	@Override
	public void start() {
		setupCluster();
	}

	public void setupCluster() {
		Future<Void> startFuture = Future.future();
		createCluster().compose(v -> {
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

		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				eventBus = res.result().eventBus();
				System.out.println("Cluster Started!");
				startFuture.complete();
			} else {
				// failed!
			}
		});
		return startFuture;
	}
	
}
