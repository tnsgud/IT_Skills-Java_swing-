package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

class FavoritePage extends BasePage {

	DefaultTableModel m = songModel();
	JTable st = songTable(m);
	int lRow;

	public FavoritePage() {
		data();
		ui();
		events();
	}

	void ui() {
		var n = new JPanel(new BorderLayout());
		var n_c = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(n, "North");
		n.add(n_c);
		n.add(lbl("좋아요 한 음악", JLabel.LEFT, 20), "North");

		n_c.add(btn("재생하기", a -> {
			que.clear();
			for (var v : m.getDataVector()) {
				que.add(toInt(v.get(v.size() - 1)));
				reFresh();
			}
		}));

		n_c.add(lbl("총 " + m.getRowCount() + "개의 음악", JLabel.LEFT, 12));

		add(st);

		n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.BLUE.darker().darker()),
				new EmptyBorder(5, 5, 5, 5)));

		setBorder(new EmptyBorder(5, 5, 5, 5));
		n_c.setOpaque(false);
		n.setOpaque(false);
	}

	void data() {
		addSongRow(
				"select if(s.titlesong = 1 , 1, 0),s.name, true, time_format(s.length, '%i:%S'), s.serial from favorite f, song s where f.song = s.serial and f.user = "
						+ u_serial,
				m);
	}

	void events() {

		st.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					lRow = st.getSelectedRow();
				}
				super.mousePressed(e);
			}
		});

		var popup = new JPopupMenu();

		for (var bcap : "앨범으로 이동,아티스트로 이동,플레이리스트에 추가".split(",")) {
			var item = new JMenuItem(bcap);
			item.addActionListener(a -> {
				int row = lRow;
				var s_serial = toInt(st.getValueAt(row, st.getColumnCount() - 1));
				switch (a.getActionCommand()) {
				case "앨범으로 이동":
					mf.swapView(new AlbumPage(songToalbum.get(s_serial) + ""));
					System.out.println(s_serial);
					break;
				case "아티스트로 이동":
					mf.swapView(new ArtistPage(albumToArtist.get(songToalbum.get(s_serial)) + ""));
					break;
				default:
					addtoPlayList(s_serial);
					break;
				}
			});
			popup.add(item);
		}

		st.setComponentPopupMenu(popup);

		st.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addSongRow(
						"select if(s.titlesong = 1 , 1, 0),s.name, true, time_format(s.length, '%i:%S'), s.serial from favorite f, song s where f.song = s.serial and f.user = "
								+ u_serial,
						m);
				super.mouseClicked(e);
			}
		});
	}

	public static void main(String[] args) {

		u_serial = 1;
		u_region = 1;
		mf.swapView(new FavoritePage());
		mf.setVisible(true);
	}
}
