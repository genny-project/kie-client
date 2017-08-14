package life.genny.kieclient;

import java.util.Map;

public class ProcessType implements CommandTypes {
	KieClient  clientKie =  KieClient.getKieClient();
	@Override
	public void run(String container, String processId, Map<String,Object>params) {
		clientKie.kieClientStartProcess(container, processId, params);
	}

	@Override
	public void skipt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abort() {
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

}
