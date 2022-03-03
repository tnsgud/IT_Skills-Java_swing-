package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
	default JPopupMenu showLocation(JComponent c, int no) {
		var menu = new JPopupMenu();
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		sz(menu, 250, 250);

		menu.setLayout(new GridLayout(1, 0));

		menu.add(new JScrollPane(p1));
		menu.add(new JScrollPane(p2));

		try {
			var rs = DB.rs("select no, name from location");
			while (rs.next()) {
				int lno = rs.getInt(1);
				var btn = btn(rs.getString(2), a1 -> {
					p2.removeAll();

					var rs1 = DB.rs("select name from location2 where location_no=?", lno);
					try {
						while (rs1.next()) {
							p2.add(btn(rs1.getString(1), a2 -> {
								if (c instanceof JTextField) {
									((JTextField) c).setText(a1.getActionCommand() + " " + a2.getActionCommand());
								} else if (c instanceof JTable) {
									((JTable) c).setValueAt(a1.getActionCommand() + " " + a2.getActionCommand(),
											((JTable) c).getSelectedRow(), ((JTable) c).getSelectedColumn());
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

		return menu;
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
			BaseFrame.setTheme(f);
		});

		b.setBackground(BaseFrame.theme ? Color.darkGray : Color.white);
		b.setForeground(BaseFrame.theme ? Color.white : Color.BLACK);

		return b;
	}

	default JLabel lbl(String c, int a, int f, int s) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", f, s));
		return l;
	}

	default JLabel lbl(String c, int a, int s) {
		return lbl(c, a, Font.BOLD, s);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default ImageIcon img(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + path));
	}

	default ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + path).getScaledInstance(w, h,
				Image.SCALE_SMOOTH));
	}

	default ImageIcon img(byte b[], int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(b).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
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
		var s = p.toString().replace("[^0-9]", "");
		return s.trim().isEmpty() ? -1 : Integer.parseInt(s);
	}
}
