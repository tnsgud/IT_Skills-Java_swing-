package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class BaseFrame extends JFrame {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;

	JPanel n, c, s, w, e;
	JPanel nn, nc, ns, nw, ne;
	JPanel cn, cc, cs, cw, ce;
	JPanel sn, sc, ss, sw, se;
	JPanel en, ec, es, ew, ee;
	JPanel wn, wc, ws, ww, we;

	static String uname, uno, ugender, ugraduate;
	String category[] = ",편의점,영화관,화장품,음식점,백화점,의류점,커피전문점,은행".split(",");
	String local[] = "전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",");
	String graduate[] = "대학교 졸업,고등학교 졸업,중학교 졸업,무관".split(",");
	String gender[] = "남자,여자,무관".split(",");
	static {
		try {
			stmt.execute("use 2022지방_2");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ArrayList<ArrayList<Object>> getResults(String sql, Object... args) {
		var rows = new ArrayList<ArrayList<Object>>();
		try {
			var pst = con.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					pst.setObject(i + 1, args[i]);
				}
			}
			System.out.println(pst);
			var meta = pst.getMetaData();
			var rs = pst.executeQuery();

			while (rs.next()) {
				var col = new ArrayList<Object>();
				for (int i = 0; i < meta.getColumnCount(); i++) {
					col.add(rs.getObject(i + 1));
				}
				rows.add(col);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}

	void setValues(String sql, Object... args) {
		try {
			var pst = con.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					pst.setObject(i + 1, args[i]);
				}
			}
			System.out.println(pst);
			pst.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	int toInt(Object p) {
		return Integer.parseInt(p.toString());
	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	JLabel crt_lbl(String title, int alig) {
		return new JLabel(title, alig);
	}

	JLabel crt_lbl(String title, int alig, int style, int size) {
		var lbl = new JLabel(title, alig);
		lbl.setFont(new Font("", style, size));
		return lbl;
	}

	JLabel crt_lbl(String title, int alig, String fontname, int style, int size) {
		var lbl = new JLabel(title, alig);
		lbl.setFont(new Font(fontname, style, size));
		return lbl;
	}

//	ImageIcon getIcon(String path) {
//		return new ImageIcon(path);
//	}

	ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	ImageIcon toIcon(Object data, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().createImage((byte[]) data).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	JButton crt_evt_btn(String cap, ActionListener a) {
		var btn = new JButton(cap);
		btn.addActionListener(a);
		return btn;
	}

	JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	DefaultTableModel crt_model(String col[]) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return m;
	}

	void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	<T> JComboBox<T> crt_combo(T values[]) {
		return new JComboBox<T>(new DefaultComboBoxModel<T>(values));
	}

	JTable crt_table(DefaultTableModel m) {
		JTable t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		r.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}

		return t;
	}

	void addRow(ArrayList<ArrayList<Object>> row, DefaultTableModel m) {
		m.setRowCount(0);
		for (var col : row) {
			m.addRow(col.toArray());
		}
	}

	class before extends WindowAdapter {
		BaseFrame b;

		public before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
			super.windowClosed(e);
		}
	}
}
