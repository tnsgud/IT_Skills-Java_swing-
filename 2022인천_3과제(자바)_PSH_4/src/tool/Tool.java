package tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	Color blue = new Color(0, 123, 255);

	default ArrayList<ArrayList<Object>> getRows(String sql, Object... objects) {
		var list = new ArrayList<ArrayList<Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < objects.length; i++) {
				DB.ps.setObject(i + 1, objects[i]);
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

	default void execute(String sql, Object... objects) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < objects.length; i++) {
				DB.ps.setObject(i + 1, objects[i]);
			}
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default String getOne(String sql, Object... objects) {
		var rs = getRows(sql, objects);
		return rs.isEmpty() ? "" : rs.get(0).get(0).toString();
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default <T extends JComponent> T event(T c, Consumer<MouseEvent> i) {
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

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default void op(JComponent jcom) {
		for (var com : jcom.getComponents()) {
			if (com instanceof JPanel || com instanceof JCheckBox) {
				((JComponent) com).setOpaque(false);
				op((JComponent) com);
			}
		}
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
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

	default JLabel lblHyp(String c, int a, int sz) {
		var l = lbl(c, a, 0, sz);
		l.setForeground(Color.orange);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				l.setText("<html><u>" + c);
				l.setCursor(new Cursor(12));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				l.setText(c);
				l.setCursor(new Cursor(0));
			}
		});
		return l;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setForeground(Color.white);
		b.setBackground(Color.orange);
		b.addActionListener(a);
		return b;
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(Object o, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) o).getScaledInstance(w, h, 4));
	}
	

	default DefaultTableModel model(String[] split) {
		return new DefaultTableModel(null, split) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}
	
	default JTable table(DefaultTableModel m) {
		var t = new JTable(m);
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
