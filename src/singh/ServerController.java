package singh;

import java.util.Map;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;

/**
 * SimpleChat - Server Controller.
 * 
 * @author Gurparkash
 * @version 15-03-18
 */
public class ServerController {
	public Ui_ChatServer ui;
	static Map<String, Integer> usermap;

	public static void main(String[] args) {
		Ui_ChatServer ui = new Ui_ChatServer();
		ui.server = new Server(5050, ui, usermap);
		ServerRun run = new ServerRun(ui.server, ui,usermap);
		run.start();
		QApplication.initialize(args);
		QDialog d = new QDialog();
		ui.setupUi(d);
		d.show();
		QApplication.execStatic();
	}

}
