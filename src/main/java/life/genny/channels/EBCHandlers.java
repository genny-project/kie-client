package life.genny.channels;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.server.api.exception.KieServicesHttpException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import life.genny.kieclient.Test;
//import life.genny.qwanda.Answer;
//import life.genny.qwanda.Ask;
//import life.genny.qwanda.message.QDataAnswerMessage;
//import life.genny.qwanda.message.QDataAskMessage;
//import life.genny.qwanda.message.QEventMessage;

import org.kie.api.runtime.Globals;

public class EBCHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(EBCHandlers.class);
	static KieServices ks = KieServices.Factory.get();
	static KieContainer kContainer;
	final static String qwandaApiUrl = System.getenv("REACT_APP_QWANDA_API_URL");
	final static String vertxUrl = System.getenv("REACT_APP_VERTX_URL");
	final static String hostIp = System.getenv("HOSTIP");
	static KieSession kSession;
	static String token;
	
	public static void registerHandlers(EventBus eventBus){
		EBConsumers.getFromEvents().subscribe(arg -> {
			logger.info("Received Event! - events");
			JsonObject ob = Buffer.buffer(arg.body().toString()).toJsonObject();
			Test executeConditions = Test.getKieClient();
			System.out.println(arg.body());
			Vertx.vertx().executeBlocking(ok -> {
				try {executeConditions.conditions(ob);}catch(KieServicesHttpException e) {}
			}, ex -> {

			});
		});
		EBConsumers.getFromData().subscribe(arg -> {
			logger.info("Received Event! - data");
			JsonObject a = Buffer.buffer(arg.toString().toString()).toJsonObject();
		});
		
	}
}
