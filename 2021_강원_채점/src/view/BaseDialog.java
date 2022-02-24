package view;

import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class BaseDialog extends JDialog implements Tool {
	JPanel c, w, e, s, n;

	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		setModal(true);
		setSize(w, h);
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());
		execute("use busticketbooking");
	}
}
