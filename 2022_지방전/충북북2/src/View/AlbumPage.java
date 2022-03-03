package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

public class AlbumPage extends BasePage {

	String ar_name, release, al_name, c_name;
	DefaultTableModel m = songModel();
	JTable st = songTable(m);
	JLabel namelbl;

	public AlbumPage(String al_serial) {
		super();
		BasePage.al_serial = al_serial;
		data();
		ui();
		events();
	}

	void data() {
		try {
			var rs = stmt.executeQuery(
					"select al.name, ar.name, c.name, year(al.release) from album al, artist ar, category c where c.serial = al.category and ar.serial = al.artist and al.serial = "
							+ al_serial);
			if (rs.next()) {
				ar_name = rs.getString(2);
				al_name = rs.getString(1);
				release = rs.getString(4);
				c_name = rs.getString(3);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select * from album where serial = " + al_serial);
			if (rs.next()) {
				ar_serial = rs.getString("artist");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		addSongRow("SELECT \r\n" + "	if(s.titlesong = 1 , 1, 0),\r\n" + "	s.name,\r\n"
				+ "   if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = "
				+ u_serial + "), true, false) isFavorite\r\n" + "    ,time_format(s.length, '%i:%S') \r\n"
				+ "	,s.serial\r\n" + "FROM\r\n" + "    song s,\r\n" + "    album al,\r\n" + "    artist ar\r\n"
				+ "WHERE\r\n" + "	s.album = al.serial\r\n" + "    and al.artist = ar.serial\r\n"
				+ "    and al.serial = " + al_serial + "\r\n" + "	group by s.serial", m);

	}

	void ui() {
		var n = new JPanel(new BorderLayout(5, 5));
		var n_c = new JPanel();
		add(n, "North");
		n.add(imglbl("./지급자료/images/album/" + al_serial + ".jpg", 200, 200), "West");
		n.add(n_c);

		n_c.add(Box.createVerticalStrut(40));

		n_c.add(lbl(al_name, JLabel.LEFT, 15));
		n_c.add(Box.createVerticalStrut(5));
		n_c.add(namelbl = lbl(ar_name, JLabel.LEFT, 15));
		n_c.add(Box.createVerticalStrut(5));
		n_c.add(lbl(c_name, JLabel.LEFT, 15));
		n_c.add(Box.createVerticalStrut(5));
		n_c.add(lbl(release + "년", JLabel.LEFT, 15));

		n.add(btn("재생하기", a -> {
			for (int i = 0; i < st.getRowCount(); i++) {
				var s_serial = toInt(st.getValueAt(i, st.getColumnCount() - 1));
				que.add(s_serial);
				reFresh();
			}

			iMsg("앨범은 대기열에 추가했습니다.");
		}), "South");

		n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.gray), new EmptyBorder(5, 5, 5, 5)));

		setBorder(new EmptyBorder(5, 5, 5, 5));

		add(st);

		n_c.setLayout(new BoxLayout(n_c, BoxLayout.Y_AXIS));
		n.setOpaque(false);
		n_c.setOpaque(false);
	}

	void events() {
		namelbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				namelbl.setForeground(Color.GREEN);
				super.mouseEntered(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				namelbl.setForeground(Color.WHITE);
				// TODO Auto-generated method stub
				super.mouseExited(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mf.swapView(new ArtistPage(ar_serial));
				super.mousePressed(e);
			}
		});

		var menu = new JPopupMenu();

		st.setComponentPopupMenu(menu);

		for (var bcap : "아티스트로 이동,플레이리스트에 추가".split(",")) {
			var item = new JMenuItem(bcap);
			menu.add(item);
			item.addActionListener(a -> {
				var me = (JMenuItem) a.getSource();
				var pop = me.getParent();
				var row = toInt(pop.getName());
				var s_serial = toInt(st.getValueAt(row, st.getColumnCount() - 1));
				if (a.getActionCommand().equals("아티스트로 이동")) {
					mf.swapView(new ArtistPage(albumToArtist.get(songToalbum.get(s_serial)) + ""));
				} else {
					addtoPlayList(s_serial);
				}
			});
		}

		st.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var popup = st.getComponentPopupMenu();
				popup.setName(st.getSelectedRow() + "");
				super.mousePressed(e);
			}
		});

	}
}
