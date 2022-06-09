package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {
	public MainFrame() {
		super("국삐");
		setSize(1000, 600);
		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setLayout(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/map.jpg"));
		
		var de = UIManager.getLookAndFeelDefaults();
		for (var prop : de.keySet()) {
			if (prop.toString().contains("back")) {
				de.put(prop, new ColorUIResource(Color.WHITE));
			}
		}
	}

	public void swap(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		getContentPane().revalidate();
		getContentPane().repaint();
	}
}
