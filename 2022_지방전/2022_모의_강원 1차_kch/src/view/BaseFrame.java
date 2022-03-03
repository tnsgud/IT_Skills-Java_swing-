package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class BaseFrame extends JFrame {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;

	static boolean theme = true;
	static String uno, uid, upwd, uname, uemail, upoint;
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel sn, sc, sw, se, ss;
	JPanel en, ec, ew, ee, es;

	static String loc1[] = new String[17];
	static String loc2[] = new String[228];
	public static int locMap[] = new int[228];
	static HashMap<String, String> map = new HashMap<String, String>();

	static {
		try {
			stmt.execute("use busticketbooking");
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void execute(String sql) {
		db.DB.execute(sql);
	}

	public static void dataInit() throws SQLException {
		var rs = stmt.executeQuery("select * from location2");
		while (rs.next()) {
			locMap[rs.getInt(1)] = rs.getInt(3);
			loc2[rs.getInt(1)] = rs.getString(2);
		}

		var rs2 = stmt.executeQuery("select * from location");
		while (rs2.next()) {
			loc1[rs2.getInt(1)] = rs2.getString(2);
		}

	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setTheme(this, theme);
		map.put("부산", "busan");
		map.put("강원도", "gangwondo");
		map.put("광주", "gyeongju");
		map.put("전라남도", "Jeollanam-do");
		map.put("서울", "seoul");

	}

	static void Query(String sql, ArrayList<ArrayList<String>> list, String... v) {

		try {

			PreparedStatement s = con.prepareStatement(sql);

			for (int i = 0; i < v.length; i++) {
				s.setString(i + 1, v[i]);
			}

			list.clear();
			ResultSet rs = s.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();

			while (rs.next()) {
				ArrayList row = new ArrayList<>();
				for (int i = 1; i <= rsm.getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				list.add(row);
			}

			s.close();

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static String getOne(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next())
				return rs.getString(1);
			else
				return "";
		} catch (SQLException e) {
			return null;
		}
	}

	static void iMsg(String c) {
		var msg = new JLabel(c, UIManager.getIcon("OptionPane.informationIcon"), JLabel.CENTER);
		JOptionPane.showMessageDialog(null, msg, "안내", JOptionPane.DEFAULT_OPTION);
	}

	static void eMsg(String c) {
		var msg = new JLabel(c, UIManager.getIcon("OptionPane.errorIcon"), JLabel.CENTER);
		JOptionPane.showMessageDialog(null, msg, "오류", JOptionPane.DEFAULT_OPTION);
	}

	public static JButton btn(String title, ActionListener a) {
		var btn = new JButton(title);
		btn.addActionListener(a);
		btn.setForeground(Color.WHITE);
		btn.setBackground(new Color(0, 100, 255));
		return btn;
	}

	public static ImageIcon getIcon(String path) {
		return new ImageIcon(path);
	}

	static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	public static ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	public static JLabel lbl(String title, int a) {
		return new JLabel(title, a);
	}

	public static JLabel lbl(String title, int a, int s) {
		var l = new JLabel(title, a);
		l.setFont(new Font("맑은 고딕", Font.BOLD, s));
		return l;
	}

	// table...
	public static JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	static DefaultTableModel model(String col[]) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		return m;
	}

	static void addRow(String sql, DefaultTableModel m) {
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// about theme...
	public static JButton themebtn(JFrame f) {
		var b = new JButton("테마");
		b.setForeground(theme ? Color.white : Color.DARK_GRAY);
		b.setBackground(theme ? Color.DARK_GRAY : Color.WHITE);

		b.addActionListener(a -> {
			theme = !theme;
			b.setForeground(theme ? Color.white : Color.DARK_GRAY);
			b.setBackground(theme ? Color.DARK_GRAY : Color.WHITE);
			setTheme(f, theme);
		});

		return b;
	}

	static JPopupMenu locPopup(JComponent comp, int r, int c, int w, int h) {
		try {
			dataInit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JPopupMenu menu = new JPopupMenu();
		menu.setPreferredSize(new Dimension((int) w, h));
		menu.setLayout(new GridLayout(1, 0));
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		JScrollPane jsp1 = new JScrollPane(p1);
		JScrollPane jsp2 = new JScrollPane(p2);

		for (var l1 : loc1) {
			if (l1 == null)
				continue;
			JButton btn = new JButton(l1);
			btn.addActionListener(a -> {
				p2.removeAll();
				p2.setLayout(new GridLayout(0, 1));
				final var 지역1 = a.getActionCommand();
				try {
					var rs = stmt.executeQuery(
							"SELECT * FROM location2 l2 inner join location l on l2.location_no = l.no where l.name = '"
									+ 지역1 + "'");
					while (rs.next()) {
						JButton btn2 = new JButton(rs.getString(2));
						btn2.addActionListener(b -> {
							if (comp instanceof JTextField) {
								((JTextField) comp).setText(지역1 + " " + b.getActionCommand());
							} else {
								((JTable) comp).setValueAt(지역1 + " " + b.getActionCommand(), r, c);
							}
						});
						p2.add(btn2);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				jsp2.setViewportView(p2);
			});
			p1.add(btn);
		}
		menu.add(jsp1);
		menu.add(jsp2);

		menu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				jsp2.setViewportView(null);
				jsp1.getVerticalScrollBar().setValue(0);
				jsp2.getVerticalScrollBar().setValue(0);
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}
		});

		jsp2.setViewportView(null);
		return menu;
	}

	static int toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	JPopupMenu ShownlocPopup(JTextComponent jt, int 지역1) {
		try {
			dataInit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JPopupMenu menu = new JPopupMenu();
		menu.setPreferredSize(new Dimension((int) jt.getPreferredSize().getWidth(), 300));
		menu.setLayout(new GridLayout(1, 0));

		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		JScrollPane jsp1 = new JScrollPane(p1);
		JScrollPane jsp2 = new JScrollPane(p2);

		for (var l1 : loc1) {
			if (l1 == null)
				continue;
			JButton btn = new JButton(l1);
			p1.add(btn);
		}

		try {
			var rs = stmt.executeQuery("select * from location2 where location_no = " + 지역1);
			while (rs.next()) {
				JButton btn2 = new JButton(rs.getString(2));
				btn2.addActionListener(b -> {
					jt.setText(loc1[지역1] + " " + b.getActionCommand());
				});
				p2.add(btn2);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		menu.add(jsp1);
		menu.add(jsp2);
		menu.show(jt, 0, 20);
		return menu;
	}

	public static void setTheme(JFrame f, boolean mode) {
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "확인");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "취소");

		var main_color = mode ? Color.WHITE : Color.DARK_GRAY;
		var sub_color = mode ? Color.DARK_GRAY : Color.WHITE;

		UIManager.getLookAndFeelDefaults().put("PasswordField.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("TextField.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("OptionPane.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("OptionPane.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("Panel.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("Label.background", new ColorUIResource(main_color));
		UIManager.put("TabbedPane.selectedForeground", sub_color);
		UIManager.put("TitledBorder.border", new LineBorder(sub_color));
		UIManager.getLookAndFeelDefaults().put("TitledBorder.borderColor", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("TitledBorder.titleColor", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("PasswordField.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("TextField.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("Label.foreground", new ColorUIResource(sub_color));

		SwingUtilities.updateComponentTreeUI(f);
	}

}

class before extends WindowAdapter {
	BaseFrame f;

	public before(BaseFrame f) {
		this.f = f;
		f.setVisible(false);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		super.windowClosed(e);
		f.setTheme(f, f.theme);
		f.setVisible(true);
	}
}
