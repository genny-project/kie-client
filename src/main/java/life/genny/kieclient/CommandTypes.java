package life.genny.kieclient;

import java.util.Map;

public interface CommandTypes {
	
	void start(String container, String processId, Map<String,Object>params) ;
	void start(String containerId, Long processInstanceId, Map<String, Object> params, Long itemId) ;

	void skipt();
	void abort(String containerId, Long processInstanceId) ;
	void find() ;
	void signal() ;
	void set() ;
	void get() ;
	void complete() ;
	

}
