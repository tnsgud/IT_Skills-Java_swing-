package tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
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

	default ArrayList<ArrayList<Object>> rs(String sql, Object... obj) {
		var row = new ArrayList<ArrayList<Object>>();

		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
			var rs = DB.ps.executeQuery();
			while (rs.next()) {
				var col = new ArrayList<Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					col.add(rs.getObject(i + 1));
				}
				row.add(col);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return row;
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

	default JLabel lbl(String c, int a, String f, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, st, sz));
		return l;
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

	default DefaultTableModel model(String col[]) {
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
		var d = new DefaultTableCellRenderer();
		t.setSelectionMode(0);
		d.setHorizontalAlignment(0);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(d);
		}

		return t;
	}

	default ImageIcon img(String p, int w, int h) {
		System.out.println("./Datafiles/" + p);
		return new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("./Datafiles/" + p).getScaledInstance(w, h,
				Image.SCALE_SMOOTH));
	}

	default ImageIcon img(String p) {
		return new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("./Datafiles/" + p));
	}

	default int toInt(Object p) {
		var s = p.toString().trim().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}
}
