package tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
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
		var t = new JTable(m);

		var dtcr = new DefaultTableCellRenderer();

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
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

	default ArrayList<ArrayList<String>> toArray(String sql, Object... obj) {
		var arr = new ArrayList<ArrayList<String>>();
		var rs = rs(sql, obj);
		var i = 0;

		try {
			while (rs.next()) {
				arr.add(new ArrayList<String>());
				for (int j = 0; j < rs.getMetaData().getColumnCount(); j++) {
					arr.get(i).add(rs.getString(j+1));
				}
				i++;
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

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JLabel img(String p, int w, int h) {
		return new JLabel(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./Datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JLabel lbl(String c, int a, int s) {
		return lbl(c, a, Font.BOLD, s);
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
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
