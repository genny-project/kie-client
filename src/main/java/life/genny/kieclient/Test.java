package life.genny.kieclient;

import java.util.HashMap;
import java.util.Map;

import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.server.client.RuleServicesClient;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Test {
	
	private static final String URL = "http://127.0.0.1:8230/kie-server/services/rest/server";
	private static final String USER = "kieserver";
	private static final String PASSWORD = "kieserver1!";
	private KieServicesConfiguration conf;
	private KieServicesClient kieServicesClient;
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
	private UserTaskServicesClient userTaskServicesClient;
	private ProcessServicesClient processServicesClient;
	private RuleServicesClient ruleServicesClient;
	private final String BUSINESSTYPE = "businessType";
	private final String BUSINESSEVENT = "businessEvent";

	public void initialize() {
		conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
		conf.setMarshallingFormat(FORMAT);
		kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
		processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
//	    userTaskServicesClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);
//	    ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
	}
	
	private static Test kieclient = null;
	private Test() {}
	public static Test getKieClient() {

		if (kieclient == null) {
			kieclient = new Test();
			kieclient.initialize();
		}
		return kieclient;
	}

//	public void startProcess(String containerId, String processId, Map<String, Object> params) {
//		System.out.println(processServicesClient.startProcess(containerId, processId, params));
//	}
//
//	public void findNodesActive(String containerId, Long instanceProcessId) {
//		System.out.println(processServicesClient.findActiveNodeInstances(containerId, instanceProcessId, 0, 100));
//	}
//
//	public void completeWorkItem(String containerId, Long instanceProcessId, Long id, Map<String, Object> results) {
//		processServicesClient.completeWorkItem(containerId, instanceProcessId, id, results);
//	}
//
//	public void getAllAboutWorkItemToBeExecuted(String containerId, Long instanceProcessId) {
//		System.out.println(processServicesClient.getWorkItemByProcessInstance(containerId, instanceProcessId));
//	}
//
//	public void getAllAboutProcessInstance(String containerId, Long instanceProcessId) {
//		System.out.println(processServicesClient.getProcessInstance(containerId, instanceProcessId));
//	}
//
//	public void getAssociatedEntityDefinition(String containerId, String processId) {
//		System.out.println(processServicesClient.getAssociatedEntityDefinitions(containerId, processId));
//	}

	public static void main(String... strings) {
//		Map<String, Object> params1 = new HashMap<String, Object>();
//		params1.put("reason", "For outstanding results");
//		params1.put("Comment", "Very well done!");
//		Test t = new Test();
//		t.initialize();
		// t.startProcess("example", "evaluation", params);
		// t.findNodesActive("example", 10L);
		// t.completeWorkItem("example", 6L,19L ,params1);
		// t.getAssociatedEntityDefinition("example","evaluation");
		// try {
		// t.getAllAboutWorkItemToBeExecuted("example", 8L);
		// }catch(KieServicesHttpException e) {
		//
		// }
		// try {
		// t.getAllAboutProcessInstance("example", 8L);
		// }catch(KieServicesHttpException e) {
		//
		// }
		// t.processServicesClient.abortProcessInstance("example",7L);
		// t.processServicesClient.abortWorkItem("example", 8L, 8L);
		// t.processServicesClient.findActiveNodeInstances("example", 8L, 0, 30);
		// t.processServicesClient.findCompletedNodeInstances("example", 6L, 0, 30);
		// t.processServicesClient.findNodeInstances("example", 8L, 0, 30);
		// t.processServicesClient.findVariableHistory("example", 8L,"reason", 0, 30);
		// t.processServicesClient.findVariablesCurrentState("example",8L);
		// t.processServicesClient.getAvailableSignals("example",8L);
		// t.processServicesClient.getProcessDefinition("example","evaluation");
		// t.processServicesClient.getProcessInstance("example",8L, true);
		// t.processServicesClient.getProcessVariableDefinitions("example","evaluation");
		// t.processServicesClient.getServiceTaskDefinitions("example","evaluation");
		// t.processServicesClient.getUserTaskDefinitions("example","evaluation");
		// t.processServicesClient.getUserTaskInputDefinitions("example","evaluation",
		// "Self Evaluation");
		// t.processServicesClient.getUserTaskOutputDefinitions("example","evaluation",
		// "Self Evaluation");
		// t.processServicesClient.getWorkItem("example", 8L, 8L);
		// t.processServicesClient.set;

	}

	public void conditions(JsonObject obj) {
		Map<String, Object> msg = obj.getMap();
		String businessType = (String) msg.get("businessType");
		String businessEvent = (String) msg.get("businessEvent");
		String containerId = (String) msg.get("container");
		String processId = (String) msg.get("processId");
		String taskName = (String) msg.get("taskName");
		String variableId = (String) msg.get("variableId");
		Object value = msg.get("variableValue");
		Long id = null;
		Long instanceProcessId = null;
		Map<String, Object> params = (Map<String, Object>) msg.get("params");
		Integer instanceProcessIdTmp = (Integer) msg.get("processInstanceId");
		Integer idTmp = (Integer) msg.get("itemId");
		try {
			id = new Long(idTmp);
			instanceProcessId = new Long(instanceProcessIdTmp);
		} catch (NullPointerException e) {
			
		}
		if (businessType.equals(BusinessProcessType.PROCESS.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.START.getDescription())) {
				processServicesClient.startProcess(containerId, processId, params);
		} else if (businessType.equals(BusinessProcessType.WORKITEM.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.START.getDescription())) {
				processServicesClient.completeWorkItem(containerId, instanceProcessId, id, params);
		} else if (businessType.equals(BusinessProcessType.PROCESS.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.ABORT.getDescription())) {
			processServicesClient.abortProcessInstance(containerId, instanceProcessId);
		} else if (businessType.equals(BusinessProcessType.WORKITEM.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.ABORT.getDescription())) {
			processServicesClient.abortWorkItem(containerId, instanceProcessId, id);
		} else if (businessType.equals(BusinessProcessType.ASSOCIATEENTITY.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.GET)) {
			
			processServicesClient.getAssociatedEntityDefinitions(containerId, processId);
		} else if (businessType.equals(BusinessProcessType.NODE.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription())) {
			processServicesClient.findActiveNodeInstances(containerId, instanceProcessId, 0, 100);
		} else if (businessType.equals(BusinessProcessType.WORKITEM.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.GET.getDescription())) {
			processServicesClient.getWorkItemByProcessInstance(containerId, instanceProcessId);
		} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription())) {
			processServicesClient.findVariableHistory(containerId, instanceProcessId, taskName, 0, 30);
		} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.SET.getDescription()) && params == null) {
			processServicesClient.setProcessVariable(containerId, instanceProcessId, variableId, value);
		} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
				&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription()) && params != null) {
			processServicesClient.setProcessVariables(containerId, instanceProcessId, params);
		}
	}
}
