package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

class ArtistPage extends BasePage {
	JPanel artistP, mainP, viewP;
	ArrayList<Integer> albumList = new ArrayList<>();
	int rate = 0;
	JPanel reviews;

	public ArtistPage(String ar_serial) {
		BasePage.ar_serial = ar_serial;
		data();
		ui();
		overReview();
	}

	void ui() {
		var n = new JPanel(new BorderLayout(5, 5));
		var n_c = new JPanel(new BorderLayout(5, 5));
		var n_s = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(n, "North");
		n.add(n_c);
		n.add(n_s, "South");
		n_c.add(imglbl("./지급자료/images/artist/" + ar_serial + ".jpg", 180, 180), "West");
		n_c.add(lbl(ar_name, JLabel.LEFT, 30, 30));
		for (var bcap : "개요,소개".split(",")) {
			n_s.add(btn(bcap, a -> {
				if (a.getActionCommand().equals("개요")) {
					overReview();
				} else {
					about();
				}
			}));
		}

		add(viewP = new JPanel());
		viewP.setOpaque(false);
		n.setBorder(new EmptyBorder(5, 5, 5, 5));
		n_c.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		n_c.setOpaque(false);
		n_s.setOpaque(false);
		n.setOpaque(false);
	}

	void overReview() {
		viewP.removeAll();
		viewP.setLayout(new BoxLayout(viewP, BoxLayout.Y_AXIS));
		DefaultTableModel m = songModel();
		JTable ftable = songTable(m);

		JPanel chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.black);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(Color.WHITE);

