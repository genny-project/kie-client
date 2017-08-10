package life.genny.kieclient;

import java.util.Map;

public interface CommandTypes {
	
	void run(String container, String processId, Map<String,Object>params) ;
	void skipt();
	void abort() ;
	void find() ;
	void signal() ;
	void set() ;
	void get() ;
	void complete() ;
	

}
