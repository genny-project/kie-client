package droolsclient;

import java.util.Arrays;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import io.vertx.core.Vertx;


public class Client {
	private static  final String hostip = System.getenv("HOSTIP");
	private static final String URL = "http://"+hostip+":8480/kie-server/services/rest/server";
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
	
	public ServiceResponse<ExecutionResults> executeCommands() {
	    System.out.println("== Sending commands to the server ==");
	    RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
	    KieCommands commandsFactory = KieServices.Factory.get().getCommands();
	    Command<?> insert = commandsFactory.newInsert("","pe");
	    Command<?> fireAllRules = commandsFactory.newFireAllRules();
	    Command<?> batchCommand = commandsFactory.newBatchExecution(Arrays.asList(insert, fireAllRules));
	    ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults("hello", batchCommand);
        return executeResponse;     
	}
	
	
}
