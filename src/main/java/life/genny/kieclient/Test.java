package life.genny.kieclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Test {

	private static final String URL = System.getenv("KIE_SERVER") != null ? System.getenv("KIE_SERVER")
			: "http://127.0.0.1:8230/kie-server/services/rest/server";
	private static final String USER = System.getenv("KIE_USERNAME") != null ? System.getenv("KIE_USERNAME")
			: "kieserver";
	private static final String PASSWORD = System.getenv("KIE_PASSWORD") != null ? System.getenv("KIE_PASSWORD")
			: "kieserver1!";
	private KieServicesConfiguration conf;
	private KieServicesClient kieServicesClient;
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
	private UserTaskServicesClient userTaskServicesClient;
	private ProcessServicesClient processServicesClient;
	private RuleServicesClient ruleServicesClient;
	private final String BUSINESSTYPE = "businessType";
	private final String BUSINESSEVENT = "businessEvent";
	

	final String qwandaApiUrl = System.getenv("REACT_APP_QWANDA_API_URL");
	final String vertxUrl = System.getenv("REACT_APP_VERTX_URL");
	final String hostIp = System.getenv("HOSTIP");
	

	String keycloakProto = System.getenv("KEYCLOAK_PROTO")!=null?System.getenv("KEYCLOAK_PROTO"): "http://";
    String keycloakPort = System.getenv("KEYCLOAK_PORT")!=null?System.getenv("KEYCLOAK_PORT"): "8180";
    String keycloakIP = System.getenv("HOSTIP")!=null?System.getenv("HOSTIP"): "localhost";
	String keycloakUrl = keycloakProto+keycloakIP+":"+keycloakPort;
	
	
	String keycloakClientId = System.getenv("KEYCLOAK_CLIENTID")!=null?System.getenv("KEYCLOAK_CLIENTID"):"curl";
	String keycloakUser = System.getenv("KEYCLOAK_USERID")!=null?System.getenv("KEYCLOAK_USERID"):"user1";
	String keycloakPassword = System.getenv("KEYCLOAK_PASSWORD")!=null?System.getenv("KEYCLOAK_PASSWORD"):"password1";
	String realm = System.getenv("KEYCLOAK_REALM")!=null?System.getenv("KEYCLOAK_REALM"):"wildfly-swarm-keycloak-example"; 	
	String secret = System.getenv("KEYCLOAK_SECRET")!=null?System.getenv("KEYCLOAK_SECRET"):"056b73c1-7078-411d-80ec-87d41c55c3b4";  

	
	String qwandaServiceUrl = System.getenv("REACT_APP_QWANDA_API_URL")==null?System.getenv("REACT_APP_QWANDA_API_URL"):qwandaApiUrl;
	

	public void initialize() {
		conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
		conf.setMarshallingFormat(FORMAT);
		kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
		processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
		// userTaskServicesClient =
		// kieServicesClient.getServicesClient(UserTaskServicesClient.class);
		ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
	}

	private static Test kieclient = null;

	private Test() {
	}

	public static Test getKieClient() {
		if (kieclient == null) {
			kieclient = new Test();
			kieclient.initialize();
		}
		return kieclient;
	}

	// public void startProcess(String containerId, String processId, Map<String,
	// Object> params) {
	// System.out.println(processServicesClient.startProcess(containerId, processId,
	// params));
	// }
	//
	// public void findNodesActive(String containerId, Long instanceProcessId) {
	// System.out.println(processServicesClient.findActiveNodeInstances(containerId,
	// instanceProcessId, 0, 100));
	// }
	//
	// public void completeWorkItem(String containerId, Long instanceProcessId, Long
	// id, Map<String, Object> results) {
	// processServicesClient.completeWorkItem(containerId, instanceProcessId, id,
	// results);
	// }
	//
	// public void getAllAboutWorkItemToBeExecuted(String containerId, Long
	// instanceProcessId) {
	// System.out.println(processServicesClient.getWorkItemByProcessInstance(containerId,
	// instanceProcessId));
	// }
	//
	// public void getAllAboutProcessInstance(String containerId, Long
	// instanceProcessId) {
	// System.out.println(processServicesClient.getProcessInstance(containerId,
	// instanceProcessId));
	// }
	//
	// public void getAssociatedEntityDefinition(String containerId, String
	// processId) {
	// System.out.println(processServicesClient.getAssociatedEntityDefinitions(containerId,
	// processId));
	// }
	public static void main(String... strings) {
		// Map<String, Object> params1 = new HashMap<String, Object>();
		// params1.put("reason", "For outstanding results");
		// params1.put("Comment", "Very well done!");
		// Test t = new Test();
		// t.initialize();
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
		
			Map<String, Object> msg = obj.getJsonObject("data").getMap();
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
					&& businessEvent.equals(BusinessProcessEvent.GET.getDescription())) {
				processServicesClient.getAssociatedEntityDefinitions(containerId, processId);
			} else if (businessType.equals(BusinessProcessType.NODE.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription())) {
				System.out.println("---node--find");
				System.out.println(processServicesClient.findActiveNodeInstances(containerId, instanceProcessId, 0, 100));
			} else if (businessType.equals(BusinessProcessType.WORKITEM.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.GET.getDescription())) {
				System.out.println("---workitem--get");
				System.out.println(processServicesClient.getWorkItemByProcessInstance(containerId, instanceProcessId));
			} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription())) {
				processServicesClient.findVariableHistory(containerId, instanceProcessId, taskName, 0, 30);
			} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.SET.getDescription()) && params == null) {
				processServicesClient.setProcessVariable(containerId, instanceProcessId, variableId, value);
			} else if (businessType.equals(BusinessProcessType.VARIABLES.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.FIND.getDescription()) && params != null) {
				processServicesClient.setProcessVariables(containerId, instanceProcessId, params);
			} else if (businessType.equals(BusinessProcessType.PROCESS.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.PREINFO.getDescription())) {
				System.out.println("---Process--info");
				System.out.println(processServicesClient.getProcessInstance(containerId, instanceProcessId));
			} else if (businessType.equals(BusinessProcessType.RULE.getDescription())
					&& businessEvent.equals(BusinessProcessEvent.FIRE.getDescription())) {
				KieCommands commandsFactory = KieServices.Factory.get().getCommands();
				KieServices.Factory.get().getCommands().newCompleteWorkItem(2L, new HashMap<String, Object>());
				Command<?> fireAllRules = commandsFactory.newFireAllRules();
				Command<?> insert = commandsFactory.newInsert(obj.getMap());
				
				List<Command<?>> insertGlobals = new ArrayList<Command<?>>();//commandsFactory.newSetGlobal("", keycloakProto);
				
				insertGlobals.add(commandsFactory.newSetGlobal("KEYPROTO", keycloakProto));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYPORT", keycloakPort));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYIP", keycloakIP));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYURL", keycloakUrl));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYID", keycloakClientId));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYUSER", keycloakUser));
				insertGlobals.add(commandsFactory.newSetGlobal("KEYPASS", keycloakPassword));
				insertGlobals.add(commandsFactory.newSetGlobal("REALM", realm));
				insertGlobals.add(commandsFactory.newSetGlobal("SECRET", secret));
				insertGlobals.add(commandsFactory.newSetGlobal("QWANDASERURL", qwandaServiceUrl));
				
				System.out.println(keycloakProto+"---1---"+ keycloakPort+"---2---"+keycloakIP+"---3---"+
				keycloakUrl+"--4----"+keycloakClientId+"---5---"+keycloakUser+"----6--"+
						keycloakPassword+"--7----"+realm+"--8----"+
				secret+"---9---"+qwandaServiceUrl+"---end---");
		
				List<Command<?>> allCommands = new ArrayList<Command<?>>();
				
				
				allCommands.addAll(insertGlobals);
			
				allCommands.addAll(Arrays.asList(insert, fireAllRules));
				allCommands.add(insert);
				allCommands.add(fireAllRules);
				
				Command<?> batchCommand = commandsFactory.newBatchExecution(allCommands);
				
				// ServiceResponse<org.kie.api.runtime.ExecutionResults> executeResponse =
				// ruleServicesClient
				// .executeCommandsWithResults("kie", batchCommand);
//				Command<?> batchCommands = insertGlobals.stream().map(a->a).flatMap(a->a);
				
				ServiceResponse<String> executeResponse = ruleServicesClient.executeCommands("cliente", batchCommand);
				if (executeResponse.getType() == ResponseType.SUCCESS) {
					System.out.println("Commands executed with success! Response: ");
					System.out.println(executeResponse.getResult());
				} else {
					System.out.println("Error executing rules. Message: ");
					System.out.println(executeResponse.getMsg());
				}


				// System.out.println(executeResponse.getResult());
				// System.out.println(executeResponse.getMsg());
				// System.out.println(executeResponse.getType());
			}
		
	}
}