package view;

import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

import db.DB;

public class BaseDialog extends JDialog {
	JPanel n, w, c, e, s;

	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setSize(w, h);
		setModal(true);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		DB.execute("use busticketbooking");
	}
}
