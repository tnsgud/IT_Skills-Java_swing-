package view;

import java.awt.GridBagLayout;

import javax.swing.JDialog;

public class BaseDialog extends JDialog{
	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setModal(true);
		setSize(w, h);
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());
	}
}
