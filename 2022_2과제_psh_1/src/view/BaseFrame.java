package view;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	static DecimalFormat format = new DecimalFormat("#,##0");
	static LocalDate now = LocalDate.parse("2022-04-05");
	JPanel w, n, c, e, s;
	static int uno, tno;
	static String cno;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		execute("use 2022지방_1");
	}
}
