package Tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingContainer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
	default void addRow(DefaultTableModel m, String sql, Object... obj) {
		m.setRowCount(0);
		var rs = rs(sql, obj);
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default JLabel img(String path) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path)));
	}

	default JLabel img(String path, int w, int h) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path).getScaledInstance(w,
				h, Image.SCALE_SMOOTH)));
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

	default ResultSet rs(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			return DB.ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	default int toInt(Object o) {
		var s = o.toString().trim().replaceAll("[^0-9]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
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

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default JLabel lbl(String s, int a, String f, int st, int sz) {
		var l = new JLabel(s, a);
		l.setFont(new Font(f, st, sz));
		return l;
	}

	default JLabel lbl(String s, int a, int sz) {
		return lbl(s, a, "HY헤드라인M", Font.BOLD, sz);
	}

	default JLabel lblB(String s, int a, int sz) {
		return lbl(s, a, "", Font.BOLD, sz);
	}

	default JLabel lbl(String s, int a) {
		return lbl(s, a, "", Font.BOLD, 12);
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

		dtcr.setHorizontalAlignment(0);

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	default JTable blueTable(DefaultTableModel m, String id) {
		var t = new JTable(m) {
			public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row,
					int column) {
				var c = super.prepareRenderer(renderer, row, column);

				c.setBackground(null);
				c.setForeground(Color.black);
				
				if (id.equals(getValueAt(row, 2) + "")) {
					c.setForeground(Color.white);
					c.setBackground(Color.blue);
				}
				
				return c;
			};
		};
		var dtcr = new DefaultTableCellRenderer();

		dtcr.setHorizontalAlignment(0);

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
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
