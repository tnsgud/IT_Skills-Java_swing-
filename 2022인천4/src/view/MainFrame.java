
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
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/covid.png"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(3);

		var defaults = UIManager.getLookAndFeelDefaults();
		for (var key : defaults.keySet()) {
			if (key.toString().contains("back")) {
				defaults.put(key, new ColorUIResource(Color.white));
			}
		}
		
		execute("use covid");
	}

	void swapPage(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		repaint();
		revalidate();
	}
}