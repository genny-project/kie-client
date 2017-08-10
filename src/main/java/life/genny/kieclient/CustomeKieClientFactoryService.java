package life.genny.kieclient;

public class CustomeKieClientFactoryService {
	
	public ClientServiceKie getClientKieService() {
		KieClient client = new KieClient();
//		client.initialize();
		return client;
	}

}
