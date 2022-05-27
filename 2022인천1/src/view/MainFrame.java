package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {
	public MainFrame() {
		super("국삐");

		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setLayout(null);
		setIconImage(img("Covid.png").getImage());
	}
	
	public void swap(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		getContentPane().revalidate();
		getContentPane().repaint();
	}
}
