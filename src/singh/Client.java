package singh;

import java.net.*;

import java.io.*;

import java.util.*;

import com.trolltech.qt.gui.QApplication;

/**
 * SimpleChat - Client verbindet mit dem Server und kann Nachrichten schreiben.
 * 
 * @author Gurparkash
 * @version 15-03-18
 *
 */
public class Client {
	private ObjectInputStream sInput; // lesen vom socket
	private ObjectOutputStream sOutput; // schreiben auf socket
	private Socket socket;
	private Ui_ChatClient cg; // gui
	private String host; // server ip
	private int port; // server port

	/**
	 * Konstruktor
	 * 
	 * @param host
	 * @param port
	 * @param cg
	 */
	public Client(String host, int port, Ui_ChatClient cg) {
		this.host = host;
		this.port = port;
		this.cg = cg;
	}

	/**
	 * Starten den Client, Listener und fragt nach dem Benutzernamen.
	 * 
	 * @return
	 */
	public boolean start() {
		try {// server verbindung aufbauen trial
			socket = new Socket(host, port);
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			QApplication.invokeLater(new Runnable() {
				@Override
				public void run() {
					cg.messages.addItem("Bitte geben Sie ihren Benutername ein!");
				}
			});
		} catch (Exception ec) {
			System.err.println(ec);
			return false;
		}
		new Listener().start();

		return true;
	}

	/**
	 * Versendet die Nachricht.
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * Zum schließen der Verbindung
	 */
	public void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		}
		try {
			if (sOutput != null)
				sOutput.close();
		}

		catch (Exception e) {
		}
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Listener Klasse, hört dem Server zu und schreib eingehnde Nachrichten in
	 * die GUI.
	 * 
	 * @author Gurparkash
	 *
	 */
	class Listener extends Thread {
		public void run() {
			while (true) {
				try {
					String msg = (String) sInput.readObject();
					QApplication.invokeLater(new Runnable() {
						@Override
						public void run() {
							cg.messages.addItem(msg);
						}
					});
				} catch (IOException e) {
					break;

				} catch (ClassNotFoundException e2) {

				}

			}

		}

	}

}
