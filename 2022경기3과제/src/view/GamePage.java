package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class GamePage extends BasePage {
	JPanel main = new JPanel(new BorderLayout());
	ArrayList<Object> game;
	JTextField txt = new JTextField(20);
	JLabel stars[] = new JLabel[5];
	int score = 1;

	public GamePage() {
		super("게임페이지");

		game = getRows(
				"select *, format(if(g_sale <> 0, g_price*(g_sale*0.01), g_price), '#,##0') from game where g_no=?",
				g_no).get(0);

		add(new JScrollPane(main));

		main.add(n = sz(new JPanel(new BorderLayout()), 800, 600), "North");
		main.add(c = sz(new JPanel(), 800, 50 * toInt(getOne("select count(*) from review where g_no = ?", g_no))));
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

		n.add(nn = new JPanel(new BorderLayout()), "North");
		n.add(lbl("<html>" + game.get(4), 2));
		n.add(ns = new JPanel(new FlowLayout(2)), "South");

		nn.add(new JLabel(getIcon(game.get(9), 300, 300)), "West");
		nn.add(nc = new JPanel(new GridLayout(0, 1)));

		nc.add(lbl("게임명 : " + game.get(2), 2));
		nc.add(lbl("장르 : " + String.join(",",
				Stream.of(game.get(1).toString().split(",")).map(a -> g_genre[toInt(a)]).toArray(String[]::new)), 2));
		nc.add(lbl("평점 : " + getOne("select round(avg(r_score), 1) from review where g_no = ?", game.get(0)) + "점", 2));
		nc.add(lbl("가격 : " + (toInt(game.get(5)) == 0 ? "무료" : format(toInt(game.get(5))) + "원"), 2));
		nc.add(lbl("연령 : " + g_age[toInt(game.get(3))], 2));

		if (toInt(game.get(6)) != 0) {
			var text = " -> " + game.get(10) + "원(" + game.get(6) + "% 할인중) 대상 : " + g_gd[toInt(game.get(7))];
			var lbl = (JLabel) nc.getComponent(3);
			lbl.setText(lbl.getText() + text);
		}

		if (getOne("select * from library where g_no = ? and u_no = ?", game.get(0), user.get(0)).isEmpty()) {
			ns.setLayout(new FlowLayout(2));
			if (toInt(game.get(5)) == 0) {
				ns.add(btn("구매하기", a -> {

				}));
			} else {
				for (var cap : "장바구니에 추가,구매하기".split(",")) {
					ns.add(btn(cap, a -> {
						if (cap.equals("장바구니에 추가")) {
							if (!getOne("select * from cart where u_no = ? and g_no = ?", user.get(0), game.get(0))
									.isEmpty()) {
								eMsg("장바구니에 있는 게임입니다.");
								return;
							}

							iMsg("장바구니에 추가가 완료되었습니다.");
							execute("insert into cart values(0, ? ,?)", user.get(0), game.get(0));
						} else {
							int price = toInt(game.get(7)) <= u_gd ? toInt(game.get(6)) : toInt(game.get(5));

							if (toInt(user.get(5)) < price) {
								eMsg("금액이 부족합니다.");
								new Charge();
								return;
							}
							execute("insert into library values(0, ?, ?, ?)", user.get(0), g_no, price,
									LocalDate.now());
							execute("update user set u_money=u_money-? where u_no = ?", price, user.get(0));
							user = getRows("select * from user where u_no= ?", user.get(0)).get(0);

							new Info(game.stream().filter(g -> game.indexOf(g) == 0).collect(Collectors.toList()));
						}
					}));
				}
			}
		} else {
			var review = getRows("select * from review where u_no = ? and g_no = ?", user.get(0), game.get(0));
			var btn = btn("", a -> {
				if (txt.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}

				iMsg("리뷰 " + a.getActionCommand() + "이 완료되었습니다.");

				if (a.getActionCommand().equals("등록")) {
					execute("insert into review values(0, ?, ?, ?, ?)", user.get(0), game.get(0), score, txt.getText());
				} else {
					execute("update review set r_score=?, r_content=? where r_no = ?", score, txt.getText(),
							game.get(0));
				}
			});

			ns.setLayout(new FlowLayout(1));

			ns.add(lbl("리뷰", 0, 15));
			ns.add(txt);

			for (int i = 0; i < stars.length; i++) {
				ns.add(stars[i] = new JLabel(getIcon("./datafiles/기본사진/" + (i == 0 ? "별" : "빈별") + ".jpg", 50, 50)));
				stars[i].addMouseListener(new MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e) {
						for (int j = 0; j < stars.length; j++) {
							stars[j].setIcon(getIcon("./datafiles/기본사진/빈별.jpg", 50, 50));
						}

						var idx = Arrays.asList(stars).indexOf((JLabel) e.getSource()) + 1;
						score = idx;
						for (int j = 0; j < idx; j++) {
							stars[j].setIcon(getIcon("./datafiles/기본사진/별.jpg", 50, 50));
						}
					};
				});
			}

			ns.add(btn);

			if (review.isEmpty()) {
				btn.setText("등록");
			} else {
				var r = review.get(0);

				txt.setText(r.get(4).toString());

				btn.setText("수정");

				score = toInt(r.get(3));

				for (int i = 0; i < toInt(r.get(3)); i++) {
					stars[i].setIcon(getIcon("./datafiles/기본사진/별.jpg", 50, 50));
				}
			}
		}

		for (var rs : getRows(
				"select u.u_no, u_img, u_name, r_score, r_content from review r, user u where r.u_no = u.u_no and g_no = ?",
				g_no)) {
			var tmp = new JPanel(new BorderLayout());

			tmp.setName(rs.get(0).toString());

			tmp.add(new JLabel(getIcon(rs.get(1), 50, 50)), "West");
			tmp.add(lbl(String.format("<html><font color='%s'>유저명 : %s<br>평점 : %s점<br>%s",
					toInt(rs.get(0)) == toInt(user.get(0)) ? "yellow" : "white", rs.get(2).toString(),
					rs.get(3).toString(), rs.get(4).toString()), 2));
			tmp.add(new JLabel(
					getIcon(toInt(rs.get(3)) < 3 ? "./datafiles/기본사진/싫어요.jpg" : "./datafiles/기본사진/좋아요.jpg", 50, 50)),
					"East");

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JPanel) e.getSource();

					new ProfilePage(toInt(me.getName()));
				}
			});

			tmp.setBorder(new LineBorder(Color.white));

			c.add(sz(tmp, 300, 50));
		}

		mf.repaint();

		for (var com : c.getComponents()) {
			((JComponent) com).setOpaque(true);
			((JComponent) com).setBackground(Color.black);
		}
	}
}
