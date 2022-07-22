package tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	LocalDate now = LocalDate.of(2022, 8, 30);
	Color red = new Color(255, 0, 55);
	String division[] = ",성인,청소년,시니어,장애닝".split(",");

	default int toInt(Object p) {
		var s = p.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default String format(int n) {
		return new DecimalFormat("#,##0").format(n);
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
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

	default void execute(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			System.out.println(DB.ps);
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default String getOne(String sql, Object... obj) {
		var rs = getRows(sql, obj);
		return rs.isEmpty() ? "" : rs.get(0).get(0).toString();
	}

	default JLabel lbl(String c, int a, String f, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		return lbl(c, a, "맑은 고딕", st, sz);
	}

	default JLabel lblSerif(String c, int a, int st, int sz) {
		return lbl(c, a, "SanSerif", st, sz);
	}

	default JLabel lblHY(String c, int a, int st, int sz) {
		return lbl(c, a, "MY헤드라인M", st, sz);
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	interface Invoker {
		void run(MouseEvent e);
	}

	default JLabel lbl(String c, int a, int st, int sz, Invoker i) {
		var l = lbl(c, a, st, sz);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				i.run(e);
			}
		});
		return l;
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JLabel imglbl(String c, int a, String p, int w, int h) {
		var l = lbl(c, a);
		l.setIcon(getIcon(p, w, h));
		l.setVerticalTextPosition(3);
		l.setHorizontalTextPosition(0);
		return l;
	}

	default JTextField hintField(String s, int c) {
		var t = new JTextField(c) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (!getText().isEmpty())
					return;

				var g2 = (Graphics2D) g;

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(s, getInsets().left + 5, g2.getFontMetrics().getMaxAscent() + getInsets().top);
			}
		};
		t.setBorder(new LineBorder(Color.LIGHT_GRAY));
		return t;
	}

	default JPasswordField hintPassField(String s, int c) {
		var t = new JPasswordField(c) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (!getText().isEmpty())
					return;

				var g2 = (Graphics2D) g;

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(s, getInsets().left + 5, g2.getFontMetrics().getMaxAscent() + getInsets().top);
			}
		};
		t.setBorder(new LineBorder(Color.LIGHT_GRAY));
		return t;
	}

	default ImageIcon getIcon(Object p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage((byte[]) p).getScaledInstance(w, h, 4));
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setForeground(Color.white);
		b.setBackground(Tool.red);
		b.addActionListener(a);
		return b;
	}

	default JButton btnBlack(String c, ActionListener a) {
		var b = new JButton(c);
		b.setForeground(Color.white);
		b.setBackground(Color.black);
		b.addActionListener(a);
		return b;
	}

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
		var r = new DefaultTableCellRenderer();

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setSelectionMode(0);
		r.setHorizontalAlignment(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}

		return t;
	}
}
