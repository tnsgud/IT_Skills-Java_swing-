package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ProfilePage extends BasePage {
	boolean isMe;
	ArrayList<Object> user;
	JPanel expBar;

	public static void main(String[] args) {
		new LoginFrame();
	}

	public ProfilePage(int uNo) {
		super("유저프로필");

		user = getRows("select * from user where u_no = ?", uNo).get(0);

		isMe = toInt(user.get(0)) == toInt(BasePage.user.get(0));
		var exp = toInt(getOne("select count(*) from library where u_no = ?", uNo)) * 3
				+ getRows("select * from v2 where u_no = ? group by g_no having count(*) > 2", uNo).size() * 10;

		add(c = new JPanel(new BorderLayout(20, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(new JLabel(getIcon(user.get(8), 200, 200)), "West");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		cc.add(lbl("닉네임 : " + user.get(3), 2, 15));
		if (isMe) {
			cc.add(lbl("보유 잔액 : " + format(toInt(user.get(5))), 2, 15));
		}
		cc.add(lbl("경험치 : " + exp + " [등급 : " + g_gd[exp / 20] + "]", 2, 15));
		cc.add(cs = new JPanel(new FlowLayout(0)));

		var l = (JLabel) cc.getComponent(2);
		l.setIcon(getIcon("./datafiles/등급사진/" + (exp / 20) + ".jpg", 80, 80));
		l.setHorizontalTextPosition(2);

		cs.add(expBar = sz(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;
				var next = (exp / 20 + 1) * 20;

				g2.setColor(Color.green);
				g2.fillRect(0, 0, (int) ((double) exp / next * 100), 30);
			}
		}, 100, 30));
		expBar.add(lbl(exp + "/" + ((exp / 20 + 1) * 20), 0));

		lib();

		expBar.setBorder(new LineBorder(Color.white));

		mf.repaint();
	}

	private void lib() {
		s.removeAll();
		
		if (toInt(user.get(6)) == 1) {
			s.add(lbl("비공개", 0, 30));
			return;
		}

		s.add(lbl("보유한 게임", 2, 30));
		s.add(sz(new JScrollPane(sc = new JPanel(new GridLayout(0, 1))), 1, 200));

		for (var rs : getRows(
				"select g.g_no, g_img, g_name, g_genre, g_age, round(avg(r_score), 1), format(l_price, '#,##0') from game g, review r, library l where g.g_no = r.g_no and g.g_no = l.g_no and l.u_no = ? group by g.g_no",
				user.get(0))) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			var text = "<html>게임명 : " + rs.get(2) + "<br>";
			var list = Stream.of(rs.get(3).toString().split(",")).map(a -> g_genre[toInt(a)])
					.collect(Collectors.toList());
			var pop = new JPopupMenu();
			var it = new JMenuItem("환불");

			text += "장르 : " + String.join(",", list.toArray(String[]::new)) + "<br>";
			text += "연령	 : " + g_age[toInt(rs.get(4))] + "<br>";
			text += "평점 : " + String.format("%.1f", (double) toInt(rs.get(5))) + "<br>";
			text += "가격 : " + (toInt(rs.get(6)) == 0 ? "무료" : rs.get(6) + "원");

			tmp.add(new JLabel(getIcon(rs.get(1), 100, 100)), "West");
			tmp.add(lbl(text, 2, 15));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					var me = (JPanel) e.getSource();

					setCursor(new Cursor(12));
					me.setBorder(new CompoundBorder(new LineBorder(Color.red), me.getBorder()));
				};

				@Override
				public void mouseExited(MouseEvent e) {
					var me = (JPanel) e.getSource();

					me.setBorder(new EmptyBorder(5, 5, 5, 5));
				};

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						g_no = toInt(rs.get(0));
						new GamePage();
					}
				};
			});

			pop.add(it);

			it.addActionListener(a -> {
				var data = getRows("select * from library where u_no = ? and g_no = ?", user.get(0), rs.get(0)).get(0);
				var date = LocalDate.parse(data.get(4).toString());

				if (LocalDate.now().minusWeeks(2).isAfter(date)) {
					eMsg("환불 가능한 기간이 지났습니다.");
					return;
				}

				iMsg("환불이 완료되었습니다.");
				execute("update user set u_money=u_money+? where u_no = ?", toInt(data.get(3)), user.get(0));
				execute("delete from library where l_no = ?", data.get(0));
				
				lib();
			});

			tmp.setBorder(new EmptyBorder(5, 5, 5, 5));
			tmp.setComponentPopupMenu(isMe ? pop : null);

			sc.add(tmp);
		}
		
		mf.repaint();
		mf.revalidate();
	}
}
