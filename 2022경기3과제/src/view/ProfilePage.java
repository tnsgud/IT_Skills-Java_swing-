
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ProfilePage extends BasePage {
	ArrayList<Object> user;
	DefaultTableModel m = new DefaultTableModel();
	JTable t = new JTable(m);
	JPanel expBar;

	public ProfilePage(int u_no) {
		super("유저프로필");

		user = getRows("select * from user where u_no=?", u_no).get(0);

		var exp = toInt(getOne("select count(*) from library where u_no=?", user.get(0))) * 3
				+ getRows("select * from v2 where u_no = ? group by g_no having count(*) > 2", user.get(0)).size() * 10;

		var money = lbl("보유 잔액 : " + new DecimalFormat("#,##0").format(toInt(user.get(5))), 2, 15);

		add(c = new JPanel(new BorderLayout(20, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(new JLabel(getIcon(user.get(8), 200, 200)), "West");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		cc.add(lbl("닉네임 : " + user.get(3), 2, 15));
		cc.add(money);
		cc.add(lbl("경험치 : " + exp + " [등급 : " + g_gd[exp / 20] + "]", 2, 15));
		cc.add(cs = new JPanel(new FlowLayout(0)));

		var lbl = (JLabel) cc.getComponent(2);
		lbl.setIcon(getIcon("./datafiles/등급사진/" + (exp / 20) + ".jpg", 80, 80));
		lbl.setHorizontalTextPosition(2);

		cs.add(expBar = new JPanel(new FlowLayout(1)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				var next = (exp / 20 + 1) * 20;

				g2.setColor(Color.green);
				g2.fillRect(0, 0, (int)(((double) exp / next) * 100), 30);
			}
		});

		expBar.add(lbl(exp + "/" + ((exp / 20 + 1) * 20), 0));

		sz(expBar, 100, 30);
		expBar.setBorder(new LineBorder(Color.white));

		s.add(lbl("보유한 게임", 2, 20), "North");
		s.add(sz(new JScrollPane(sc = new JPanel(new GridLayout(0, 1, 10, 10))), 1, 200));

		lib();

		money.setVisible(u_no == toInt(BasePage.user.get(0)));

		mf.setJPanelOpaque(this);
	}

	private void lib() {
		sc.removeAll();

		if (toInt(user.get(6)) == 1) {
			sc.add(lbl("비공개", 0, 30));
			return;
		}

		var rs = getRows(
				"select g.g_no, g_img, g_name, g_genre, g_age, round(avg(r_score), 0), format(l_price, '#,##0') from game g, review r, library l where g.g_no = r.g_no and l.g_no = g.g_no and l.u_no = ? group by g.g_no",
				user.get(0));
		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			var text = "<html>게임명 : " + r.get(2) + "<br>장르 : ";
			var list = new ArrayList<>();
			var pop = new JPopupMenu();
			var item = new JMenuItem("환불");

			pop.add(item);

			item.addActionListener(a -> {
				var row = getRows("select l_date, l_price, l_no from library where u_no=? and g_no = ?", user.get(0),
						r.get(0)).get(0);
				var date = LocalDate.parse(row.get(0).toString());

				if (LocalDate.now().minusWeeks(2).isAfter(date)) {
					eMsg("환불 가능한 기간이 지났습니다.");
					return;
				}

				iMsg("환불이 완료되었습니다.");
				execute("update user set u_money=? where u_no=?", toInt(user.get(5)) + toInt(row.get(1)), user.get(0));
				execute("delete from library where l_no= ?", row.get(2));

				var lbl = (JLabel) cc.getComponent(1);
				lbl.setText("보유 잔액 : " + new DecimalFormat("#,##0").format(toInt(user.get(5))));

				lib();
			});

			tmp.setComponentPopupMenu(pop);

			for (var gen : r.get(3).toString().split(",")) {
				list.add(g_genre[toInt(gen)]);
			}

			tmp.setBackground(back);

			text += String.join(",", list.toArray(String[]::new)) + "<br>";
			text += "연령 : " + g_age[toInt(r.get(4))] + "<br>";
			text += "평점 : " + String.format("%.1f", (double) toInt(r.get(5))) + "<br> ";
			text += "가격 : " + (toInt(r.get(6)) == 0 ? "무료" : r.get(6) + "원");

			tmp.add(new JLabel(getIcon(r.get(1), 200, 200)), "West");
			tmp.add(lbl(text, 2));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					tmp.setBorder(new LineBorder(Color.red));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					tmp.setBorder(null);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						g_no = toInt(r.get(0));
						new GamePage();
					}
				}
			});

			sc.add(tmp);
		}
	}
}
