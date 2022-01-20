package view;

import java.awt.Dialog;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import db.DB;

public class BaseDialog extends JDialog {
	JPanel root, n, w, c, e, s;

	public BaseDialog(JFrame jf, String t, int w, int h) {
		super(jf, t, true);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		DB.execute("use busticketbooking");
	}
}
