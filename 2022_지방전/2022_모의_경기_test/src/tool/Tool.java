package tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import db.DB;
import view.BaseFrame;

public interface Tool {
	default void execute(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			var arr = Stream.of(obj).flatMap(a->Stream.of(a)).toArray();
			for (int i = 0; i < arr.length; i++) {
				DB.ps.setObject(i + 1, arr[i]);
			}
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default ResultSet rs(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			var arr = Stream.of(obj).flatMap(a->Stream.of(a)).toArray();
			for (int i = 0; i < arr.length; i++) {
				DB.ps.setObject(i+1, arr[i]);
			}
			return DB.ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	default String getOne(String sql, Object...obj) {
		var rs= rs(sql, obj);
		try {
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	default void createV() {
		execute("drop view if exists v1");
		execute("create view v1 as select s.no as sno, l11.no as l11no, l11.name as l11name, l21.no as l21no, l21.name as l21name, l21.location_no as l21lno, l12.no as l12no, l12.name as l12name, l22.no as l22no, l22.name as l22name, l22.location_no as l22lno from schedule s, location l11, location2 l21, location l12, location2 l22 where l11.no = l21.location_no and l21.no = l22.location_no and l21.no = s.departure_location2_no and l22.no = s.arrival_location2_no");
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "오류", JOptionPane.ERROR_MESSAGE);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "안내", JOptionPane.INFORMATION_MESSAGE);
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setOpaque(true);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, Font.BOLD, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default void setUI(Object key, Object value) {
		UIManager.getLookAndFeelDefaults().put(key, value);
	}

	default void setTheme(JFrame f, boolean mode) {
		var back = mode ? Color.white : Color.darkGray;
		var fore = mode ? Color.black : Color.white;

		var defaults = UIManager.getLookAndFeelDefaults();
		synchronized (defaults) {
			for (var key : defaults.keySet()) {
				if (key.toString().contains("back")) {
					setUI(key, new ColorUIResource(back));
				}
				if (key.toString().contains("fore")) {
					setUI(key, new ColorUIResource(fore));
				}
				if (key.toString().contains("asc")) {
					setUI(key, sIcon("↑"));
				}
				if (key.toString().contains("desc")) {
					setUI(key, sIcon("↓"));
				}
			}
		}

		setUI("OptionPane.okButtonText", "확인");
		setUI("OptionPane.cancelButtonText", "취소");

		SwingUtilities.updateComponentTreeUI(f);
	}

	default Icon sIcon(String i) {
		Icon icon = new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(i, x, y + 4);
			}

			@Override
			public int getIconWidth() {
				return 0;
			}

			@Override
			public int getIconHeight() {
				return 0;
			}
		};
		return icon;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setBackground(new Color(0, 123, 255));
		b.setForeground(Color.white);
		return b;
	}

	default JButton themeBtn(JFrame f) {
		var b = btn("테마", a -> {
			BaseFrame.theme = !BaseFrame.theme;
			setTheme(f, BaseFrame.theme);
		});

		b.setBackground(BaseFrame.theme ? Color.DARK_GRAY : Color.white);
		b.setForeground(BaseFrame.theme ? Color.white : Color.DARK_GRAY);

		return b;
	}

	default JLabel img(String p) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + p)));
	}

	default JLabel img(String p, int w, int h) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + p).getScaledInstance(w,
				h, Image.SCALE_SMOOTH)));
	}

	default JLabel img(byte[] b, int w, int h) {
		return new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().createImage(b).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default int toInt(Object p) {
		var s = p.toString().trim().replaceAll("[^0-9]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

}
