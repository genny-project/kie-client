package life.genny.kieclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;

public class BusinessProcessController {

	private final String BUSINESSTYPE = "businessType";
	private final String BUSINESSEVENT = "businessEvent";
	private final String DATA= "params";
	private Optional<CommandTypes> bussinessType;
	/**
	 * Predicates that test Process Types ex: User Task, Work Items etc. Initial
	 * source might be from gebcmd or a user interface
	 */

	Predicate<Entry<String, Object>> isProcess = obj -> (BusinessProcessType.PROCESS.getDescription()
			.equals(obj.getValue()) && obj.getKey().equals(BUSINESSTYPE));

	Predicate<Entry<String, Object>> isService = obj -> (BusinessProcessType.SERVICETASK.getDescription()
			.equals(obj.getValue()) && obj.getKey().equals(BUSINESSTYPE));

	Predicate<Entry<String, Object>> isUserTask = obj -> (BusinessProcessType.USERTASK.getDescription()
			.equals(obj.getValue()) && obj.getKey().equals(BUSINESSTYPE));

	Predicate<Entry<String, Object>> isWorkItem = obj -> (BusinessProcessType.WORKITEM.getDescription()
			.equals(obj.getValue()) && obj.getKey().equals(BUSINESSTYPE));

	Predicate<Entry<String, Object>> isNodeTask = obj -> (BusinessProcessType.WORKITEM.getDescription()
			.equals(obj.getValue()) && obj.getKey().equals(BUSINESSTYPE));

	/**
	 * Predicates that test Event Types ex: Start a process, complete a tack etc.
	 * Initial source might be from gebcmd or a user interface
	 */
	Predicate<String> isStart = obj -> (BusinessProcessEvent.START.getDescription().equals(obj));
	Predicate<String> isAbort = obj -> (BusinessProcessEvent.ABORT.getDescription().equals(obj));
	Predicate<String> isSkipt = obj -> (BusinessProcessEvent.SKIPT.getDescription().equals(obj));
	Predicate<String> isFind = obj -> (BusinessProcessEvent.FIND.getDescription().equals(obj));
	Predicate<String> isSignal = obj -> (BusinessProcessEvent.SIGNAL.getDescription().equals(obj));
	Predicate<String> isSet = obj -> (BusinessProcessEvent.SET.getDescription().equals(obj));
	Predicate<String> isGet = obj -> (BusinessProcessEvent.GET.getDescription().equals(obj));
	Predicate<String> isComplete = obj -> (BusinessProcessEvent.COMPLETE.getDescription().equals(obj));

	BiFunction<String, Map<String, Object>, Void> runBusinessProcessCommands = (record, data)-> {
		if (isStart.test(record)) {
			System.out.println("start");
			String container = (String) data.get("container");
			String processId = (String) data.get("processId");
			data.remove("container");
			data.remove("processId");
			bussinessType.get().run(container, processId, data);
		} else if (isAbort.test(record)) {
			System.out.println("abort");
		} else if (isSkipt.test(record)) {
			System.out.println("skipt");
		} else if (isFind.test(record)) {
			System.out.println("find");
		}  else if (isSignal.test(record)) {
			System.out.println("signal");
		}  else if (isSet.test(record)) {
			System.out.println("set");
		}  else if (isGet.test(record)) {
			System.out.println("get");
		} else if (isComplete.test(record)) {
			System.out.println("complete");
		}
		return null;
	};

	Function<Entry<String, Object>, CommandTypes> getBusinessProcessType = record -> {
		if (isProcess.test(record)) {
			return new ProcessType();
		} else if (isService.test(record)) {
			return new ServiceTaskType();
		} else if (isUserTask.test(record)) {
			return new UserTaskType();
		} else if (isNodeTask.test(record)) {
			return new WorkItemType();
		} else
			return null;
	};

	public void businessReceptor(JsonObject obj) {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg = normalizeMsg(obj);
		bussinessType = msg.entrySet().stream().peek(System.out::println)
				.map(getBusinessProcessType).peek(System.out::println).reduce((as, bi) -> as);

		if (bussinessType.isPresent())
		{
			String record = (String) msg.get(BUSINESSEVENT);
			msg.remove(BUSINESSEVENT);
			msg.remove(BUSINESSTYPE);
			Map<String, Object> data = msg;
			runBusinessProcessCommands.apply(record, data);
		}
		else {
			System.out.println("Nothing");
		}
	}

	public Map<String, Object> normalizeMsg(JsonObject obj) {
		Map<String, Object> msg = obj.getMap();
		try {
			msg.put(BUSINESSTYPE, msg.get(BUSINESSTYPE).toString().toLowerCase());
			msg.put(BUSINESSEVENT, msg.get(BUSINESSEVENT).toString().toLowerCase());
		} catch (NullPointerException e) {
			System.out.println("Not enough data");
		}
		return msg;
	}

}
