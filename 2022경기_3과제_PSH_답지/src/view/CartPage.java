package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class CartPage extends BasePage {
	ArrayList<Item> items = new ArrayList<>();
	JCheckBox selectAll = new JCheckBox("모두 선택");
	JLabel totPrcie;

	public CartPage() {
		super("장바구니");

		add(n = new JPanel(new FlowLayout(2)), "North");
		add(new JScrollPane(c = new JPanel()));
		add(s = new JPanel(new BorderLayout()), "South");

		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

		n.add(selectAll);
		selectAll.setForeground(Color.white);

		selectAll.addItemListener(i -> {
			for (var c : items) {
				c.chk.setSelected(i.getStateChange() == ItemEvent.SELECTED);
			}
		});

		s.add(totPrcie = lbl("총 금액: 0원", 2, 25), "West");
		s.add(se = new JPanel(new FlowLayout(2)), "East");

		for (var cap : "장바구니 삭제,구매하기".split(",")) {
			se.add(btn(cap, a -> {
				if (cap.equals("구매하기")) {
					int price = toInt(totPrcie.getText());

					if (toInt(user.get(5)) < price) {
						eMsg("금액이 부족합니다.");
						new Charge().setVisible(true);
						return;
					}

					items.stream().filter(i -> i.chk.isSelected()).forEach(i -> {
						execute("insert into library values(0, ?, ?, ?)", user.get(0), i.rs.get(0),
								i.dc ? i.dcprice : i.price, LocalDate.now());
					});

					execute("update user set u_money=u_money-? where u_no = ?", price, user.get(0));
					user = getRows("select * from user where u_no=?", user.get(0)).get(0);

					new Info(items.stream().map(i -> i.rs.get(0)).collect(Collectors.toList()));
				} else {
					if (items.stream().filter(i -> i.chk.isSelected()).count() == 0) {
						eMsg("삭제할 게임을 선택해주세요.");
						return;
					}

					iMsg("삭제가 완료되었습니다.");
					items.stream().filter(i -> i.chk.isSelected()).forEach(i -> {
						execute("delete from cart where u_no = ? and g_no = ?", user.get(0), i.rs.get(0));
					});
					addItem();
				}
			}));
		}

		addItem();

		mf.repaint();
	}

	void addItem() {
		c.removeAll();
		items.clear();
		for (var rs : getRows(
				"select g.g_no, g_img, g_name, round(avg(r_score), 1), format(g_price, '#,##0'), g_sale, g_gd from cart c, game g, review r where c.g_no =g.g_no and g.g_no = r.g_no and c.u_no = ? group by g.g_no",
				user.get(0))) {
			var item = new Item(rs);
			c.add(sz(item, 800, 150));
			items.add(item);
		}

		totPrcie.setText("총 금액: 0원");

		mf.repaint();
		mf.revalidate();
	}

	class Item extends JPanel {
		JLabel img;
		JCheckBox chk;
		ArrayList<Object> rs;
		int price, dcprice;
		boolean dc = false;

		public Item(ArrayList<Object> rs) {
			super(new BorderLayout());

			this.rs = rs;

			var c = new JPanel(new GridLayout(0, 1));
			img = new JLabel(getIcon(rs.get(1), 150, 150));
			chk = new JCheckBox();

			c.setOpaque(false);

			add(img, "West");
			add(c);
			add(chk, "East");

			setBorder(new LineBorder(Color.black));

			c.add(lbl(String.format("게임명 : %s", rs.get(2).toString()), 2, 15));
			c.add(lbl(String.format("평점 : %s점", rs.get(3).toString()), 2, 15));
			c.add(lbl("가격 : " + rs.get(4)
					+ (toInt(rs.get(6)) != 0 ? String.format(" -> %s원(%s%% 할인중) 대상 : %s↑",
							format((int) (toInt(rs.get(4)) - (toInt(rs.get(4)) * toInt(rs.get(5)) * 0.01))),
							rs.get(5).toString(), g_gd[toInt(rs.get(6))]) : ""),
					2, 15));

			price = toInt(rs.get(4));
			dcprice = (int) (price - (price * toInt(rs.get(5)) * 0.01));
			dc = !(toInt(rs.get(6)) == 0 || toInt(rs.get(6)) > u_gd);

			chk.setForeground(Color.white);

			chk.addItemListener(i -> {
				int tot = toInt(totPrcie.getText());
				if (i.getStateChange() == ItemEvent.SELECTED) {
					setBorder(new LineBorder(Color.red));
					tot += dc ? dcprice : price;
				} else {
					setBorder(new LineBorder(Color.black));
					tot -= dc ? dcprice : price;
				}

				totPrcie.setText("총 금액:" + format(tot) + "원");
			});
		}
	}
}
