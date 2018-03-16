package singh;

import java.util.Map;

/**
 * Thread fuer den Server
 * 
 * @author Gurparkash
 * @version 15-03-18
 */
public class ServerRun extends Thread {
	Server server;

	public ServerRun(Server server, Ui_ChatServer ui, Map<String, Integer> usermap) {
		this.server = new Server(5050, ui, usermap);
	}

	public void run() {
		server.start();
	}
}