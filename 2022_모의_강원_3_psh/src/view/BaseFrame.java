package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	public static boolean theme = true;
	public static LocalDate now = LocalDate.of(2021, 10, 06);
	public static int uno = 0;
	JPanel n, w, c, e, s;

	public BaseFrame(int w, int h) {
		super("버스예매");
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setTheme(this);
		execute("use busticketbooking");
	}

	class Before extends WindowAdapter {
		BaseFrame b;
		
		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
}
