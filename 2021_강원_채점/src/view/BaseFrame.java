package view;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool{
	public static boolean theme = true;
	public static int uno;
	JPanel n, w, c, e, s;
	
	public BaseFrame(int w, int h) {
		super("버스 에매");
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		execute("use busticketbooking");
		setTheme(this, theme);
	}
}
