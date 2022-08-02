package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

public class MainFrame extends BaseFrame {
	CardLayout card;
	ArrayList<String> history = new ArrayList<>();

	public MainFrame() {
		super("게임유통관리", 900, 650);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(card = new CardLayout()));

		n.add(lbl("사용자 메뉴", 0, 25), "North");
		n.add(nw = new JPanel(new FlowLayout(0, 5, 5)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		var logout = lbl("로그아웃", 0, 1, 13, e -> {
			this.dispose();
		});
		var img = new JLabel(BasePage.user == null ? null : getIcon(BasePage.user.get(8), 25, 25));

		var pop = new JPopupMenu();
		var menu = new JMenu("내정보");
		var cart = new JMenuItem("장바구니");
		var storage = new JMenuItem("보관함");

		pop.add(menu);
		pop.add(cart);
		pop.add(storage);

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
			menu.add(i);
		}

		cart.addActionListener(a -> {
			history = new ArrayList<>(history.subList(0, 1));
			new CartPage();
		});
		storage.addActionListener(a -> {
			history = new ArrayList<>(history.subList(0, 1));
			new StorageAndMarketPage("보관함");
		});

		img.setComponentPopupMenu(pop);

		ne.add(logout);
		ne.add(img);

		if (BasePage.user != null) {
			ne.add(lbl(BasePage.user.get(3).toString(), 0));
		}

		getContentPane().setBackground(back);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				BasePage.user = null;
			};
		});

		setVisible(true);
	}

	void addPage(BasePage page, String name) {
		c.add(page, name);

		if (history.contains(name)) {
			history = new ArrayList<>(history.subList(0, history.indexOf(name) + 1));
		} else {
			history.add(name);
		}

		swapPage(name);

		repaint();
		revalidate();
	}

	void swapPage(String name) {
		nw.removeAll();
		nw.setLayout(new FlowLayout(0, 5, 5));

		history.forEach(a -> {
			var l = lbl(a, 0, 1, 15, e -> {
				var text = ((JLabel) e.getSource()).getText();
				history = new ArrayList<>(history.subList(0, history.indexOf(text) + 1));
				swapPage(text);
			});

			nw.add(l);

			if (history.indexOf(a) < history.size() - 1) {
				nw.add(lbl(">", 0, 15));
			}
		});

		card.show(c, name);

		c.repaint();
		c.revalidate();
	}
}
