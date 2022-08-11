package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MainFrame extends BaseFrame {
	CardLayout card;
	ArrayList<String> history = new ArrayList<>();

	public MainFrame() {
		super("게임유통관리", 900, 650);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(card = new CardLayout()));

		n.add(lbl(BasePage.user == null ? "관리자 메뉴" : "사용자 메뉴", 0, 25), "North");
		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		var logout = lbl("로그아웃", 1, 0, 15, e -> {
			dispose();
		});
		var img = new JLabel(getIcon(BasePage.user == null ? null : BasePage.user.get(8), 25, 25));
		var pop = new JPopupMenu();
		var me = new JMenu("내정보");
		var i1 = new JMenuItem("장바구니");
		var i2 = new JMenuItem("보관함");

		i1.addActionListener(a -> {
			history = new ArrayList<>(history.subList(0, 1));
			new CartPage();
		});
		i2.addActionListener(a -> {
			history = new ArrayList<>(history.subList(0, 1));
			new StoragePage();
		});

		pop.add(me);
		pop.add(i1);
		pop.add(i2);

		for (var cap : "프로필,정보수정".split(",")) {
			var i = new JMenuItem(cap);
			i.addActionListener(a -> {
				history = new ArrayList<>(history.subList(0, 1));

				if (cap.equals("프로필")) {
					new ProfilePage(toInt(BasePage.user.get(0)));
				} else {
					new InfoEditPage();
				}
			});
			me.add(i);
		}

		img.setComponentPopupMenu(pop);

		ne.add(logout);
		ne.add(img);
		ne.add(lbl(BasePage.user == null ? "" : BasePage.user.get(3).toString(), 0));

		setVisible(true);
	}

	void add(BasePage page, String name) {
		if (history.contains(name)) {
			history = new ArrayList<>(history.subList(0, history.indexOf(name) + 1));
		} else {
			c.add(page, name);
			history.add(name);
		}

		swap(name);

		repaint();
		revalidate();
	}

	void swap(String name) {
		nw.removeAll();
		nw.setLayout(new FlowLayout(0));

		history.forEach(na -> {
			var lbl = lbl(na, 0, 1, 15, e -> {
				history = new ArrayList<>(history.subList(0, history.indexOf(na) + 1));
				swap(na);
			});

			nw.add(lbl);

			if (history.indexOf(na) < history.size() - 1) {
				nw.add(lbl(">", 0, 15));
			}
		});

		card.show(c, name);
	}
}
