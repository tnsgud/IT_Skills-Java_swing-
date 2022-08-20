package tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import db.DB;

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

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default <T extends JComponent> T evt(T c, Consumer<MouseEvent> i) {
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1)
					return;
				i.accept(e);
			}
		});
		return c;
	}

	default void op(JComponent jcom) {
		for (var com : jcom.getComponents()) {
			if (com instanceof JPanel) {
				((JPanel) com).setOpaque(false);
				op((JPanel) com);
			}
		}
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

	default JLabel lbl(String c, int a, String f, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		return lbl(c, a, "맑은 고딕", st, sz);
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JLabel hylbl(String c, int a, int st, int sz) {
		return lbl(c, a, "HY헤드라인M", st, sz);
	}

	default JLabel lbl(String c, int a, int st, int sz, Consumer<MouseEvent> i) {
		var l = lbl(c, a, st, sz);
		return evt(l, i);
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(int no, int w, int h) {
		var rs = getRows("select b_img from base where b_no = ?", no).get(0).get(0);
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) rs).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(Object o, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) o).getScaledInstance(w, h, 4));
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setForeground(Color.white);
		b.setBackground(Color.GREEN.darker());
		return b;
	}

	default DefaultTableModel model(String col[]) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	default JTable table(DefaultTableModel m) {
		var t = new JTable(m) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				var comp = super.prepareRenderer(renderer, row, column);
				var data = m.getDataVector();

				try {
					var map = new HashMap<String, TreeSet<String>>();
					data.stream().filter(a -> data.stream().filter(x -> x.get(1).equals(a.get(1))).count() > 1)
							.forEach(e -> {
								if (!map.containsKey(e.get(1).toString())) {
									map.put(e.get(1).toString(), new TreeSet<>());
								}

								map.get(e.get(1).toString()).add(format(toInt(e.get(4))));
							});

					var tree = map.get(getValueAt(row, 1).toString());
					if (tree.iterator().hasNext() && getValueAt(row, 4)
							.equals(format(tree.stream().mapToInt(a -> toInt(a)).min().getAsInt()))) {
						comp.setFont(new Font("맑은 고딕", 2, 12));
						comp.setForeground(Color.blue);
					} else {
						comp.setFont(getFont());
						comp.setForeground(Color.black);
					}
				} catch (Exception e) {
					comp.setFont(getFont());
					comp.setForeground(Color.black);
				}

				return comp;
			}
		};
		var r = new DefaultTableCellRenderer();

		t.setSelectionMode(0);
		r.setHorizontalAlignment(0);

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}
		return t;
	}
}
