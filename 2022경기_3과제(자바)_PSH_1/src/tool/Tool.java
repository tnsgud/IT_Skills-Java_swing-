package tool;

import java.awt.Color;
import java.awt.Component;
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
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.GenreSelect;

public interface Tool {
	String[] g_genre = ",공포,RPG,레이싱,스포츠,시뮬레이션,액션,어드벤쳐,전략,슈팅".split(","),
			g_age = "전체이용가,12세 이상,15세 이상,18세 이상".split(","), g_gd = "일반,브론즈,실버,골드,플레티넘,다이아".split(",");
	Color back = new Color(51, 63, 112);

	default void execute(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default ArrayList<ArrayList<Object>> getRows(String sql, Object... obj) {
		var list = new ArrayList<ArrayList<Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
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

	default String getOne(String sql, Object... obj) {
		var rs = getRows(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0).toString();
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default String format(int n) {
		return new DecimalFormat("#,##0").format(n);
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default void opaque(JComponent c, boolean op) {
		for (var com : c.getComponents()) {
			if (com instanceof JComboBox || com instanceof JTextField || com instanceof JButton
					|| com instanceof CellRendererPane)
				continue;
			((JComponent) com).setOpaque(op);
			opaque((JComponent) com, op);
		}
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(Object o, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) o).getScaledInstance(w, h, 4));
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setForeground(Color.white);
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
				if (e.getButton() == 1) {
					i.run(e);
				}
			}
		});
		return l;
	}

	default JLabel lblImg(String c, int a, String p, int w, int h, Invoker i) {
		try {
			var icon = ImageIO.read(new File(p));
			var l = new JLabel("<html>" + c, a) {
				void setColor(int r, int g, int b) {
					for (int i = 0; i < icon.getWidth(); i++) {
						for (int j = 0; j < icon.getHeight(); j++) {
							var p = icon.getRaster().getPixel(i, j, (int[]) null);
							p[0] = r;
							p[1] = g;
							p[2] = b;
							icon.getRaster().setPixel(i, j, p);
						}
					}
				}
			};

			l.setForeground(Color.white);
			l.setFont(new Font("맑은 고딕", 1, 25));
			l.setColor(255, 255, 255);
			l.setIcon(new ImageIcon(icon.getScaledInstance(w, h, 4)));

			l.setVerticalTextPosition(JLabel.BOTTOM);
			l.setHorizontalTextPosition(JLabel.CENTER);

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					l.setColor(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue());
					l.setIcon(new ImageIcon(icon.getScaledInstance(w, h, 4)));
					l.setCursor(new Cursor(12));
					l.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					l.setColor(255, 255, 255);
					l.setIcon(new ImageIcon(icon.getScaledInstance(w, h, 4)));
					l.repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						i.run(e);
					}
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
				new GenreSelect(genre).setVisible(true);
			}
		});

		return l;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setBackground(new Color(53, 167, 249));
		b.setForeground(Color.white);
		return b;
	}

	default DefaultTableModel model(String col[]) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return JLabel.class;
			}
		};
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		var r = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				return value instanceof JLabel ? (JLabel) value
						: super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setDefaultRenderer(JLabel.class, r);

		return t;
	}
}
