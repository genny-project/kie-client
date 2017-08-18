package life.genny.kieclient;

import java.util.Map;

public class WorkItemType implements CommandTypes{

	KieClient  clientKie =  KieClient.getKieClient();
	

	@Override
	public void start(String containerId, String processInstanceId, Map<String,Object>params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skipt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abort(String containerId, Long processInstanceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void find() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signal() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void get() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(String containerId, Long processInstanceId, Map<String, Object> params, Long itemId) {
		// TODO Auto-generated method stub
		clientKie.startWorkItem(containerId, processInstanceId, params, itemId);
	}

}
