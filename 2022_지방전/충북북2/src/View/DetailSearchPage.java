package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class DetailSearchPage extends BasePage {

	int idx;
	JPanel c = new JPanel();
	int cnt;
	String sql;
	String[] type = "음악,앨범,아티스트,플레이리스트".split(",");

	public DetailSearchPage(int idx, int cnt, String sql) {
		this.idx = idx;
		this.sql = sql;
		this.cnt = cnt;
		ui();
		System.out.println(sql);
	}

	void ui() {
		var n = new JPanel(new BorderLayout());
		add(n, "North");
		n.add(lbl(type[idx] + " " + cnt + "건", JLabel.LEFT, 15));

		n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE), new EmptyBorder(5, 5, 5, 5)));

		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		c.setOpaque(false);
		n.setOpaque(false);

		add(c);

		try {
			var rs = stmt.executeQuery(sql);
			var hBox = Box.createHorizontalBox();
			hBox.setAlignmentX(LEFT_ALIGNMENT);
			while (rs.next()) {

				var path = "";
				if (idx == 2) {
					path = "./지급자료/images/artist/" + rs.getString(2) + ".jpg";
				} else {
					path = "./지급자료/images/album/" + rs.getString(2) + ".jpg";
				}

				SongItem item = new SongItem(path, rs.getString(1), BorderLayout.SOUTH);
				if (idx == 0)
					item.s_serial = rs.getInt(4);
				item.setPreferredSize(new Dimension(200, 200));
				item.setMaximumSize(new Dimension(200, 200));
				hBox.add(item);
				if (hBox.getComponents().length == 5) {
					c.add(hBox);
					hBox = Box.createHorizontalBox();
					hBox.setAlignmentX(LEFT_ALIGNMENT);
				}

				item.setName(rs.getString(2));

				item.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var me = (SongItem) e.getSource();
						var serial = me.getName();
						if (idx == 0) {
							iMsg("대기열에 추가되었습니다.");
							que.add(me.s_serial);
							reFresh();
						} else if (idx == 1) {
							mf.swapView(new AlbumPage(serial));
						} else if (idx == 2) {
							mf.swapView(new ArtistPage(serial));
						} else if (idx == 3) {
							mf.swapView(new PlayListPage(serial));
						}
						super.mousePressed(e);
					}
				});
			}

			if (hBox.getComponents().length > 0) {
				c.add(hBox);
				hBox.setAlignmentX(LEFT_ALIGNMENT);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
