package tool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	default ResultSet rs(String sql, Object... obj) throws SQLException {
		DB.ps = DB.con.prepareStatement(sql);
		for (int i = 0; i < obj.length; i++) {
			DB.ps.setObject(i + 1, obj[i]);
		}
		System.out.println(DB.ps);
		return DB.ps.executeQuery();
	}
	
	default void addRow(ArrayList<ArrayList<Object>> row, DefaultTableModel m) {
		m.setRowCount(0);
		for (var col : row) {
			m.addRow(col.toArray());
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
	
	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(0);
		var r = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JLabel) {
					return (JComponent) value;
				} else {
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			}
		};
		r.setHorizontalAlignment(0);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}
		return t;
	}
	
	default int toInt(Object o) {
		var s = o.toString().trim().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1:Integer.parseInt(s);
	}

	default void execute(String sql, Object... obj) throws SQLException {
		DB.ps = DB.con.prepareStatement(sql);
		for (int i = 0; i < obj.length; i++) {
			DB.ps.setObject(i + 1, obj[i]);
		}
		DB.ps.execute();
	}

	default String getOne(String sql, Object... obj) {
		try {
			var rs = rs(sql, obj);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

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

	default ArrayList<ArrayList<Object>> getResults(String sql, Object... obj) {
		var results = new ArrayList<ArrayList<Object>>();
		try {
			var rs = rs(sql, obj);
			while (rs.next()) {
				var col = new ArrayList<Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					col.add(rs.getObject(i + 1));
				}
				results.add(col);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}

	default JLabel lblH(String c, int a, int st, int sz) {
		return lbl(c, a, "HY헤드라인M", st, sz);
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, "", Font.BOLD, sz);
	}

	default ImageIcon img(String p, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default ImageIcon img(Object b, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[])b).getScaledInstance(w, h, Image.SCALE_SMOOTH));
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
}
