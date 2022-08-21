package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import tool.Tool;

public class Purchase extends BaseFrame {
	JLabel cnt, price;
	ArrayList<Object> rs;

	public Purchase(int sNo) {
		super("구매", 600, 300);
		user = getRows("select * from user where u_no = 1").get(0);
		rs = getRows("select s_no, s_name, s_explanation,  format(s_price, '#,##0') from store where s_no = ?", sNo)
				.get(0);

		add(new JLabel(getIcon("./datafile/스토어/" + rs.get(1) + ".jpg", 250, 200)), "West");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

		for (int i = 0; i < 6; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			if (i == 0) {
				tmp.add(lbl(rs.get(1).toString(), 2));
			} else if (i == 1) {
				tmp.add(lbl(rs.get(3) + "원", 2, 15));
			} else if (i == 2) {
				tmp.add(sz(lbl("설명", 2, 15), 60, 20));
				tmp.add(lbl("<html><font color='gray'>" + rs.get(2), 2));
			} else if (i == 3) {
				tmp.setLayout(new BorderLayout());

				tmp.add(event(lbl("-", 0), e -> {
					if (toInt(cnt.getText()) == 1) {
						eMsg("최소 1개 이상은 구매해야 합니다.");
						return;
					}
					cnt.setText(toInt(cnt.getText()) - 1 + "");
					setPrice();
				}), "West");
				tmp.add(cnt = lbl("1", 0, 15));
				tmp.add(event(lbl("+", 0), a -> {
					if (toInt(cnt.getText()) == 5) {
						eMsg("최대 5개까지 구매하실 수 있습니다.");
						return;
					}
					cnt.setText(toInt(cnt.getText()) + 1 + "");
					setPrice();
				}), "East");
			} else if (i == 4) {
				tmp.setLayout(new FlowLayout(2, 10, 0));
				tmp.add(lbl("총 상품금액", 2));
				tmp.add(price = lbl(rs.get(3) + "원", 2));
				price.setForeground(Tool.red);
			} else {
				tmp.setLayout(new GridLayout(1, 0, 5, 0));
				tmp.add(btnBlack("취소", a -> dispose()));
				tmp.add(btn("구매하기", a -> {
					if (user == null) {
						eMsg("로그인 후 이용 가능합니다.");
						new Login().addWindowListener(new Before(this));
						return;
					}

					new Payment(rs, toInt(cnt.getText())).addWindowListener(getWindowListeners()[0]);
					setVisible(false);
				}));
			}

			if (i < 3) {
				tmp.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
			} else if (i == 3) {
				tmp.setBorder(new LineBorder(Color.black));
			}

			c.add(tmp);
		}

		setVisible(true);
	}

	private void setPrice() {
		price.setText(new DecimalFormat("#,##0").format(toInt(cnt.getText()) * toInt(rs.get(3))) + "원");
	}

	public static void main(String[] args) {
		new Store();
	}
}
