package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CartPage extends BasePage {
	ArrayList<Item> items = new ArrayList<>();
	JCheckBox chk = new JCheckBox("모두 선택");
	JLabel lblTot;

	public static void main(String[] args) {
		new LoginFrame();
	}

	public CartPage() {
		super("장바구니");

		add(n = new JPanel(new FlowLayout(2)), "North");
		add(new JScrollPane(c = new JPanel()));
		add(s = new JPanel(new BorderLayout()), "South");
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		n.add(chk);

		s.add(lblTot = lbl("<html><font color='black'>총 금액 : 0원", 2, 25), "West");
		s.add(se = new JPanel(new FlowLayout(2)), "East");

		for (var cap : "장바구니 삭제,구매하기".split(",")) {
			se.add(btn(cap, a -> {
				if (cap.equals("장바구니 삭제")) {
					if (items.stream().filter(p -> p.chk.isSelected()).count() < 1) {
						eMsg("삭제할 게임을 선택하세요.");
						return;
					}

					iMsg("삭제가 완료되었습니다.");
					items.stream().filter(p -> p.chk.isSelected())
							.forEach(t -> execute("delete from cart where c_no = ?", t.rs.get(0)));
				} else {
					if (items.stream().filter(p -> p.chk.isSelected()).count() < 1) {
						eMsg("구매할 게임을 선택하세요.");
						return;
					}

					int price = toInt(lblTot.getText());

					if (toInt(user.get(5)) < price) {
						eMsg("굼액이 부족합니다.");
						new ChargeDialog().setVisible(true);
						return;
					}

					items.stream().filter(p -> p.chk.isSelected())
							.forEach(p -> execute("insert into library value(0,?,?,?,?)", p.rs.get(7), user.get(0),
									p.dc ? p.dcPrice : p.price, LocalDate.now()));
					execute("update user set u_money = u_money-? where u_no = ?", price, user.get(0));
					user = getRows("select * from user where u_no = ?", user.get(0)).get(0);

					new InfoDialog(items.stream().filter(p -> p.chk.isSelected()).map(p -> p.rs.get(7))
							.collect(Collectors.toList()));
				}
			}));
		}

		addItem();

		chk.addActionListener(a -> {
			items.stream().map(p -> p.chk).forEach(JCheckBox::doClick);
		});

		s.setOpaque(true);
		s.setBackground(Color.white);
		chk.setForeground(Color.white);
	}

	private void addItem() {
		c.removeAll();
		items.clear();

		for (var rs : getRows(
				"select c_no, g_img, g_name, round(avg(r_score), 1), format(g_price, '#,##0'), g_sale, g_gd, g.g_no from game g, cart c, review r where g.g_no = c.g_no and g.g_no = r.g_no and c.u_no = ? group by g.g_no",
				user.get(0))) {
			var i = new Item(rs);

			c.add(i);
			items.add(i);
		}

		lblTot.setText("<html><font color='black'>총 금액 : 0원");

		mf.repaint();
		mf.revalidate();
	}

	class Item extends JPanel {
		JLabel img;
		JCheckBox chk;
		ArrayList<Object> rs;
		int price, dcPrice;
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
			dcPrice = (int) (price - (price * toInt(rs.get(5)) * 0.01));
			dc = !(toInt(rs.get(6)) == 0 || toInt(rs.get(6)) > u_gd);

			chk.addActionListener(a -> {
				int tot = toInt(lblTot.getText());
				tot += (chk.isSelected() ? 1 : -1) * (dc ? dcPrice : price);
				lblTot.setText("<html><font color='black'>총 금액 : " + format(tot) + "원");

				setBorder(new CompoundBorder(new LineBorder(chk.isSelected() ? Color.red : Color.black),
						new EmptyBorder(5, 5, 5, 5)));
			});

			setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		}
	}
}
