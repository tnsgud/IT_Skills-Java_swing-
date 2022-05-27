package tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	default ArrayList<ArrayList<Object>> rs(String sql, Object... obj) {
		var arr = new ArrayList<ArrayList<Object>>();
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
				arr.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arr;
	}

	default ArrayList<HashMap<String, Object>> map(String sql, Object... obj) {
		var arr = new ArrayList<HashMap<String, Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			var rs = DB.ps.executeQuery();
			while (rs.next()) {
				var map = new HashMap<String, Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					map.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));
				}
				arr.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arr;
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

	default DefaultTableModel model(String col[], boolean e) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return e;
			}
		};
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default ImageIcon img(String p, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	default ImageIcon img(String p) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/" + p));
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, Font.BOLD, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JLabel hyplbl(String c, int a, int st, int sz, Runnable r) {
		var l = lbl("<html><u>" + c, a, st, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1) {
					r.run();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				l.setCursor(new Cursor(Cursor.HAND_CURSOR));
				l.setText("<html><u>" + c);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				l.setText(c);
			}
		});
		return l;
	}

	default String getOne(String sql, Object... obj) {
		var rs = rs(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0) + "";
	}

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setForeground(Color.white);
		b.setBackground(Color.orange);
		b.addActionListener(a);
		return b;
	}
}
