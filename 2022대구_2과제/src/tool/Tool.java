package tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	Color red = new Color(237, 85, 59), orange = new Color(246, 213, 92), navy = new Color(42, 72, 88);

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

	default String getOne(String sql, Object... obj) {
		var rs = getRows(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0).toString();
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 0);
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

	default JButton btnBlack(String c, ActionListener a) {
		var b = btn(c, a);
		b.setForeground(Color.white);
		b.setBackground(Color.black);
		return b;
	}

	default JButton btnRound(String c, ActionListener a) {
		var btn = new JButton(c) {
			Shape shape;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(red);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
			}

			@Override
			protected void paintBorder(Graphics g) {
				super.paintBorder(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
			}

			@Override
			public boolean contains(int x, int y) {
				if (shape == null || !shape.getBounds().equals(getBounds())) {
					shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				}

				return shape.contains(x, y);
			}
		};
		btn.addActionListener(a);
		btn.setOpaque(false);
		btn.setForeground(Color.white);
		btn.setBackground(red);

		return btn;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, 0, sz);
	}

	default JLabel lblB(String c, int a, int sz) {
		return lbl(c, a, 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	interface Invoker {
		void run(MouseEvent e);
	}

	default JLabel lbl(String c, int a, int sz, Invoker i) {
		var l = lbl(c, a, 0, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				i.run(e);
			}
		});
		return l;
	}

	default JLabel hyplbl(String c, int a, int sz, Invoker i) {
		var l = lbl(c, a, 0, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				l.setBorder(null);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				l.setBorder(new MatteBorder(0, 0, 1, 0, red));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				i.run(e);
			}
		});
		return l;
	}

	default JLabel lblAgeLimit(String age) {
		return sz(new JLabel("") {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				var fm = g2.getFontMetrics();
				var str = age.equals("1") ? "ALL" : age.equals("2") ? "12" : "15";

				g2.setColor(age.equals("1") ? Color.green : age.equals("2") ? new Color(0, 123, 255) : Color.orange);
				g2.fillOval(0, 0, getWidth(), getHeight());

				g2.setColor(Color.white);
				g2.drawString(str, getWidth() / 2 - fm.stringWidth(str) / 2,
						getHeight() / 2 + g2.getFont().getSize() / 2);
			}
		}, 30, 30);
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}

	default ImageIcon getIcon(Object p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) p).getScaledInstance(w, h, 4));
	}

	default JTextField hintField(String c, int col) {
		var txt = new JTextField(col) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				g2.setColor(Color.LIGHT_GRAY);

				var ins = getInsets();
				var h = getHeight();
				var fm = g2.getFontMetrics();

				if (getText().isEmpty()) {
					g2.drawString(c, ins.left, h / 2 + fm.getAscent() / 2 - 2);
				}
			}
		};
		return txt;
	}

	default JPasswordField hintPassField(String c, int col) {
		var txt = new JPasswordField(col) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				g2.setColor(Color.LIGHT_GRAY);

				var ins = getInsets();
				var h = getHeight();
				var fm = g2.getFontMetrics();

				if (getText().isEmpty()) {
					g2.drawString(c, ins.left, h / 2 + fm.getAscent() / 2 - 2);
				}
			}
		};
		return txt;
	}

	default JTextArea hintArea(String c) {
		var area = new JTextArea() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				g2.setColor(Color.lightGray);

				var ins = getInsets();
				if (getText().isEmpty()) {
					g2.drawString(c, ins.left, 10);
				}
			}
		};
		return area;
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
		var r = new DefaultTableCellRenderer();

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setSelectionMode(0);
		r.setHorizontalAlignment(0);

		t.setDefaultRenderer(Component.class, r);

		return t;
	}

	default void addRow(DefaultTableModel m, ArrayList<ArrayList<Object>> rs) {
		m.setRowCount(0);

		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}
}
