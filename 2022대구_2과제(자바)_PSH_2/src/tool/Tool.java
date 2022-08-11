package tool;

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
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import db.DB;

public interface Tool {
	Color red = new Color(237, 57, 59), yellow = new Color(246, 213, 92), navy = new Color(42, 72, 88);
	String[] m_age = ",전체관람가,12세이상관람가,15세이상관람가".split(",");

	default ArrayList<ArrayList<Object>> getRows(String sql, Object... obj) {
		var list = new ArrayList<ArrayList<Object>>();
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
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

	default int toInt(Object o) {
		var s = o.toString().replaceAll("[^0-9|^-]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	default File filechooser() {
		var filter = new FileNameExtensionFilter("JPG Images", "jpg");
		var chooser = new JFileChooser();

		chooser.setFileFilter(filter);
		chooser.setDialogTitle("이미지 선택");

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default int loginCheck() {
		return JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시겠습니까?", "질문", JOptionPane.YES_NO_OPTION);
	}

	default String mapToGenre(String genre) {
		var genres = getRows("select g_name from genre").stream().map(x -> x.get(0).toString()).toArray(String[]::new);
		return Stream.of(genre.split("\\.")).map(x -> genres[toInt(x) - 1]).collect(Collectors.joining(","));
	}

	default JScrollPane scroll(Component c) {
		var scr = new JScrollPane(c);
		var ui1 = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.LIGHT_GRAY;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}
		};
		var ui2 = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.LIGHT_GRAY;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}
		};

		scr.getVerticalScrollBar().setUI(ui1);
		scr.getHorizontalScrollBar().setUI(ui2);

		return scr;
	}

	default JLabel lbl(String c, int a, int st, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", st, sz));
		return l;
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

	default JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		b.setForeground(Color.white);
		b.setBackground(Tool.red);
		return b;
	}

	default JButton btnRound(String c, ActionListener a) {
		var b = new JButton(c) {
			Shape shape;

			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(red);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

				super.paintComponent(g);
			}

			@Override
			public boolean contains(int x, int y) {
				if (shape == null || !shape.getBounds().equals(getBounds())) {
					shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				}
				return shape.contains(x, y);
			}
		};
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);

		b.addActionListener(a);

		b.setForeground(Color.white);
		b.setBackground(red);

		return b;
	}

	default JTextField txt(String c, int sz) {
		var txt = new JTextField(sz) {
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

	default JPasswordField txtPw(String c, int sz) {
		var txt = new JPasswordField() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				g2.setColor(Color.lightGray);
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

	default JTextArea area(String c, int sz) {
		var area = new JTextArea() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
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
		return area;
	}

	default ImageIcon getIcon(String p, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(p).getScaledInstance(w, h, 4));
	}
}
