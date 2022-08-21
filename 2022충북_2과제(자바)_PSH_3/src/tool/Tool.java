package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	Color red = new Color(255, 0, 50);
	String[] div = ",성인,청소년,시니어,장애인".split(",");

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

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default <T extends JComponent> T event(T c, Consumer<MouseEvent> i) {
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1)
					return;
				i.accept(e);
			}
		});
		return c;
	}

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default void op(JComponent jcom) {
		for (var com : jcom.getComponents()) {
			if (com instanceof JPanel || com instanceof JCheckBox || com instanceof JRadioButton) {
				((JComponent) com).setOpaque(false);
				op(((JComponent) com));
			}
		}
	}
	
	default String format(int n) {
		return new DecimalFormat("#,##0").format(n);
	}

	default JLabel lbl(String c, int a, String f, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font(f, st, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		return lbl(c, a, "맑은 고딕", st, sz);
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, 1, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JLabel lblHY(String c, int a, int st, int sz) {
		return lbl(c, a, "HY헤드라인M", st, sz);
	}

	default JLabel lblSerif(String c, int a, int st, int sz) {
		return lbl(c, a, "SanSerif", st, sz);
	}

	default JLabel lblIcon(String c, int a, String p, int w, int h) {
		var l = new JLabel(c, a);
		l.setIcon(getIcon(p, w, h));
		l.setVerticalTextPosition(3);
		l.setHorizontalTextPosition(0);
		return l;
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.setForeground(Color.white);
		b.setBackground(red);
		b.addActionListener(a);
		return b;
	}

	default JButton btnBlack(String c, ActionListener a) {
		var b = btn(c, a);
		b.setBackground(Color.black);
		return b;
	}

	default JTextField txt(String s, int c) {
		return new JTextField(c) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				if (!getText().isEmpty())
					return;

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(s, getInsets().left, g2.getFontMetrics().getMaxAscent() + getInsets().top);
			}
		};
	}

	default JPasswordField pw(String s, int c) {
		return new JPasswordField(c) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				if (!getText().isEmpty())
					return;

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(s, getInsets().left, g2.getFontMetrics().getMaxAscent() + getInsets().top);
			}
		};
	}

	default JTextArea area(String s) {
		return new JTextArea() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				if (!getText().isEmpty())
					return;

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(s, getInsets().left, g2.getFontMetrics().getMaxAscent() + getInsets().top);
			}
		};
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
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
		
		t.setSelectionMode(0);
		r.setHorizontalAlignment(0);
		
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}
		
		return t;
	}
}
