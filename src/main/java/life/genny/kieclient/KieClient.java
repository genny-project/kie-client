package life.genny.kieclient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class KieClient {

	private static final String URL = System.getenv("KIE_SERVER") != null ? System.getenv("KIE_SERVER")
			: "http://127.0.0.1:8230/kie-server/services/rest/server";
	private static final String USER = System.getenv("KIE_USERNAME") != null ? System.getenv("KIE_USERNAME")
			: "kieserver";
	private static final String PASSWORD = System.getenv("KIE_PASSWORD") != null ? System.getenv("KIE_PASSWORD")
			: "kieserver1!";

	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
	private UserTaskServicesClient queryTask;
	private ProcessServicesClient proCtrl;

	private static KieClient kieclient = null;

	public static KieClient getKieClient() {

		if (kieclient == null) {
			kieclient = new KieClient();
			kieclient.initialize();
		}
		return kieclient;
	}

	public UserTaskServicesClient getQueryTask() {
		return queryTask;
	}
	
	
	private KieServicesConfiguration conf;
	private KieServicesClient kieServicesClient;

	public void initialize() {
		conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
		conf.setMarshallingFormat(FORMAT);
		kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
		proCtrl = kieServicesClient.getServicesClient(ProcessServicesClient.class);
		queryTask = kieServicesClient.getServicesClient(UserTaskServicesClient.class);
	}

	public void listCapabilities() {
		KieServerInfo serverInfo = kieServicesClient.getServerInfo().getResult();
		System.out.print("Service capabilities");
		for (String capability : serverInfo.getCapabilities()) {
			System.out.print(" --" + capability);
		}
	}

	public void listContainers() {
		KieContainerResourceList containersList = kieServicesClient.listContainers().getResult();
		List<KieContainerResource> kieContainers = containersList.getContainers();
		System.out.println("Available containers: ");
		for (KieContainerResource container : kieContainers) {
			System.out.println("\t" + container.getContainerId() + " (" + container.getReleaseId() + ")");
		}
	}

	public ServiceResponse<ExecutionResults> executeCommands() {
		System.out.println("== Sending commands to the server ==");
		RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
		KieCommands commandsFactory = KieServices.Factory.get().getCommands();
		// Command<?> insert = commandsFactory.newInsert("","pe");
		Command<?> fireAllRules = commandsFactory.newFireAllRules();
		Command<?> batchCommand = commandsFactory.newBatchExecution(Arrays.asList(fireAllRules));
		ServiceResponse<org.kie.api.runtime.ExecutionResults> executeResponse = rulesClient
				.executeCommandsWithResults("kie", batchCommand);
		return executeResponse;
	}

	public void kieClientStartProcess(String containerId, String processId, Map<String, Object> params) {
		Long id = null;
		try {
			 id = proCtrl.startProcess(containerId, processId, params);
			 System.out.println(proCtrl.getProcessInstance(containerId, id));
			 System.out.println(proCtrl.findActiveNodeInstances(containerId, id, 0, 100));
			 System.out.println(proCtrl.getUserTaskDefinitions(containerId, processId));
			 System.out.println(proCtrl.getServiceTaskDefinitions(containerId, processId));
		}catch(rx.exceptions.OnErrorNotImplementedException e) {
			System.out.println("error");
		}catch(KieServicesHttpException e) {
			System.out.println("error");
		}
		catch(IllegalArgumentException e) {}
	}

	public void kieClientAbortProcess(String containerId, Long processInstanceId) {
		try {
			proCtrl.abortProcessInstance(containerId, processInstanceId);
		}catch(rx.exceptions.OnErrorNotImplementedException e) {
			System.out.println("error");
		}catch(KieServicesHttpException e) {
			System.out.println("error");
		}
		catch(IllegalArgumentException e) {}
	}
	
	public TaskInstance kieClientTaskInfo(String containerId, Long taskId, Long instanceProcessId) {
		System.out.println(queryTask.getTaskInstance(containerId, taskId));
		System.out.println();
		System.out.println("Active Node Instance -----------------"
				+ proCtrl.findActiveNodeInstances(containerId, instanceProcessId, 0, 100));
		System.out.println();
		System.out.println(
				"Node Instance -----------------" + proCtrl.findNodeInstances(containerId, instanceProcessId, 0, 100));
		System.out.println();
		System.out
				.println("Node Instance -----------------" + proCtrl.getUserTaskDefinitions(containerId, "evaluation"));
		System.out.println();
		System.out.println(
				"Node Instance -----------------" + proCtrl.getProcessInstance(containerId, instanceProcessId));
		System.out.println();
		return queryTask.getTaskInstance(containerId, taskId);
	}

	public ProcessServicesClient getProCtrl() {
		return proCtrl;
	}

	public void setProCtrl(ProcessServicesClient proCtrl) {
		this.proCtrl = proCtrl;
	}

	public List<NodeInstance> getActiveNodeInstances(String containerId, Long instanceProcessId) {
		return proCtrl.findActiveNodeInstances(containerId, instanceProcessId, 0, 100);
	}

	public void kieClientStartTask(String containerId, String processId, Map<String, Object> params, Long taskId,
			String author) {
		queryTask.completeAutoProgress(containerId, taskId, author, params);
		System.out.println(queryTask.getTaskInstance(containerId, taskId));
	}

	public void startWorkItem(String containerId,Long processInstanceId, Map<String, Object> params, Long itemId) {
		proCtrl.completeWorkItem(containerId, processInstanceId, itemId, params);
	}
	public void listProcesses(String container) {
		QueryServicesClient queryClient = kieServicesClient.getServicesClient(QueryServicesClient.class);
		List<ProcessDefinition> findProcessesByContainerId = queryClient.findProcessesByContainerId(container, 0, 1000);
		queryClient.findProcesses(0, 10);
		System.out.println(queryClient.findProcesses(0, 1000));
		for (ProcessDefinition def : findProcessesByContainerId) {
			System.out.println(def.getName() + " - " + def.getId() + " v" + def.getVersion() + " " + " ");
		}
	}

}
