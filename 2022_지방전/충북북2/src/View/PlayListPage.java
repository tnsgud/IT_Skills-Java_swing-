package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

public class PlayListPage extends BasePage {

	JLabel img;
	DefaultTableModel m = songModel();

	JTable t = songTable(m);
	String pl_serial, pl_name, total;

	public PlayListPage(String pl_serial) {
		this.pl_serial = pl_serial;
		data();
		ui();
		events();
	}

	void events() {
		var menu = new JPopupMenu();
		t.setComponentPopupMenu(menu);

		for (var bcap : "플레이리스트에서 제거,앨범으로 이동,아티스트로 이동".split(",")) {
			var item = new JMenuItem(bcap);
			menu.add(item);
			item.addActionListener(a -> {
				var s_serial = toInt(
						t.getValueAt(toInt(((JMenuItem) a.getSource()).getParent().getName()), t.getColumnCount() - 1));

				if (a.getActionCommand().equals("플레이리스트에서 제거")) {
					execute("delete from songlist where playlist = " + pl_serial + " and song = " + s_serial);
					iMsg("삭제 되었습니다.");
					refresh();
				} else if (a.getActionCommand().equals("앨범으로 이동")) {
					mf.swapView(new AlbumPage(songToalbum.get(s_serial) + ""));
				} else {
					mf.swapView(new ArtistPage(albumToArtist.get(songToalbum.get(s_serial)) + ""));
				}
			});
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					menu.setName(t.getSelectedRow() + "");
				}
				super.mousePressed(e);
			}
		});

	}

	void refresh() {

		removeAll();
		ui();
		data();
		repaint();
		revalidate();
	}

	void ui() {
		var w = new JPanel();
		add(size(w, 200, 1), "West");
		add(t);
		if (img == null) {
			img = new JLabel("NO IMAGE", JLabel.CENTER);
			img.setMaximumSize(new Dimension(180, 180));
			img.setBorder(new LineBorder(Color.WHITE));
			img.setForeground(Color.RED);
		}
		w.add(img);

		w.add(Box.createVerticalStrut(5));
		JButton btn = null;

		w.add(btn = btn("재생하기", a -> {
			que.clear();
			for (var v : m.getDataVector()) {
				que.add(toInt(v.get(v.size() - 1)));
			}
			System.out.println(que);
			BasePage.reFresh();
		}));
		JLabel lbl = null;
		w.add(lbl = lbl(pl_name, JLabel.CENTER, 15));

		lbl.setMaximumSize(new Dimension(180, 20));
		btn.setMaximumSize(new Dimension(180, 30));

		w.add(Box.createVerticalStrut(5));

		JLabel lbl2 = null;
		w.add(lbl2 = lbl("총" + m.getRowCount() + "개의 음악", JLabel.CENTER, 10));

		JLabel lbl3 = null;
		w.add(lbl3 = lbl("총 길이 : " + (total == null ? "0" : total), JLabel.CENTER, 10));

		lbl2.setMaximumSize(new Dimension(180, 10));
		lbl3.setMaximumSize(new Dimension(180, 10));
		w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));
		w.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.WHITE), new EmptyBorder(5, 5, 5, 5)));
		w.setOpaque(false);
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void data() {
		addSongRow(
				"SELECT if(s.titlesong = 1 , 1, 0) isTitle, s.name, if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = "
						+ u_serial
						+ "), true, false) isFavorite ,time_format(s.length, '%i:%S')  ,s.serial FROM song s, playlist pl, songlist sl where pl.serial = "
						+ pl_serial + " and sl.playlist = pl.serial and sl.song = s.serial group by s.serial",
				m);
		if (m.getRowCount() > 0) {
			var s_serial = m.getValueAt(0, 5).toString();

			try {
				var rs = stmt.executeQuery("select * from song where serial = " + s_serial);
				if (rs.next()) {
					img = imglbl(MainFrame.IMG_PATH + "album/" + rs.getInt("album") + ".jpg", 180, 180);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			var rs = stmt.executeQuery("select * from playlist where serial = " + pl_serial);
			if (rs.next()) {
				pl_name = rs.getString("name");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery(
					"SELECT sec_to_time(sum(time_to_sec(s.length))) from song s, playlist pl, songlist sl where pl.serial = "
							+ pl_serial + " and sl.playlist = pl.serial and sl.song = s.serial");
			if (rs.next()) {
				total = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		u_serial = 1;
		mf.swapView(new PlayListPage("1"));
		mf.setVisible(true);
	}

}
