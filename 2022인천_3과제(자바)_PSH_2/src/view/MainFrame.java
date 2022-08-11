package view;

import java.awt.Color;

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
		setIconImage(getIcon("./datafiles/Covid.png", 100, 100).getImage());

		var de = UIManager.getLookAndFeelDefaults();
		for (var k : de.keySet()) {
			if (k.toString().contains("back")) {
				de.put(k, new ColorUIResource(Color.white));
			}
		}

		execute("use covid");
	}

	void swap(BasePage p) {
		getContentPane().removeAll();
		add(p);
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
