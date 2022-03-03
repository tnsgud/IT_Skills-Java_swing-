import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BaseFrame extends JFrame {

	JPanel n, c, s, e, w;

	static String uno = "", pno;

	static Connection con = DB.con;
	static Statement stmt = DB.stmt;

	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	
	static DecimalFormat df = new DecimalFormat("#,##0");

	static {
		try {
			stmt.execute("use 2021전국");
			dtcr.setHorizontalAlignment(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	int toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	String getone(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {
			return null;
		}
	}

	JLabel lbl(String t, int a) {
		var l = new JLabel(t, a);
		return l;
	}

	JLabel lbl(String t, int a, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("맑은 고딕", Font.PLAIN, s));
		return l;
	}

	ImageIcon icon(String path, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("Datafiles/공연사진/" + path + ".jpg").getScaledInstance(w, h, 4));
	}

	JButton btn(String t, ActionListener a) {
		var b = new JButton(t);
		b.addActionListener(a);
		return b;
	}

	DefaultTableModel model(String[] col) {
		var m = new DefaultTableModel(null, col) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};

		return m;
	}

	JTable table(DefaultTableModel m) {
		var t = new JTable(m);

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	void addRow(DefaultTableModel m, String sql) {
		m.setRowCount(0);

		try {
			var rs = stmt.executeQuery(sql);
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

	<T extends JComponent> T sz(T c, int w, int h) {
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

	public BaseFrame(String t, int w, int h) {
		super(t);
		this.setSize(w, h);
		this.setDefaultCloseOperation(2);
		this.setLocationRelativeTo(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Datafiles/오렌지.jpg"));
	}
}
