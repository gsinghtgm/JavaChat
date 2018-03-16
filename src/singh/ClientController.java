package singh;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;
/**
 * SimpleChat - Client Controller.
 * @author Gurparkash
 * @version 15-03-18
 */
public class ClientController {
	public static void main(String[] args) {
		QApplication.initialize(args);
		QDialog d = new QDialog();
		Ui_ChatClient ui = new Ui_ChatClient();
		ui.client = new Client("localhost", 5050, ui);
		ui.setupUi(d);
		d.show();
		ui.client.start();
		QApplication.execStatic();
	}
}
