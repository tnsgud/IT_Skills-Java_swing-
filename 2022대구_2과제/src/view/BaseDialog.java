package view;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import tool.Tool;


public class BaseDialog extends JDialog implements Tool {
	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		setModal(true);
		setLayout(new BorderLayout());
	}
}
