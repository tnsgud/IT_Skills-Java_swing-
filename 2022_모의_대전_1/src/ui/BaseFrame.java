package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import model.Perform;
import model.User;

public class BaseFrame extends JFrame {
	static Perform perform;
	static User user;
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	static boolean isLogin = false;
	static int totPrice;
	JPanel n, w, c, e, s;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setIconImage(img("오렌지").getImage());
		DB.execute("use 2021전국");
	}

	public static ImageIcon img(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path + ".jpg"));
	}

	public static ImageIcon img(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Datafiles/" + path + ".jpg").getScaledInstance(w,
				h, Image.SCALE_SMOOTH));
	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	public static JLabel lbl(String c, int a, int s) {
		var l = new JLabel(c, a);
		l.setFont(new Font("", Font.BOLD, s));
		return l;
	}

	public static JLabel lbl(String c, int a) {
		return new JLabel(c, a);
	}

	public static JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
	}

	public static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	public static int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	public static DefaultTableModel model(String[] col) {
		return new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	public static JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}
	
	public static void addRow(DefaultTableModel m, String sql, Object...obj) {
		m.setRowCount(0);
		
		try {
			var rs = DB.rs(sql, obj);
			while(rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i+1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
