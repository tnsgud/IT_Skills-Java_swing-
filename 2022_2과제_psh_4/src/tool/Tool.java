package tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import db.DB;

public interface Tool {
	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default JLabel lbl(String c, int a, String f, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, st, sz));
		return l;
	}

	default int toInt(Object o) {
		var s = o.toString().trim().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default JLabel lblH(String c, int a, int st, int sz) {
		return lbl(c, a, "HY헤드라인M", st, sz);
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, "", 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, "", 0, 12);
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default ArrayList<ArrayList<Object>> rs(String sql, Object... obj) {
		var re = new ArrayList<ArrayList<Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
			var rs = DB.ps.executeQuery();
			while (rs.next()) {
				var row = new ArrayList<Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getObject(i + 1));
				}
				re.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return re;
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

	default DefaultTableModel model(String[] col) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	default JTable table(DefaultTableModel m, String id) {
		var t = new JTable(m) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				var c = super.prepareRenderer(renderer, row, column);

				if (!id.isEmpty() && getValueAt(row, 2).equals(id)) {
					c.setBackground(Color.blue);
					c.setForeground(Color.white);
				} else if (!id.isEmpty()) {
					c.setBackground(null);
					c.setForeground(Color.black);
				}
				return c;
			}
		};
		var r = new DefaultTableCellRenderer();
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(0);
		r.setHorizontalAlignment(0);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}
		return t;
	}

	default ImageIcon img(String p, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./Datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}
}
