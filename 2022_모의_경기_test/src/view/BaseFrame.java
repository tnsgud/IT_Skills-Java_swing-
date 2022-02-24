package view;

import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	public static boolean theme = true;
	static int uno;
	JPanel n, c, e, w, s;

	public BaseFrame(int w, int h) {
		super("버스 예매");
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setTheme(this, theme);
	}
}
