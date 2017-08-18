package life.genny.kieclient;

public enum BusinessProcessType {

	PROCESS("Process"),
	WORKITEM("Workitem"),
	NODE("Node"),
	USERTASK("Usertask"),
	SERVICETASK("Servicetask"),
	ASSOCIATEENTITY("Associateentity"),
	VARIABLES("Variable");
	
	public String getDescription() {
		return description;
	}

	private final String description;
	
	BusinessProcessType (String desc){
		description = desc.toLowerCase();
	}
}
