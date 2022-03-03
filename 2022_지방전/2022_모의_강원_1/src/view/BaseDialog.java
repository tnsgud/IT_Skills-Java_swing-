package view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import db.DB;

public class BaseDialog extends JDialog {
	JPanel root, n, w, c, e, s;

	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setSize(w, h);
		setModal(true);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		DB.execute("use busticketbooking");
	}
}
