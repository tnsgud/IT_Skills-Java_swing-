package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GamePage extends BasePage {
	ArrayList<Object> game;
	JPanel main = new JPanel(new BorderLayout());
	JTextField txt = new JTextField(20);
	JLabel[] stars = Stream.generate(() -> new JLabel(getIcon("./datafiles/기본사진/빈별.jpg", 50, 50))).limit(5)
			.toArray(JLabel[]::new);
	int score = 1, r_no = 0;

	@Override
	public JButton btn(String c, ActionListener a) {
		var b = super.btn(c, a);
		b.setBackground(null);
		b.setForeground(Color.BLACK);
		return b;
	}

	public GamePage() {
		super("게임페이지");
 
		game = getRows(
				"select *, format(if(g_sale <> 0, g_price*(g_sale*0.01), g_price), '#,##0') from game where g_no = ?",
				g_no).get(0);

		add(new JScrollPane(main));

		main.add(c = sz(new JPanel(new BorderLayout()), 800, 300));
		main.add(s = sz(new JPanel(), 800, 50 * toInt(getOne("select count(*) from review where g_no = ?", g_no))),
				"South");
		s.setLayout(new BoxLayout(s, BoxLayout.PAGE_AXIS));

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(lbl("<html>" + game.get(4), 2));
		c.add(cs = new JPanel(new FlowLayout(2)), "South");

		cn.add(new JLabel(getIcon(game.get(9), 200, 200)), "West");
		cn.add(cc = new JPanel(new GridLayout(0, 1)));

		cc.add(lbl("게임명 : " + game.get(2), 2));
		cc.add(lbl("장르 : " + String.join(",",
				Stream.of(game.get(1).toString().split(",")).map(a -> g_genre[toInt(a)]).toArray(String[]::new)), 2));
		cc.add(lbl("평점 : " + getOne("select round(avg(r_score), 1) from review where g_no = ?", game.get(0)) + "점", 2));
		cc.add(lbl("가격 : " + (toInt(game.get(5)) == 0 ? "무료" : format(toInt(game.get(5))) + "원"), 2));
		cc.add(lbl("연령 : " + g_age[toInt(game.get(3))], 2));

		if (toInt(game.get(6)) != 0) {
			var text = " -> " + game.get(10) + "원(" + game.get(6) + "% 할인중) 대상 : " + g_gd[toInt(game.get(7))];
			var lbl = (JLabel) cc.getComponent(3);
			lbl.setText(lbl.getText() + text);
		}

		if (getOne("select * from library where g_no = ? and u_no = ?", g_no, user.get(0)).isEmpty()) {
			if (toInt(game.get(5)) == 0) {
				cs.add(btn("구매하기", a -> {
					new InfoDialog(Stream.of(g_no).collect(Collectors.toList()));
				}));
			} else {
				for (var cap : "장바구니에 추가,구매하기".split(",")) {
					cs.add(btn(cap, a -> {
						if (cap.equals("장바구니에 추가")) {
							if (!getOne("select * from cart where u_no = ? and g_no = ?", user.get(0), game.get(0))
									.isEmpty()) {
								eMsg("장바구니에 있는 게임입니다.");
								return;
							}

							iMsg("장바구니에 추가가 완료되었습니다.");
							execute("insert into cart values(0, ? ,?)", user.get(0), game.get(0));
							new CartPage();
						} else {
							int price = toInt(game.get(7)) <= u_gd && toInt(game.get(6)) != 0 ? toInt(game.get(6))
									: toInt(game.get(5));

							if (toInt(user.get(5)) < price) {
								eMsg("금액이 부족합니다.");
								new ChargeDialog().setVisible(true);
								return;
							}
							execute("insert into library values(0, ?, ?, ?, ?)", user.get(0), g_no, price,
									LocalDate.now());
							execute("update user set u_money=u_money-? where u_no = ?", price, user.get(0));
							user = getRows("select * from user where u_no= ?", user.get(0)).get(0);

							new InfoDialog(
									game.stream().filter(g -> game.indexOf(g) == 0).collect(Collectors.toList()));
							
							new GamePage();
						}
					}));
				}
			}
		} else {
			var review = getRows("select * from review where u_no = ? and g_no = ?", user.get(0), g_no);
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
							r_no);
				}
				
				setReview();
			});

			cs.setLayout(new FlowLayout(1));

			cs.add(lbl("리뷰", 0, 15));
			cs.add(txt);

			for (int i = 0; i < stars.length; i++) {
				cs.add(stars[i]);
				stars[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						Stream.of(stars).forEach(x -> x.setIcon(getIcon("./datafiles/기본사진/빈별.jpg", 50, 50)));

						var me = (JLabel) e.getSource();
						var idx = Arrays.asList(stars).indexOf(me);
						score = idx + 1;
						for (int j = 0; j < score; j++) {
							stars[j].setIcon(getIcon("./datafiles/기본사진/별.jpg", 50, 50));
						}
					}
				});
			}

			cs.add(btn);

			if (review.isEmpty()) {
				btn.setText("등록");
			} else {
				var r = review.get(0);

				txt.setText(r.get(4).toString());
				btn.setText("수정");

				score = toInt(r.get(3));
			}

			for (int i = 0; i < score; i++) {
				stars[i].setIcon(getIcon("./datafiles/기본사진/별.jpg", 50, 50));
			}
		}

		setReview();

		c.setBorder(new EmptyBorder(5, 5, 5, 5));
	}
	
	void setReview() {
		s.removeAll();
		
		for (var rs : getRows(
				"select u.u_no, u_img, u_name, r_score, r_content, r_no from review r, user u where r.u_no = u.u_no and g_no = ?",
				g_no)) {
			var tmp = new JPanel(new BorderLayout());

			r_no = toInt(rs.get(0)) == toInt(user.get(0)) ? toInt(rs.get(5)) : r_no;

			tmp.add(new JLabel(getIcon(rs.get(1), 50, 50)) {
				@Override
				public void setOpaque(boolean isOpaque) {
					setBackground(Color.white);
					super.setOpaque(true);
				}
			}, "West");
			tmp.add(lbl(String.format("<html><font color='%s'>유저명 : %s<br>평점 : %s점<br>%s",
					toInt(rs.get(0)) == toInt(user.get(0)) ? "yellow" : "white", rs.get(2).toString(),
					rs.get(3).toString(), rs.get(4).toString()), 2));
			tmp.add(new JLabel(
					getIcon(toInt(rs.get(3)) < 3 ? "./datafiles/기본사진/싫어요.jpg" : "./datafiles/기본사진/좋아요.jpg", 50, 50)),
					"East");

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new ProfilePage(toInt(rs.get(0)));
				}
			});

			tmp.setBorder(new LineBorder(Color.white));

			s.add(sz(tmp, 300, 50));
		}
		
		mf.repaint();
		mf.revalidate();

		s.setOpaque(true);
		s.setBackground(Color.black);
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}
