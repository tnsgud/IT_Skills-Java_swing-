package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class BaseFrame extends JFrame {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;

	HashMap<String, String> hashMap = new HashMap<String, String>();
	static boolean theme;

	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();

	static {
		try {
			stmt.execute("use busticketbooking");
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String uname, uno;

	JPanel n, c, s, w, e;
	JPanel nn, nc, ns, nw, ne;
	JPanel cn, cc, cs, cw, ce;
	JPanel en, ec, es, ew, ee;
	JPanel sn, sc, ss, sw, se;
	JPanel wn, wc, ws, ww, we;

	static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static ArrayList<String> loc1List;
	static ArrayList<String> loc2List;
	static int[] locDim;

	static void dataInit() throws SQLException {
		loc1List = new ArrayList<String>();
		loc1List.clear();
		var rs = stmt.executeQuery("select name from location");
		loc1List.add("");
		while (rs.next()) {
			loc1List.add(rs.getString(1));
		}

		loc2List = new ArrayList<String>();
		loc2List.clear();
		rs = stmt.executeQuery("select name from location2");
		loc2List.add("");
		while (rs.next()) {
			loc2List.add(rs.getString(1));
		}

		locDim = new int[loc2List.size() + 1];
		rs = stmt
				.executeQuery("select l1.no, l2.no from location l1 inner join location2 l2 on l1.no = l2.location_no");
		while (rs.next()) {
			locDim[rs.getInt(2)] = rs.getInt(1);
		}

	}

	public BaseFrame(String title, int w, int h) {
		super(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		hashMap.put("�λ�", "busan");
		hashMap.put("������", "gangwondo");
		hashMap.put("����", "gyeongju");
		hashMap.put("���󳲵�", "Jeollanam-do");
		hashMap.put("����", "seoul");
	}

	public static JLabel lbl(String lbl, int a) {
		return new JLabel(lbl);
	}

	static String getOne(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JLabel lbl(String lbl, int a, int s) {
		var l = new JLabel(lbl, a);
		l.setFont(new Font("���� ���", Font.BOLD, s));
		return l;
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

	static void iMsg(String c) {
		var msg = new JLabel(c, UIManager.getIcon("OptionPane.informationIcon"), JLabel.CENTER);
		JOptionPane.showMessageDialog(null, msg, "�ȳ�", JOptionPane.DEFAULT_OPTION);
	}

	static void eMsg(String c) {
		var msg = new JLabel(c, UIManager.getIcon("OptionPane.errorIcon"), JLabel.CENTER);
		JOptionPane.showMessageDialog(null, msg, "����", JOptionPane.DEFAULT_OPTION);
	}

	public static JButton btn(String btn, ActionListener a) {
		var b = new JButton(btn);
		b.addActionListener(a);
		return b;
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

	static JTable table(DefaultTableModel m) {
		var t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	static void addRow(String sql, DefaultTableModel m) {
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				m.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	JButton themeButton() {
		var b = new JButton("�׸�");
		if (theme) {
			b.setForeground(Color.WHITE);
			b.setBackground(Color.darkGray);
		} else {
			b.setForeground(Color.BLACK);
			b.setBackground(Color.WHITE);
		}
		b.addActionListener(a -> {
			theme = !theme;
			if (theme) {
				b.setForeground(Color.WHITE);
				b.setBackground(Color.darkGray);
			} else {
				b.setForeground(Color.BLACK);
				b.setBackground(Color.WHITE);
			}
			setTheme(BaseFrame.this, theme);
			revalidate();
			repaint();
		});

		return b;
	}

	<T extends JTextComponent> JPopupMenu locationPopup(T txt) {
		try {
			dataInit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		var pop = new JPopupMenu();

		var menu = new JMenuItem();
		pop.add(menu);
		var c = new JPanel(new GridLayout(1, 0));
		var loc1p = new JPanel(new GridLayout(0, 1));
		var loc2p = new JPanel(new GridLayout(0, 1));
		var pane2 = new JScrollPane();
		menu.setLayout(new BorderLayout());
		menu.add(c);
		c.add(new JScrollPane(loc1p));
		c.add(pane2);
		for (String loc1 : loc1List) {
			if (loc1.equals(""))
				continue;
			var btn = btn(loc1, a -> {
				loc2p.removeAll();
				var no = loc1List.indexOf(a.getActionCommand());
				for (int i = 0; i < locDim.length; i++) {
					if (locDim[i] == no) {
						System.out.println(i);

						var btn2 = btn(loc2List.get(i + 1), aa -> {
							txt.setText(a.getActionCommand() + " " + aa.getActionCommand());
							txt.setForeground(Color.BLACK);
							pop.setVisible(false);
						});
						loc2p.add(btn2);
						btn2.setBackground(new JComboBox<>().getBackground());
						btn2.setForeground(new JComboBox<>().getForeground());
					}
				}
				pane2.setViewportView(loc2p);
				loc2p.revalidate();
				loc2p.repaint();
			});

			loc1p.add(btn);
			btn.setBackground(new JComboBox<>().getBackground());
			btn.setForeground(new JComboBox<>().getForeground());
		}
		
		
		return pop;
	};

	JComponent getPos(JComponent jc) {
		jc.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println(e.getX() + "," + e.getY());
				super.mousePressed(e);
			}
		});
		return jc;
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
			setTheme(b, theme);
			super.windowClosed(e);
		}
	}

	int toInt(Object obj) {
		return Integer.parseInt(obj.toString());
	}

	static void setTheme(JFrame jf, boolean condition) {

		// �������ΰ͵�....
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "Ȯ��");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "���");
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "Ȯ��");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "���");
		UIManager.getLookAndFeelDefaults().put("Button.foreground", new ColorUIResource(Color.WHITE));
		UIManager.getLookAndFeelDefaults().put("Button.background", new Color(0, 100, 255));

		var main_color = condition ? Color.WHITE : Color.DARK_GRAY;
		var sub_color = condition ? Color.BLACK : Color.WHITE;

		UIManager.getLookAndFeelDefaults().put("PasswordField.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("TextField.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("PasswordField.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("TextField.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("OptionPane.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("Panel.background", new ColorUIResource(main_color));
		UIManager.getLookAndFeelDefaults().put("Label.foreground", new ColorUIResource(sub_color));
		UIManager.getLookAndFeelDefaults().put("Label.background", new ColorUIResource(main_color));

		SwingUtilities.updateComponentTreeUI(jf);
	}

}
