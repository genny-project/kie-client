package life.genny.kieclient;
//
//import org.bitsofinfo.hazelcast.discovery.docker.swarm.SwarmAddressPicker;
//import org.bitsofinfo.hazelcast.discovery.docker.swarm.SystemPrintLogger;
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
import life.genny.cluster.Cluster;
import rx.Observable;

public class Service extends AbstractVerticle {
	
	@Override
	public void start() {
		Cluster.joinCluster(vertx);
	}

}
