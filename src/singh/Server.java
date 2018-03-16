package singh;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.trolltech.qt.gui.QApplication;

/**
 * SimpleChat - Server verwaltet die Clients mittels hilfe von Threads.
 * 
 * @author Gurparkash
 * @version 15-03-18
 */
public class Server {
	private static int connectionid;// id fuer jeweiligen verbundenen client
	private int port;// Portadresse
	private boolean keepGoing;
	private Ui_ChatServer sg;// ServerGUI
	private ArrayList<ClientThread> al; // Collection fuer Clients
	public Map<String, Integer> usermap;

	/**
	 * Konstruktor
	 */

	public Server(int port, Ui_ChatServer sg, Map<String, Integer> usermap) {
		this.port = port;
		this.sg = sg;
		al = new ArrayList<ClientThread>();
		this.usermap = new HashMap<String, Integer>();
	}

	/*
	 * Schickt Nachrichten alle Clients.
	 */
	public synchronized void broadcast(String msg) {
		// nachricht in die GUI
		QApplication.invokeLater(new Runnable() {
			@Override
			public void run() {
				sg.messages.addItem(msg);
			}
		});
		// rueckwaerts loop falls jemand dc ist und wir ihn kicken müssen
		for (int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// versucht nachricht an client zu schicken falls false remove
			if (!ct.writeMsg(msg))
				al.remove(i);
		}

	}

	/**
	 * Entfernt einen Client vom Server
	 * 
	 * @param id
	 *            die ID des Client's der entfernt wird.
	 */

	public synchronized void remove(int id) {
		for (int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if (ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}

	/**
	 * Stoppt den Server
	 */
	protected void stop() {
		keepGoing = false;
	}

	/**
	 * Startet den Server
	 */
	public void start() {
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (keepGoing) {
				// wartet auf einen client
				Socket socket = serverSocket.accept();
				if (!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);
				al.add(t);// fügt client in die ArrayList hinzu
				t.start();// startet Thread
			}
			serverSocket.close();

			for (int i = 0; i < al.size(); ++i) {
				ClientThread ct = al.get(i);
				ct.close();
			}
		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Thread fuer die jeweiligen Clients.
	 * 
	 * @author Gurparkash Singh
	 *
	 */
	public class ClientThread extends Thread {
		Socket socket;
		int id;
		String cm;// ChatMessage
		ObjectOutputStream sOutput;
		ObjectInputStream sInput;
		String username;

		/**
		 * Konstruktor
		 * 
		 * @param socket
		 */
		public ClientThread(Socket socket) {
			id = ++connectionid;// ++ Berechnung vorher
			this.socket = socket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			// Hier nimmt der Server den Username an.
			try {
				username = (String) sInput.readObject();
				// broadcast das ein benuter den Chatserver betrettem hat
				broadcast("Der Benutzer " + username + " ist dem Chatserver beigetreten!");
				usermap.put(username, connectionid);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			/*
			 * Fuegt den Namen des Clients in die GUI(Liste der verbunden
			 * Clients) invokeLater nötig, weil in einem Thread sonst die GUI
			 * nicht ändern kann.
			 */
			QApplication.invokeLater(new Runnable() {
				@Override
				public void run() {
					sg.clients.addItem(username);
				}
			});
			/*
			 * Hoert dem Client zu und sendet im Broadcast weiter.
			 */
			boolean keepGoing = true;
			while (keepGoing) {
				try {

					cm = (String) sInput.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				// sendet an alle anderen
				broadcast(cm);
			}
			// remove client id
			remove(id);
			close();
		}

		/**
		 * schließen von Input/Output und socket des Clients
		 */
		private void close() {
			// TODO Auto-generated method stub

			try {
				if (sOutput != null)
					sOutput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (sInput != null)
					sInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Sendet die Nachrichten durch den Outputstream
		 */
		private boolean writeMsg(String msg) {
			if (!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}

}
