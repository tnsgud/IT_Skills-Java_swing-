package view;

import javax.swing.JFrame;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool{
	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(3);
	}
}
