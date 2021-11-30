package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import db.DB;
import model.User;

public class BaseFrame extends JFrame {
	JPanel n, e, c, s, w;
	static int isLoginned = 0;
	static User user;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		DB.execute("use adventure");
	}

	static int toInt(Object p) {
		try {
			var str = p.toString().replaceAll("[^0-9]", "");
			return str.isEmpty() ? -1 : Integer.parseInt(str);
		} catch (Exception e) {
			return -1;
		}
	}

	static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.ERROR_MESSAGE);
	}

	static JLabel lbl(String c, int a) {
		return lbl(c, a, 12, Font.PLAIN);
	}

	static JLabel lblB(String c, int a, int sz) {
		return lbl(c, a, sz, Font.BOLD);
	}

	static JLabel lbl(String c, int a, int sz, int f) {
		var l = new JLabel(c, a);
		l.setFont(new Font("", f, sz));
		return l;
	}

	static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	static JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
	}

	static ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	static ImageIcon img(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
	}

	class Before extends WindowAdapter {
		BaseFrame base;

		public Before(BaseFrame base) {
			this.base = base;
			base.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			base.setVisible(true);
		}
	}

	class IconLabel extends JLabel {
		String title;

		public IconLabel(String t) {
			super("", img("./datafiles/아이콘.png", 25, 30), 0);
			title = t;
		}

		public IconLabel(String t, boolean isEnable) {
			this(t);
			setEnabled(isEnable);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!isEnable) {
						eMsg("회원님이 이용할 수 없는 놀이기구 입니다.");
						return;
					}

					new Detail(t).addWindowListener(new Before(BaseFrame.this));
				}
			});
		}

		public IconLabel(String t, String fl) {
			this(t);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					new RideEdit(RideEdit.EDIT, t, fl, (Ride) BaseFrame.this)
							.addWindowListener(new Before(BaseFrame.this));
				}
			});
		}
	}
}
