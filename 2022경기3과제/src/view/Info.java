package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class Info extends BaseFrame {
	public Info(ArrayList<Object> game) {
		super("정보", 400, 300);

		var rs = getRows("select * from item where g_no = ?", BasePage.g_no);

		var price = toInt(game.get(6)) != 0 ? toInt(game.get(10)) : toInt(game.get(5));

		add(lbl("보유 잔액 : " + format(toInt(BasePage.user.get(5)) - price) + "원", 0));
		add(lbl("결제가 완료되었습니다.", 0, 30), "South");

		if (!rs.isEmpty()) {
			add(new JScrollPane(n = new JPanel(new GridLayout(0, 1))), "North");

			for (var r : rs) {
				var tmp = new JPanel(new BorderLayout());

				tmp.add(new JLabel(getIcon(r.get(3), 50, 50)), "West");
				tmp.add(lbl("아이템명 : " + r.get(2), 2, 15));

				tmp.setBorder(new LineBorder(Color.black));

				n.add(tmp);
			}

			var idx = new Random().nextInt(rs.size());

			execute("insert into storage values(0, ?, ?)", BasePage.user.get(0), rs.get(idx).get(0));
		}

		execute("update user set u_money=? where u_no = ?", toInt(BasePage.user.get(5)) - price, BasePage.user.get(0));
		execute("insert into library values(0, ?, ?, ?, ?)", BasePage.user.get(0), BasePage.g_no, price,
				LocalDate.now());

		setVisible(true);
	}
}
