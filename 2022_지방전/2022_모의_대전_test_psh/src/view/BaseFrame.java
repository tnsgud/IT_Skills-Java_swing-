package view;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool{
	static int uno = 0;
	static boolean isLogin = false;
	static int pno = 0;
	JPanel n, w, c, e, s;
	
	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./Datafiles/오렌지.jpg"));
		execute("use 2021전국");
	}
}
