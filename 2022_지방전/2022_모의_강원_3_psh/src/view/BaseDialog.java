package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class BaseDialog extends JDialog implements Tool{
	JPanel n, w, c, e, s;
	
	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setSize(w, h);
		setModal(true);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		execute("use busticketbooking");
	}
}
