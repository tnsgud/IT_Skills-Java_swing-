package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CategoryPage extends BasePage {

	int i = 0;
	String t;
	JPanel n = new JPanel(new BorderLayout()) {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			System.out.println(t);

			ImageIcon img = new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/images/category/" + t + ".jpg")
					.getScaledInstance(1050, 250, Image.SCALE_SMOOTH));
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(img.getImage(), 0, 0, null);
		}

	};
	JPanel c = new JPanel(new BorderLayout());
	JPanel c_c = new JPanel(new GridLayout(0, 3, 5, 5));

	public CategoryPage(String t) {
		this.t = t.replace("\r", "").replace("\n", "");

		BasePage.size(n, 1000, 250);

		n.setOpaque(false);
		c.setOpaque(false);
		c_c.setOpaque(true);

		c_c.setBackground(Color.black);

		setBorder(new EmptyBorder(5, 5, 5, 5));

		add(n, "North");
		n.add(lbl(this.t, JLabel.CENTER, Font.BOLD, 30));
		add(c);
		c.add(lbl("최근 발매한 앨범", JLabel.LEFT, Font.BOLD, 15), "North");
		c.add(c_c);

		try {
			var rs = stmt.executeQuery(
					"select a.name, year(a.release), a.serial from history h, song s, category c, album a where a.category = c.serial and a.serial = s.album and h.song = s.serial and s.titlesong = 1 and c.name like '%"
							+ this.t + "%' group by s.name order by year(a.release) desc, a.name asc");
			while (rs.next()) {
				SongItem item = new SongItem(
						"./지급자료/images/album/" + (rs.getString(3).replace("\n", "").replace("\r", "")) + ".jpg",
						"<html>" + rs.getString(1) + "<br>" + rs.getString(2) + "년");
				item.setName(rs.getString(3));
				item.setBorder(new EmptyBorder(25, 0, 25, 0));
				item.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						BasePage.mf.swapView(new AlbumPage(((JPanel) e.getSource()).getName()));
					}
				});
				c_c.add(item);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setPreferredSize(new Dimension(1000, 250 * (i / 3)));
	}
}