				try {
					ArrayList<Integer> listValues = new ArrayList<>();

					for (int i = 10; i + 10 < 70; i += 10) {
						var rs = stmt.executeQuery(
								"select count(*) FROM artist ar, album al, user u, song s, history h WHERE ar.serial =  "
										+ ar_serial
										+ " AND h.song = s.serial AND h.user = u.serial AND s.album = al.serial AND al.artist = ar.serial  and datediff(now(), u.birth)/365 >= '"
										+ i + "' and datediff(now(), u.birth)/365 < '"
										+ ((i + 10) == 60 ? 100 : (i + 10)) + "'");

						if (rs.next()) {
							listValues.add(rs.getInt(1));
						}
					}

					int max = Collections.max(listValues);
					int mvalue = (int) (((double) max / max) * 150);
					for (int i = 0; i < 5; i++) {
						int value = (int) (((double) listValues.get(i) / max) * 200);
						if (listValues.get(i) == max)
							g2.setColor(Color.RED);
						else
							g2.setColor(Color.WHITE);
						g2.fillRect(i * 40 + 20, (mvalue - value) - 20, 30, value);
						g2.setColor(Color.WHITE);
						g2.setFont(new Font("맑은 고딕", Font.BOLD, 15));
						g2.drawString(((i + 1) * 10) + "대", (i * 40) + 20, mvalue);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		addSongRow("SELECT \r\n" + "    if(s.titlesong = 1 , 1, 0),\r\n" + "	s.name,\r\n"
				+ "   if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = "
				+ u_serial + "), true, false) isFavorite\r\n" + "    ,time_format(s.length, '%i:%S') \r\n"
				+ "	,s.serial\r\n" + "FROM\r\n" + "    song s,\r\n" + "    album al,\r\n" + "    artist ar,\r\n"
				+ "    history h\r\n" + "WHERE\r\n" + "	s.album = al.serial\r\n" + "    and al.artist = ar.serial\r\n"
				+ "    and h.song = s.serial\r\n" + "    and ar.serial = " + ar_serial + "\r\n"
				+ "	group by s.serial\r\n" + "	order by count(h.serial) desc limit 5", m);

		JPanel fm = new JPanel(new BorderLayout());
		fm.add(lbl("인기 있는 음악", JLabel.LEFT, 20), "North");

		fm.add(ftable);
		var fmenu = new JPopupMenu();
		var fitem = new JMenuItem("플레이리스트에 추가");
		fmenu.add(fitem);
		ftable.setComponentPopupMenu(fmenu);

		fitem.addActionListener(a -> {
			var me = (JMenuItem) (a.getSource());
			var serial = toInt(me.getName());
			addtoPlayList(serial);
		});

		ftable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					fitem.setName(ftable.getSelectedRow() + "");
				}
				super.mousePressed(e);
			}
		});

		fm.add(size(chart, 260, 200), "East");
		fm.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new MatteBorder(0, 0, 2, 0, Color.GRAY)));
		viewP.add(fm);

		albumList.forEach(a -> {
			var em = songModel();
			var et = songTable(em);

			addSongRow("SELECT \r\n" + "	if(s.titlesong = 1 , 1, 0),\r\n" + "	s.name,\r\n"
					+ "   if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = "
					+ u_serial + "), true, false) isFavorite\r\n" + "    ,time_format(s.length, '%i:%S') \r\n"
					+ "	,s.serial\r\n" + "FROM\r\n" + "    song s,\r\n" + "    album al,\r\n" + "    artist ar\r\n"
					+ "WHERE\r\n" + "	s.album = al.serial\r\n" + "    and al.artist = ar.serial\r\n"
					+ "    and al.serial = " + a + "\r\n" + "	group by s.serial", em);
			var item = new JMenuItem("플레이리스트에 추가");
			JPopupMenu menu = new JPopupMenu();

			et.setComponentPopupMenu(menu);
			menu.add(item);

			item.addActionListener(e -> {
				var me = (JMenuItem) e.getSource();
				int row = toInt(me.getName());
				var s_serial = toInt(et.getValueAt(row, et.getColumnCount() - 1));
				addtoPlayList(s_serial);
			});
			et.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						item.setName(et.getSelectedRow() + "");
					}
					super.mousePressed(e);
				}

			});
			var e = new JPanel(new BorderLayout());
			var e_n = new JPanel(new BorderLayout());
			var e_n_c = new JPanel(new BorderLayout());
			var showAll = lbl("모두보기", JLabel.LEFT, 10);
			e.add(e_n, "North");
			e_n.add(e_n_c);
			e_n.add(size(imglbl("./지급자료/images/album/" + a + ".jpg", 180, 180), 180, 180), "West");
			e.add(et);

			try {
				var rs = stmt.executeQuery(
						"SELECT al.name, c.name, al.release FROM album al, category c where al.category = c.serial and al.serial = "
								+ a);
				if (rs.next()) {

					e_n_c.add(lbl("<html><left>" + rs.getString(1) + "<br>" + ar_name + "<br>" + rs.getString(2) + "·"
							+ rs.getString(3), JLabel.LEFT, 15));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.setName(a + "");
			e_n_c.add(showAll, "South");

			showAll.setName(a + "");
			showAll.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) (e.getSource());
					mf.swapView(new AlbumPage(me.getName()));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					var me = (JLabel) (e.getSource());
					me.setForeground(Color.WHITE);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					var me = (JLabel) (e.getSource());
					me.setForeground(Color.GREEN);
				}

			});

			e_n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.GRAY), new EmptyBorder(5, 5, 5, 5)));

			e.setBorder(new EmptyBorder(5, 5, 5, 5));
			e_n_c.setOpaque(false);
			viewP.add(e);
			e_n.setOpaque(false);
			e.setOpaque(false);
		});

		fm.setOpaque(false);
		chart.setOpaque(false);

		repaint();
		revalidate();
	}

	void about() {
		viewP.removeAll();
		viewP.setLayout(new BorderLayout());

		ArrayList<Integer> rank = new ArrayList<Integer>();
		JLabel lbl[] = new JLabel[5];
		try {
			var rs = stmt.executeQuery(
					"select ar.serial ,count(h.serial) from history h, song s, album a, artist ar where h.song = s.serial and a.serial = s.album and ar.serial = a.artist group by ar.serial order by count(h.serial) desc, a.serial asc");
			while (rs.next()) {
				rank.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		var area = new JTextArea();
		var c = new JPanel(new BorderLayout());
		var w = new JPanel();
		var c_c = new JPanel(new BorderLayout());
		var c_c_s = new JPanel(new BorderLayout());
		var c_c_s_w = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var c_c_s_e = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
		reviews = new JPanel(new GridLayout(0, 1));
		w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));
		viewP.add(w, "West");
		viewP.add(c);
		c.add(c_c);
		c_c.add(c_c_s, "South");
		c_c_s.add(c_c_s_w, "West");
		c_c_s.add(c_c_s_e, "East");
		c.add(reviews, "South");

		try {
			var rs = stmt.executeQuery("SELECT * FROM music.artist where serial = " + ar_serial);
			if (rs.next()) {
				w.add(size(new JLabel("<html><font face = \"맑은 고딕\"; size = \"5\"; color = \"WHITE\"><left>세계 순위 "
						+ (rank.indexOf(toInt(ar_serial)) + 1) + "위<br><br><font size = \"3\">" + rs.getString(3)), 250,
						170));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		c_c.add(size(area, 1, 80));

		area.setBackground(Color.gray);
		area.setForeground(Color.WHITE);

		c_c_s_e.add(btn("등록", a -> {
			if (area.getText().isEmpty()) {
				eMsg("내용을 입력해야 합니다.");
				return;
			}

			if (rate == 0) {
				eMsg("평점을 입력해야 합니다.");
				return;
			}

			execute("insert into community values(0, " + u_serial + ", " + ar_serial + ", " + rate + ", '"
					+ area.getText() + "', curdate())");
			setReviews();
		}));

		for (int i = 0; i < lbl.length; i++) {
			c_c_s_w.add(lbl[i] = lbl("☆", JLabel.LEFT, 15));
			lbl[i].setForeground(Color.ORANGE);
			lbl[i].setName((i + 1) + "");

			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();

					for (int j = 0; j < lbl.length; j++) {
						lbl[j].setText("☆");
					}
					for (int j = 0; j < toInt(me.getName()); j++) {
						lbl[j].setText("★");
					}

					rate = toInt(me.getName());

					super.mousePressed(e);
				}
			});
		}
		setReviews();
		c_c.setBorder(new EmptyBorder(5, 5, 5, 5));
		c_c.setOpaque(false);
		c_c_s.setOpaque(false);
		c_c_s_w.setOpaque(false);
		c_c_s_e.setOpaque(false);
		w.setOpaque(false);
		c.setOpaque(false);
		repaint();
		revalidate();
	}

	void setReviews() {
		reviews.removeAll();
		try {
			var rs = stmt.executeQuery(
					"SELECT u.name, c.rate, c.date, c.content FROM community c, user u where c.user = u.serial and c.artist = "
							+ ar_serial + " order by c.date desc");
			while (rs.next()) {
				JLabel s[] = new JLabel[5];

				var panel = new JPanel(new BorderLayout());
				var panel_n = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var panel_area = new JTextArea(rs.getString(4));
				panel.add(panel_n, "North");
				var title = lbl(rs.getString(1), JLabel.LEFT, 10);
				panel_n.add(title);
				for (int i = 0; i < s.length; i++) {
					s[i] = lbl("☆", JLabel.LEFT, 10);
					if (i < rs.getInt(2))
						s[i].setText("★");
					panel_n.add(s[i]);
					s[i].setForeground(Color.ORANGE);
				}
				panel_area.setEditable(false);
				panel_area.setLineWrap(true);
				panel_area.setForeground(Color.WHITE);
				panel_area.setBackground(Color.GRAY);
				panel.add(size(panel_area, 400, 80));
				panel.setBorder(new EmptyBorder(5, 5, 5, 5));
				panel_n.add(lbl(rs.getString(3) + " 개시", JLabel.LEFT, 10));
				reviews.setOpaque(false);
				panel_n.setOpaque(false);
				panel.setOpaque(false);
				reviews.add(panel);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		revalidate();
		repaint();
	}

	void data() {
		try {
			var rs = stmt.executeQuery("select * from artist where serial = " + ar_serial);
			if (rs.next()) {
				ar_name = rs.getString(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select * from album where artist = " + ar_serial);
			while (rs.next()) {
				albumList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BasePage.u_serial = 1;
		mf.swapView(new ArtistPage(26 + ""));
		mf.setPlayList();
		mf.setVisible(true);
	}
}