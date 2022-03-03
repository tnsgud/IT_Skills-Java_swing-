package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import db.DB;

public class BaseFrame extends JFrame {
	public static int no;
	public static LocalDate now = LocalDate.parse("2021-10-06");
	public static boolean theme = true;
	JPanel n, w, c, e, s;

	public BaseFrame(int w, int h) {
		super("버스예매");
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setTheme(this);
		DB.execute("use busticketbooking");
	}

	static Object getUI(String txt1, Color value) {
		return UIManager.getLookAndFeelDefaults().put(txt1, new ColorUIResource(value));
	}

	public static void setTheme(BaseFrame f) {
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "확인");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "취소");

		var back = theme ? Color.WHITE : Color.DARK_GRAY;
		var fore = theme ? Color.DARK_GRAY : Color.WHITE;

		getUI("TextField.background", back);
		getUI("TextField.foreground", fore);
		getUI("PasswordField.background", back);
		getUI("PasswordField.foreground", fore);
		getUI("OptionPane.background", back);
		getUI("OptionPane.foreground", fore);
		getUI("Label.background", back);
		getUI("Label.foreground", fore);
		
		getUI("Panel.background", back);
		getUI("TabbedPane.selectedForeground", fore);
		getUI("TitledBorder.titleColor", fore);

		UIManager.getLookAndFeelDefaults().put("Table.ascendingSortIcon", sIcon("↑"));
		UIManager.getLookAndFeelDefaults().put("Table.descendingSortIcon", sIcon("↓"));

		SwingUtilities.updateComponentTreeUI(f);
	}

	static Icon sIcon(String txt) {
		Icon icon = new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(txt, x, y + 4);

			}

			@Override
			public int getIconWidth() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getIconHeight() {
				// TODO Auto-generated method stub
				return 0;
			}
		};

		return icon;
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			b.setVisible(true);
		}
	}
}