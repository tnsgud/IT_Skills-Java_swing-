package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

public interface Tool {
	String sub[] = "국어,수학,영어,역사,사회,과학,직업,제2외국어".split(",");
	String detailSub[][] = { "문학,독서,화법과 작문,언어와 매체".split(","), "수학1,수학2,미적분".split(","), "영어독해,영어문법,영어듣기".split(","),
			"한국사,세계사".split(","), "생활과 윤리,사회 문화,윤리와 사상".split(","), "지구과학,생명과학,화학".split(","),
			"공업,상업정보,농업기술".split(","), "중국어,일본어,프랑스어".split(",") };

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	default ArrayList<ArrayList<Object>> getRows(String sql, Object... obj) {
		var list = new ArrayList<ArrayList<Object>>();

		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			var rs = DB.ps.executeQuery();
			while (rs.next()) {
				var row = new ArrayList<Object>();
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

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default JLabel lbl(String c, int a, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", 0, sz));
		return l;
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 12);
	}

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setFont(new Font("맑은 고딕", 0, 13));
		b.setBackground(new Color(200, 200, 250));
		return b;
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
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
				g2.drawString(s, getInsets().left + 125, g2.getFontMetrics().getMaxAscent() + getInsets().top + 10);
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
				g2.drawString(s, getInsets().left + 90, g2.getFontMetrics().getMaxAscent() + getInsets().top + 10);
			}
		};
		t.setBorder(new LineBorder(Color.LIGHT_GRAY));
		return t;
	}

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default void opeque(JComponent c, boolean isOp) {
		for (var com : c.getComponents()) {
			if (com instanceof JPanel) {
				((JPanel) com).setOpaque(isOp);
				opeque((JPanel) com, isOp);
			}
		}
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

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}

		return t;
	}
}
