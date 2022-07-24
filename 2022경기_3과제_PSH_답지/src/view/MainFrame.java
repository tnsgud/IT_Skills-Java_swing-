package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

public class MainFrame extends BaseFrame {

	CardLayout card;
	ArrayList<JLabel> history = new ArrayList<>();

	void createV() {
		execute("drop view if exists v1");
		execute("create view v1 as select g.g_no, g_img, g_name, g_genre, g_age, round(avg(r_score), 1) as g_review , format(g_price, '#,##0') g_price, g_sale, format(g_price-g_price*g_sale*0.01, '#,##0') g_dcprice from game g, review r where g.g_no = r.g_no group by g.g_no");
		execute("drop view if exists v2");
		execute("create view v2 as select s.s_no, s.u_no, g_no from storage s left join market m on s.s_no = m.s_no inner join item i on s.i_no = i.i_no where m.m_no is null");
	}

	public MainFrame() {
		super("게임유통관리", 900, 600);

//		BasePage.user = getRows("select * from user where u_no=1").get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(card = new CardLayout()));

		n.add(lbl("사용자 메뉴", 0, 25), "North");
		n.add(nw = new JPanel(new FlowLayout(0, 5, 5)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		var logout = lbl("로그아웃", 0, 1, 13, e -> {
			this.dispose();
		});
		var img = BasePage.user == null ? new JLabel() : new JLabel(getIcon(BasePage.user.get(8), 25, 25));

		{
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
					if (cap.equals("프로필")) {
						new ProfilePage(toInt(BasePage.user.get(0)));
					} else {
						new InfoEditPage();
					}
				});
				menu.add(i);
			}

			cart.addActionListener(a -> {
				new CartPage();
			});

			storage.addActionListener(a -> {
				new StoragePage();
			});

			img.setComponentPopupMenu(pop);
		}

		ne.add(logout);
		ne.add(img);
		if(BasePage.user != null) {
			ne.add(lbl(BasePage.user.get(3).toString(), 0));
		}

        getContentPane().setBackground(new Color(51, 63, 112));
		
		n.setOpaque(false);
		c.setOpaque(false);
		nw.setOpaque(false);
		ne.setOpaque(false);

		createV();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				BasePage.user = null;
			}
		});

		setVisible(true);
	}

	void addPage(BasePage page, String name) {
		c.add(page, name);

		swapPage(name);

		repaint();
		revalidate();
	}

	void swapPage(String name) {
		nw.removeAll();
		nw.setLayout(new FlowLayout(0, 5, 5));

		var l = lbl(name, 0, 15);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				history = new ArrayList<>(history.subList(0, history.indexOf(l)));
				swapPage(name);
			}
		});

		history.add(l);
		history.forEach(a -> {
			nw.add(a);

			if (history.indexOf(a) < history.size() - 1) {
				nw.add(lbl(">", 0, 15));
			}
		});

		card.show(c, name);

		setJPanelOpaque(c);
	}
}
