package tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
	default DefaultTableModel model(String col[]) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		var dtcr = new DefaultTableCellRenderer();

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setAutoCreateRowSorter(true);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	default JPopupMenu pop(JComponent com, int lno) {
		var menu = new JPopupMenu();
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));
		var scr1 = new JScrollPane(p1);
		var scr2 = new JScrollPane(p2);

		menu.setLayout(new GridLayout(1, 0));
		menu.add(scr1);
		menu.add(scr2);

		var rs = rs("select * from location ");
		try {
			while (rs.next()) {
				var btn = new JButton(rs.getString(2));
				btn.setName(rs.getString(1));
				btn.addActionListener(a -> {
					p2.removeAll();

					var rs1 = rs("select * from location2 where location_no=?",
							toInt(((JButton) a.getSource()).getName()));
					try {
						while (rs1.next()) {
							var b = new JButton(rs1.getString(2));
							b.addActionListener(e -> {
								if (com instanceof JTextField) {
									((JTextField) com).setText(a.getActionCommand() + " " + e.getActionCommand());
								} else {

								}
							});
							b.setName(rs1.getString(1));
							p2.add(b);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					p2.repaint();
					p2.revalidate();
				});
				if (lno == rs.getInt(1)) {
					btn.doClick();
				}
				p1.add(btn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sz(menu, 250, 250);

		return menu;
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

	default ResultSet rs(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
			return DB.ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	default String getOne(String sql, Object... obj) {
		var rs = rs(sql, obj);
		try {
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	default JLabel img(String a, int w, int h) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + a).getScaledInstance(w,
				h, Image.SCALE_SMOOTH)));
	}

	default JLabel img(String a) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + a)));
	}

	default JLabel img(byte[] b, int w, int h) {
		return new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().createImage(b).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "안내", JOptionPane.INFORMATION_MESSAGE);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "오류", JOptionPane.ERROR_MESSAGE);
	}

	default void createV() {
		execute("drop view if exists v1");
		execute("CREATE VIEw v1 AS SELECT s.no AS sno, l11.no AS l11no,l11.name AS l11name,l21.no AS l21no,l21.name AS l21name,l21.location_no AS l21lno,l12.no AS l12no,l12.name AS l12name,l22.no AS l22no,l22.name AS l22name,l22.location_no AS l22lno FROM schedule s, location l11, location2 l21, location l12, location2 l22 WHERE l11.no = l21.location_no AND l12.no = l22.location_no AND l21.no = s.departure_location2_no AND l22.no = s.arrival_location2_no");
	}

	default void setUI(Object key, Object value) {
		UIManager.getLookAndFeelDefaults().put(key, value);
	}

	default void setTheme(JFrame j, boolean mode) {
		var back = mode ? Color.white : Color.DARK_GRAY;
		var fore = mode ? Color.black : Color.white;

		var defaults = UIManager.getLookAndFeelDefaults();
		synchronized (defaults) {
			for (var key : defaults.keySet()) {
				if (key.toString().contains("fore")) {
					setUI(key, new ColorUIResource(fore));
				}

				if (key.toString().contains("back")) {
					setUI(key, new ColorUIResource(back));
				}

				if (key.toString().contains("asc")) {
					setUI(key, sicon("↑"));
				}

				if (key.toString().contains("desc")) {
					setUI(key, sicon("↓"));
				}

				if (key.toString().contains("title")) {
					setUI(key, new ColorUIResource(fore));
				}
			}
		}

		setUI("OptionPane.okButtonText", "확인");
		setUI("OptionPane.cancelButtonText", "취소");

		SwingUtilities.updateComponentTreeUI(j);
	}

	default Icon sicon(String q) {
		var icon = new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(q, x, y + 4);
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

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		l.setOpaque(true);
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, Font.BOLD, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);

		b.setBackground(new Color(0, 123, 255));
		b.setForeground(Color.white);

		return b;
	}

	default JButton themeBtn(JFrame j) {
		var b = btn("테마", a -> {
			BaseFrame.theme = !BaseFrame.theme;
			setTheme(j, BaseFrame.theme);

			((JButton) a.getSource()).setBackground(BaseFrame.theme ? Color.DARK_GRAY : Color.white);
			((JButton) a.getSource()).setForeground(BaseFrame.theme ? Color.white : Color.black);
		});
		b.setBackground(BaseFrame.theme ? Color.DARK_GRAY : Color.white);
		b.setForeground(BaseFrame.theme ? Color.white : Color.black);

		return b;
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default int toInt(Object p) {
		var s = p.toString().trim().replaceAll("[^0-9]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
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
