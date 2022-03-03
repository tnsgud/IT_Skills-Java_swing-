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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
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
			e.printStackTrace();
			return null;
		}
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

	default void createV() {
		execute("drop view if exists v1");
		execute("create view v1 as select s.no as sno, l11.no as l11no, l11.name as l11name, l21.location_no as l21lno, l21.no as l21no, l21.name as l21name, l12.no as l12no, l12.name as l12name, l22.location_no as l22lno, l22.no as l22no, l22.name as l22name "
				+ "from schedule s, location l11, location l12, location2 l21, location2 l22 "
				+ "where s.departure_location2_no = l21.no and s.arrival_location2_no=l22.no and l11.no = l21.location_no and l12.no = l22.location_no");
	}

	default ArrayList<ArrayList<String>> toArrayList(String sql, Object... obj) {
		var rs = rs(sql, obj);
		var result = new ArrayList<ArrayList<String>>();

		try {
			while (rs.next()) {
				var row = new ArrayList<String>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getString(i + 1));
				}
				result.add(row);
			}

			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	default JPopupMenu showLocaltion(JComponent c, int no) {
		var m = new JPopupMenu();
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		sz(m, 250, 250);

		m.setLayout(new GridLayout(1, 0));

		m.add(new JScrollPane(p1));
		m.add(new JScrollPane(p2));

		try {
			var rs = rs("select no, name from location");
			while (rs.next()) {
				var lno = rs.getInt(1);
				var btn = btn(rs.getString(2), a -> {
					p2.removeAll();

					try {
						var rs1 = rs("select name from location2 where location_no=?", lno);
						while (rs1.next()) {
							p2.add(btn(rs1.getString(1), a2 -> {
								var txt = a.getActionCommand() + " " + a2.getActionCommand();
								if (c instanceof JTextField) {
									((JTextField) c).setText(txt);
								} else if (c instanceof JTable) {
									((JTable) c).setValueAt(txt, ((JTable) c).getSelectedRow(),
											((JTable) c).getSelectedColumn());
								}
							}));
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					p2.repaint();
					p2.revalidate();
				});

				if (rs.getInt(1) == no) {
					btn.doClick();
				}

				p1.add(btn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return m;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setBackground(new Color(0, 125, 255));
		b.setForeground(Color.white);
		b.addActionListener(a);

		return b;
	}

	default JButton themeBtn(BaseFrame f) {
		var b = btn("테마", a -> {
			BaseFrame.theme = !BaseFrame.theme;
			setTheme(f);
		});

		b.setBackground(BaseFrame.theme ? Color.DARK_GRAY : Color.white);
		b.setForeground(BaseFrame.theme ? Color.white : Color.black);

		return b;
	}

	default void ui(String key, Color value) {
		UIManager.getLookAndFeelDefaults().put(key, new ColorUIResource(value));
	}

	default void setTheme(BaseFrame f) {
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "확인");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "취소");

		var back = BaseFrame.theme ? Color.white : Color.darkGray;
		var fore = BaseFrame.theme ? Color.black : Color.white;

		for (var s : "TextField,PasswordField,OptionPane,Label".split(",")) {
			ui(s + ".background", back);
			ui(s + ".foreground", fore);
		}

		ui("Panel.background", back);
		ui("TabbedPane.selectedForeground", fore);
		ui("TitleBorder.titleColor", fore);

		UIManager.getLookAndFeelDefaults().put("Table.ascendingSortIcon", sIcon("↑"));
		UIManager.getLookAndFeelDefaults().put("Table.desceningSortIcon", sIcon("↓"));

		SwingUtilities.updateComponentTreeUI(f);
	}

	default Icon sIcon(String string) {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(string, x, y + 4);
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
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int s) {
		return lbl(c, a, Font.BOLD, s);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JLabel img(String path) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + path)));
	}

	default JLabel img(String path, int w, int h) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + path)
				.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
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

	default DefaultTableModel model(String[] col) {
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
		t.setAutoCreateRowSorter(true);
		t.setSelectionMode(0);
 
		dtcr.setHorizontalAlignment(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		
		return t;
	}
	
	default int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9]", "");
		return s.trim().isEmpty() ? -1 : Integer.parseInt(s);
	}
}
