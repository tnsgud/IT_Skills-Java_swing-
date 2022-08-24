package view;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {
	@Override
	public void repaint() {
		op((JComponent) getContentPane());
		super.repaint();
	}

	public MainFrame() {
		super("국삐");
		setSize(1000, 600);
		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);
		getContentPane().setBackground(Color.white);
		setIconImage(getIcon("./datafiles/Covid.png", 100, 100).getImage());
		execute("use covid");
	}

	void swap(BasePage b) {
		getContentPane().removeAll();
		add(b);
		repaint();
		revalidate();
	}
}
