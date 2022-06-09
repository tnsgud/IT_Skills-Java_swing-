package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

import tool.Tool;

public class MainFrame extends JFrame implements Tool{

	public MainFrame() {
		super("국삐");
		setSize(1000, 600);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/covid.png"));

		execute("use covid");
		
		var de = UIManager.getLookAndFeelDefaults();
		for (var p : de.keySet()) {
			if (p.toString().contains("back")) {
				de.put(p, Color.white);
			}
		}
	}

	void swapPage(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		revalidate();
		repaint();
	}
}
