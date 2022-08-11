package tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import db.DB;
import view.GenreSelect;

public interface Tool {
	default ArrayList<ArrayList<Object>> getRows(String sql, Object... obj) {
		var list = new ArrayList<ArrayList<Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			var rs = DB.ps.executeQuery();
			while (rs.next()) {
				var row = new ArrayList<>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				list.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	default void execute(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default String getOne(String sql, Object... obj) {
		var rs = getRows(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0).toString();
	}

	default void createV() {
		execute("drop view if exists v1");
		execute("create view v1 as select g.*, ifnull(round(avg(r_score), 1), 0) rate, format(g_price - (g_price * g_sale * 0.01), '#,##0') g_dcprice from game g, review r where g.g_no = r.g_no group by g.g_no");
		execute("drop view if exists v2");
		execute("create view v2 as select s.*, g_no, i_name, i_img from storage s left join market m on s.s_no = m.s_no inner join item i on s.i_no = i.i_no where m.m_no is null");
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default String format(int n) {
		return new DecimalFormat("#,##0").format(n);
	}

	default void op(JComponent comp) {
		for (var com : comp.getComponents()) {
			if (com instanceof JPanel) {
				((JPanel) com).setOpaque(false);
				op((JPanel) com);
			}
		}
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	interface Invoker {
		void run(MouseEvent e);
	}

	default JLabel lbl(String c, int a, int st, int sz, Invoker i) {
		var l = lbl(c, a, st, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1)
					return;

				i.run(e);
			}
		});
		return l;
	}

	default JLabel lblIcon(String c, String p, int w, int h, Invoker i) {
		try {
			var img = ImageIO.read(new File(p));
			var l = new JLabel(c, new ImageIcon(img.getScaledInstance(w, h, 4)), 0) {
				void setColor(int r, int g, int b) {
					for (int i = 0; i < img.getWidth(); i++) {
						for (int j = 0; j < img.getHeight(); j++) {
							var p = img.getRaster().getPixel(i, j, (int[]) null);

							p[0] = r;
							p[1] = g;
							p[2] = b;

							img.getRaster().setPixel(i, j, p);
						}
					}
				}
			};
			l.setColor(255, 255, 255);
			l.setForeground(Color.white);
			l.setVerticalTextPosition(3);
			l.setHorizontalTextPosition(0);

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					l.setColor(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue());
					l.setIcon(new ImageIcon(img.getScaledInstance(w, h, 4)));
					l.setCursor(new Cursor(12));
					l.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					l.setColor(255, 255, 255);
					l.setIcon(new ImageIcon(img.getScaledInstance(w, h, 4)));
					l.repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() != 1)
						return;

					i.run(e);
				}
			});
			return l;
		} catch (IOException e) {
			return null;
		}
	}

	default JLabel lblAdd(ArrayList<String> genre) {
		var l = new JLabel(getIcon("./datafiles/기본사진/10.png", 30, 30));
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1)
					return;
				new GenreSelect(genre).setVisible(true);
			}
		});
		return l;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setBackground(new Color(0, 150, 255));
		return b;
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(Object o, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) o).getScaledInstance(w, h, 4));
	}
}
