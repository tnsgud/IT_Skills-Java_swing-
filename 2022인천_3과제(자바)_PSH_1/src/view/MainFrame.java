package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {
	public MainFrame() {
		super("국삐");
		setSize(1000, 600);
		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/Covid.png"));

		var d = UIManager.getLookAndFeelDefaults();
		for (var k : d.keySet()) {
			if (k.toString().contains("back")) {
				d.put(k, new ColorUIResource(Color.white));
			}
		}

		execute("use covid");
	}
	
	void swap(BasePage p) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(p);
		repaint();
		revalidate();
	}
}
