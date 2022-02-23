package view;

import java.awt.Toolkit;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	static boolean isLogin = false;
	static int uno, pno;
	static DecimalFormat df = new DecimalFormat("#,##0");
	JPanel n, c, e, s, w;
	
	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./Datafiles/오렌지.jpg"));
		execute("use 2021전국");
	}
}
