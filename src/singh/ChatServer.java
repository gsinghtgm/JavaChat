package singh;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Der Chat Server.
 * 
 * @author Gurparkash
 *
 */
public class ChatServer {
	private static int connectionid; // ID für den verbundenen Client
	private ArrayList<CThread> list; // Thread fuer den Client in einer
										// Collection
	private Ui_ChatServer ui; // GUI
	private int port; // Port
	private boolean status; // Status des Servers <True>Server On</True>
							// <False>Server Off</False>

	/**
	 * Konstruktor
	 * 
	 * @param port
	 * @param ui
	 */
	public ChatServer(int port, Ui_ChatServer ui) {
		this.ui = ui;
		this.port = port;
		list = new ArrayList<CThread>();
	}

	/**
	 * Eine Thread fuer jeden Client.
	 * 
	 * @author Gurparkash
	 * @version 19-02-2018
	 */
	class CThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
	}
}
