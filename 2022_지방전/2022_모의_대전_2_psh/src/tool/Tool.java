package tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import view.BaseFrame;

public interface Tool {
	DecimalFormat format = new DecimalFormat("#,##0");
	
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
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	default void addRow(DefaultTableModel m, String sql, Object... obj) {
		m.setRowCount(0);

		var rs = DB.rs(sql, obj);
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

	default int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
	}

	default JLabel img(String p, int w, int h) {
		return new JLabel(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./Datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default JLabel img(byte[] b, int w, int h) {
		return new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().createImage(b).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, "맑은 고딕", 0, 12);
	}

	default JLabel lbl(String c, int a, int s) {
		return lbl(c, a, "맑은 고딕", Font.BOLD, s);
	}

	default JLabel lbl(String c, int a, String f, int s, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, s, sz));
		return l;
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
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
