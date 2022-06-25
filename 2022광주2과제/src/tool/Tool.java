
package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import db.DB;

public interface Tool {
//	drop view v1;
//	create view v1 as SELECT b.*, b_gun - IFNULL((SELECT SUM(bo_num) from borrow bo LEFT JOIN `return` r ON r.bo_no = bo.bo_no where bo.b_no = b.b_no GROUP BY r.r_no HAVING r.r_no IS NULL),0) as cnt FROM book b;
//	drop view overdue;
//	create view overdue as select bo.* from borrow bo inner join `return` r on bo.bo_no = r.bo_no where bo.bo_endate < r.r_date;
//	drop view bo;
//	create view bo as select bo.* from borrow bo left join `return` r on bo.bo_no = r.bo_no where r_no is null;
//	drop view re;
//	create view re as select bo.*, r_date from borrow bo inner join `return` r on bo.bo_no = r.bo_no;
//	select * from re
	
	default void createV() {
		execute("drop view v1");
		execute("", null);
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

	default int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void ㅑMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

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

	default String getOne(String sql, Object... obj) {
		var rs = getRows(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0) + "";
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

	interface Invoker {
		void run(MouseEvent e);
	}

	default JLabel lbl(String c, int a, int st, int sz, Invoker i) {
		var l = lbl(c, a, st, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1)
					i.run(e);
			}
		});
		return l;
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

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}
}
