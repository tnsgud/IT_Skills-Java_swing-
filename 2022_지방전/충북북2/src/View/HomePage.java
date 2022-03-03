package View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class HomePage extends BasePage {

	ArrayList<JPanel> _3most = new ArrayList<>();
	ArrayList<JPanel> categoryList = new ArrayList<>();

	public HomePage() {
		data();
		ui();
	}

	void ui() {
		var n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var c = new JPanel(new BorderLayout(5, 5));
		var c_n = new JPanel(new GridLayout(1, 0, 5, 5));
		var c_c = new JPanel(new GridLayout(0, 3, 5, 5));
		add(n, "North");
		add(c);
		c.add(c_n, "North");
		c.add(c_c);
		n.add(btn("로그아웃", a -> {
			mf.swapPage(new LoginPage());
		}));

		_3most.iterator().forEachRemaining(c_n::add);
		var bd = new TitledBorder(new MatteBorder(1, 0, 0, 0, Color.WHITE), "카테고리 둘러보기");
		bd.setTitleColor(Color.WHITE);
		bd.setTitleFont(new Font("맑은 고딕", Font.BOLD, 15));
		bd.setTitlePosition(TitledBorder.ABOVE_TOP);
		c_c.setBorder(new CompoundBorder(bd, new EmptyBorder(5, 5, 5, 5)));
		categoryList.forEach(c_c::add);
		n.setOpaque(false);
		c.setOpaque(false);
		c_n.setOpaque(false);
		c_c.setOpaque(false);

	}

	void data() {
		String cap[] = "이번달 히트 곡,최근 발매된 앨범,의 인기 음악".split(",");
		String sql[] = {
				"select a.serial, a.name ,s.name ,count(h.serial) from song s , album a, history h where s.album = a.serial and h.song = s.serial and month(h.date) = month(now()) group by s.serial order by count(h.serial) desc ",
				"select a.serial, a.name, s.name from album a, song s where a.release < now() or a.release > now()  and a.serial = s.album  order by a.release desc",
				"select a.serial,a.name, s.name, r.name, count(h.serial) from song s, album a, history h, user u, region r where s.album = a.serial and h.song = s.serial and h.user = u.serial and u.region = "
						+ u_region + " and u.region = r.serial group by s.serial order by count(h.serial) desc" };

		for (int i = 0; i < 3; i++) {
			try {
				var rs = stmt.executeQuery(sql[i]);
				if (rs.next()) {
					JPanel b = new JPanel(new BorderLayout()) {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							Graphics2D g2 = (Graphics2D) g;
							g2.setStroke(new BasicStroke(3));
							g.setColor(myColor);
							RoundRectangle2D rec = new RoundRectangle2D.Float(1.5f, 5.5f, getWidth() - 3,
									getHeight() - 3, 20, 20);
							g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2.fill(rec);
						}
					};
					JPanel t = new JPanel(new BorderLayout(5, 5));
					b.setOpaque(false);
					t.setOpaque(false);
					if (i == 2)
						t.add(lbl(rs.getString(4) + cap[i], JLabel.LEFT, Font.BOLD, 10), "North");
					else
						t.add(lbl(cap[i], JLabel.LEFT, Font.BOLD, 10), "North");
					t.add(lbl("<html>" + rs.getString(2) + "<br>" + rs.getString(3) + "<html>", JLabel.LEFT, 0, 10));
					t.add(imglbl("./지급자료/images/album/" + rs.getInt(1) + ".jpg", 100, 100), "West");
					b.add(t);
					_3most.add(size(b, 300, 150));
					t.setBorder(new EmptyBorder(10, 10, 10, 10));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			var rs = stmt.executeQuery("select name from category");
			while (rs.next()) {
				Category cate = new Category(rs.getString(1));
				JPanel tmp = new JPanel(new BorderLayout());
				tmp.setBackground(Color.WHITE);
				tmp.add(cate);
				categoryList.add(tmp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
