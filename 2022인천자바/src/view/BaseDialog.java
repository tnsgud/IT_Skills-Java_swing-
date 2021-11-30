package view;

import java.awt.BorderLayout;

import javax.swing.JDialog;

public class BaseDialog extends JDialog {
	public BaseDialog(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setModal(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
	}
}
