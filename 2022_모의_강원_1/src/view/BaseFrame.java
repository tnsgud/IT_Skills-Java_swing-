package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public class BaseFrame extends JFrame {
	static int no;
	static LocalDate now = LocalDate.parse("2021-10-06");
	static boolean curCon = true;
	JPanel n, w, c, e, s;

	public BaseFrame(int w, int h) {
		super("버스예매");
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setTheme(this, curCon);
		DB.execute("use busticketbooking");
	}

	public static JPopupMenu showPopupLocation2(JComponent com, int no) {
		var menu = new JPopupMenu();

		sz(menu, 250, 250);

		menu.setLayout(new BorderLayout());
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		menu.add(sz(new JScrollPane(p1), 125, 300), "West");
		menu.add(new JScrollPane(p2));

		try {
			var rs = DB.rs("select no, name from location");
			while (rs.next()) {
				var btn = new JButton(rs.getString(2));
				btn.setName(rs.getString(1));
				btn.addActionListener(a1 -> {
					var name = toInt(((JButton) a1.getSource()).getName());
					try {
						var rs2 = DB.rs("select no, name from location2 where location_no=?", name);
						while (rs2.next()) {
							var b = new JButton(rs2.getString(2));
							b.setName(rs2.getString(1));
							b.addActionListener(a2 -> {
								if (com instanceof JTextField) {
									((JTextField) com).setText(btn.getText() + " " + a2.getActionCommand());
								} else {

								}
							});
							p2.add(b);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					p2.repaint();
					p2.revalidate();
				});
				p1.add(btn);
			}

			var rs2 = DB.rs("select no, name from location2 where location_no=?", no);
			while (rs2.next()) {
				var b = new JButton(rs2.getString(2));
				b.setName(rs2.getString(1));
				b.addActionListener(a2 -> {
					if (com instanceof JTextField) {
						((JTextField) com).setText(
								DB.getOne("select name from location where no=?", no) + " " + a2.getActionCommand());
					} else {

					}
				});
				p2.add(b);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		menu.show(com, 0, 20);

		return menu;
	}

	public static JPopupMenu popup(JComponent com) {
		var menu = new JPopupMenu();
		menu.removeAll();

		sz(menu, 250, 250);

		menu.setLayout(new BorderLayout());
		var p1 = new JPanel(new GridLayout(0, 1));
		var p2 = new JPanel(new GridLayout(0, 1));

		menu.add(sz(new JScrollPane(p1), 125, 300), "West");
		menu.add(new JScrollPane(p2));

		try {
			var rs = DB.rs("select no, name from location");
			while (rs.next()) {
				var btn = new JButton(rs.getString(2));
				btn.setName(rs.getString(1));
				btn.addActionListener(a1 -> {
					var name = toInt(((JButton) a1.getSource()).getName());
					try {
						var rs2 = DB.rs("select no, name from location2 where location_no=?", name);
						while (rs2.next()) {
							var b = new JButton(rs2.getString(2));
							b.setName(rs2.getString(1));
							b.addActionListener(a2 -> {
								if (com instanceof JTextField) {
									((JTextField) com).setText(btn.getText() + " " + a2.getActionCommand());
								} else if (com instanceof JTable) {
									var t = (JTable) com;
									t.setValueAt(btn.getText() + " " + a2.getActionCommand(), t.getSelectedRow(),
											t.getSelectedColumn());
								}
							});
							p2.add(b);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					p2.repaint();
					p2.revalidate();
				});
				p1.add(btn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		menu.repaint();
		menu.revalidate();

		return menu;
	}

	public static int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9]", "");
		return s.trim().isEmpty() ? -1 : Integer.parseInt(s);
	}

	public static JButton themeBtn(JFrame jf, JTextField... txt) {
		var b = btn("테마", a -> {
			curCon = !curCon;
			setTheme(jf, curCon);

			for (var t : txt) {
				var name = t.getName();
				if (t.getText().equals(name)) {
					t.setForeground(curCon ? Color.LIGHT_GRAY : Color.white);
				} else {
					t.setForeground(curCon ? Color.BLACK : Color.white);
				}
			}

			var source = (JButton) a.getSource();
			source.setBackground(curCon ? Color.darkGray : Color.white);
			source.setForeground(curCon ? Color.white : Color.black);
			jf.repaint();
			jf.revalidate();
		});

		b.setBackground(curCon ? Color.darkGray : Color.white);
		b.setForeground(curCon ? Color.white : Color.white);

		return b;
	}

	public static JLabel lbl(String c, int a) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", 0, 12));
		return l;
	}

	public static JLabel lbl(String c, int a, int s) {
		var l = lbl(c, a);
		l.setFont(new Font("맑은 고딕", Font.BOLD, s));
		return l;
	}

	public static JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setForeground(Color.white);
		b.setBackground(new Color(0, 100, 255));
		b.addActionListener(a);
		return b;
	}

	public static Image img(String path, int w, int h) {
		return img(path).getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}

	public static Image img(String path) {
		return Toolkit.getDefaultToolkit().getImage("./지급파일/images/" + path);
	}

	public static ImageIcon img(byte[] bytes, int w, int h) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().createImage(bytes).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	public static <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	public static <T extends JTextField> T txt(Class<T> cls, int s, String holder) {
		try {
			var t = cls.getDeclaredConstructor(int.class).newInstance(s);
			t.setBorder(new MatteBorder(0, 0, 2, 0, Color.black));

			t.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					var o = (T) e.getSource();
					if (o.getText().trim().isEmpty()) {
						if (o instanceof JPasswordField) {
							((JPasswordField) o).setEchoChar((char) 0);
						}
						o.setText(holder);
						o.setForeground(Color.LIGHT_GRAY);
					} else {
						o.setText(o.getText());
						o.setForeground(curCon ? Color.black : Color.white);
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
					var o = (T) e.getSource();
					if (o instanceof JPasswordField)
						((JPasswordField) o).setEchoChar('*');
					if (o.getText().trim().equals(holder)) {
						o.setText("");
						o.setForeground(curCon ? Color.DARK_GRAY : Color.white);
					} else {
						o.setText(o.getText());
						o.setForeground(curCon ? Color.black : Color.white);
					}
				}
			});
			if (t instanceof JPasswordField) {
				((JPasswordField) t).setEchoChar((char) 0);
			}
			t.setText(holder);
			t.setName(holder);
			t.setForeground(Color.LIGHT_GRAY);
			return t;
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "안내", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "오류", JOptionPane.ERROR_MESSAGE);
	}

	public static void setTheme(JFrame jf, boolean condition) {
		UIManager.getLookAndFeelDefaults().put("OptionPane.okButtonText", "확인");
		UIManager.getLookAndFeelDefaults().put("OptionPane.cancelButtonText", "취소");
		UIManager.getLookAndFeelDefaults().put("OptionPane.yesButtonText", "네(Y)");
		UIManager.getLookAndFeelDefaults().put("OptionPane.noButtonText", "아니요(Y)");

		var main = condition ? Color.white : Color.DARK_GRAY;
		var sub = condition ? Color.black : Color.white;

		UIManager.getLookAndFeelDefaults().put("PasswordField.background", new ColorUIResource(main));
		UIManager.getLookAndFeelDefaults().put("TextField.background", new ColorUIResource(main));
		UIManager.getLookAndFeelDefaults().put("OptionPane.background", new ColorUIResource(main));
		UIManager.getLookAndFeelDefaults().put("OptionPane.foreground", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("Panel.background", new ColorUIResource(main));
		UIManager.getLookAndFeelDefaults().put("Label.background", new ColorUIResource(main));
		UIManager.put("TabbedPane.selectedForeground", sub);
		UIManager.put("TitledBorder.border", new LineBorder(sub));
		UIManager.getLookAndFeelDefaults().put("TitledBorder.borderColor", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("TitledBorder.titleColor", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("PasswordField.foreground", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("TextField.foreground", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("Label.foreground", new ColorUIResource(sub));
		UIManager.getLookAndFeelDefaults().put("Table.ascendingSortIcon", sIcon("↑"));
		UIManager.getLookAndFeelDefaults().put("Table.descendingSortIcon", sIcon("↓"));

		SwingUtilities.updateComponentTreeUI(jf);
	}

	private static Icon sIcon(String txt) {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawString(txt, x, y + 4);
			}

			@Override
			public int getIconWidth() {
				return 0;
			}

			@Override
			public int getIconHeight() {
				return 0;
			}
		};
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			setTheme(b, curCon);
			b.setVisible(true);
		}
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
		var dtcr = new DefaultTableCellRenderer();

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setAutoCreateRowSorter(true);
		t.setSelectionMode(0);

		dtcr.setHorizontalAlignment(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

//		t.getTableHeader().addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				
//			}
//		});

		return t;
	}
}
