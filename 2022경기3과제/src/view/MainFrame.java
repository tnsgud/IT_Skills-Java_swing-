package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

	public MainFrame() {
		super("게임유통관리", 900, 700);

		BasePage.user = getRows("select * from user where u_no = 1").get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(card = new CardLayout()));

		n.add(lbl("사용자 메뉴", 0, 25), "North");
		n.add(nw = new JPanel(new FlowLayout(0, 5, 5)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		var logout = lbl("로그아웃", 0, 1, 13, e->{
			this.dispose();
		});
		var img = new JLabel(getIcon(BasePage.user.get(8), 25, 25));

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
						new ProfilePage();
					} else {
						new InfoEditPage();
					}
				});
				menu.add(i);
			}

			cart.addActionListener(a->{
				new CartPage();
			});
			
			storage.addActionListener(a->{
				new StoragePage();
			});
			
			img.setComponentPopupMenu(pop);
		}

		
		ne.add(logout);
		ne.add(img);
		ne.add(lbl(BasePage.user.get(3).toString(), 0));

		getContentPane().setBackground(new Color(51, 63, 112));

		n.setOpaque(false);
		c.setOpaque(false);
		nw.setOpaque(false);
		ne.setOpaque(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

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
	
	void setJPanelOpaque(JComponent com) {
		for (var c : com.getComponents()) {
			if (c instanceof JComponent) {
				((JComponent) c).setOpaque(false);
				setJPanelOpaque((JComponent) c);
			}
		}		
	}
}
