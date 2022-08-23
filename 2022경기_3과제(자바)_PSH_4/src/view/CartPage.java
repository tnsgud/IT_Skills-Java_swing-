package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CartPage extends BasePage {
	ArrayList<Item> itemList = new ArrayList<>();
	JCheckBox chk = new JCheckBox("모두 선택");
	JLabel lblTot;

	public CartPage() {
		super("장바구니");

		add(n = new JPanel(), "North");
		add(new JScrollPane(c = new JPanel()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(c);

		s.add(lblTot = lbl("<html><font color='black'>총 금액 : 0원", 2, 25), "North");
		s.add(se = new JPanel(new FlowLayout(2)), "East");

		for (var cap : "장바구니 삭제,구매하기".split(",")) {
			se.add(btn(cap, a -> {
				if (itemList.stream().filter(p -> p.chk.isSelected()).count() < 1) {
					eMsg((cap.equals("구매하기") ? "구매" : "삭제") + "할 게임을 선택하세요.");
					return;
				}

				if (cap.equals("장바구니 삭제")) {
					iMsg("삭제가 완료되었습니다.");
					itemList.stream().filter(p -> p.chk.isSelected())
							.forEach(p -> execute("delete from cart where c_no = ?", p.rs.get(0)));
				} else {
					int price = toInt(lblTot.getText());

					if (toInt(user.get(5)) < price) {
						eMsg("굼액이 부족합니다.");
						new ChargeDialog().setVisible(true);
						return;
					}

					itemList.stream().filter(p -> p.chk.isSelected()).forEach(p -> {
						execute("insert library values(0,?,?,?,?)", user.get(0), p.rs.get(7),
								p.dc ? p.dcprice : p.price, LocalDate.now().toString());
						execute("delete from cart where c_no = ?", p.rs.get(0));
					});
					user = getRows("select * from user where no = ?", user.get(0)).get(0);
					new InfoDialog(itemList.stream().filter(p -> p.chk.isSelected()).map(p -> p.rs.get(7))
							.collect(Collectors.toList()));
				}
				addItem();
			}));
		}

		chk.addActionListener(a -> itemList.stream().map(p -> p.chk).forEach(JCheckBox::doClick));
		addItem();
	}

	private void addItem() {
		c.removeAll();
		itemList.clear();

		for (var rs : getRows(
				"select c_no, g_img, g_name, round(avg(r_score), 1), format(g_price, '#,##0'), g_sale, g_gd, g.g_no from game g, cart c, review r where g.g_no = c.g_no and g.g_no = r.g_no and c.u_no = ? group by g.g_no",
				user.get(0))) {
			var i = new Item(rs);

			c.add(i);
			itemList.add(i);
		}

		mf.repaint();
		mf.revalidate();

		lblTot.setText("<html><font color='black'>총 금액 : 0원");
		s.setOpaque(true);
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

			add(img = new JLabel(getIcon(rs.get(1), 100, 100)), "West");
			add(c);
			add(chk = new JCheckBox(), "East");

			c.add(lbl("게임명 : " + rs.get(2), 2, 15));
			c.add(lbl("평점 : " + rs.get(3), 2, 15));
			c.add(lbl("가격 : " + rs.get(4)
					+ (toInt(rs.get(6)) != 0 ? String.format(" -> %s원(%s%% 할인중) 대상 : %s↑",
							format((int) (toInt(rs.get(4)) - (toInt(rs.get(4)) * toInt(rs.get(5)) * 0.01))),
							rs.get(5).toString(), g_gd[toInt(rs.get(6))]) : ""),
					2, 15));

			price = toInt(rs.get(4));
			dcprice = (int) (price - (price * toInt(rs.get(5)) * 0.01));
			dc = !(toInt(rs.get(6)) == 0 || toInt(rs.get(6)) > uGd);

			chk.addActionListener(a -> {
				int tot = toInt(lblTot.getText());
				tot += (chk.isSelected() ? 1 : -1) * (dc ? dcprice : price);
				lblTot.setText("<html><font color='black'>총 금액 : " + format(tot) + "원");

				setBorder(new CompoundBorder(new LineBorder(chk.isSelected() ? Color.red : Color.black),
						new EmptyBorder(5, 5, 5, 5)));
			});
			
			setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		}
	}
}
