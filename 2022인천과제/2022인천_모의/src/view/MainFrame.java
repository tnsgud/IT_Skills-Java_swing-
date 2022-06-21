
package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {

	public MainFrame() {
		super("국삐");

		execute("use covid");

		setIconImage(img("./datafiles/covid.png", 10, 10).getImage());
		setSize(1000, 600);
		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);

		var defaults = UIManager.getLookAndFeelDefaults();
		for (var k : defaults.keySet()) {
			if (k.toString().contains("back")) {
				defaults.put(k, Color.white);
			}
		}
	}

	void swapPage(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		repaint();
		revalidate();
	}
}
