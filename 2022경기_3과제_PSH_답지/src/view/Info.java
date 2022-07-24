package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Info extends BaseDialog {
	public Info(List<Object> g_nos) {
		super("정보", 300, 300);

		add(new JScrollPane(n = new JPanel(new GridLayout(0, 1))), "North");
		add(lbl("결제가 완료되었습니다.", 0, 20));
		add(lbl("보유 잔액:" + format(toInt(BasePage.user.get(5))) + "원", 0), "South");

		for (var gNo : g_nos) {
			var rs = getRows("select i_img,i_name from item where g_no=?", gNo).get(0);

			var tmp = new JPanel(new BorderLayout());

			tmp.add(new JLabel(getIcon(rs.get(3), 50, 50)), "West");
			tmp.add(lbl("아이템명 : " + rs.get(2), 2, 15));

			n.add(tmp);
		}
		
		

//		var rs = getRows("select * from item where g_no = ?", BasePage.g_no);
//
//		var price = toInt(game.get(6)) != 0 ? toInt(game.get(10)) : toInt(game.get(5));
//
//		add(lbl("보유 잔액 : " + format(toInt(BasePage.user.get(5)) - price) + "원", 0));
//		add(lbl("결제가 완료되었습니다.", 0, 30), "South");
//
//		if (!rs.isEmpty()) {
//			add(new JScrollPane(n = new JPanel(new GridLayout(0, 1))), "North");
//
//			for (var r : rs) {
//				var tmp = new JPanel(new BorderLayout());
//
//				tmp.add(new JLabel(getIcon(r.get(3), 50, 50)), "West");
//				tmp.add(lbl("아이템명 : " + r.get(2), 2, 15));
//
//				tmp.setBorder(new LineBorder(Color.black));
//
//				n.add(tmp);
//			}
//
//			var idx = new Random().nextInt(rs.size());
//
//			execute("insert into storage values(0, ?, ?)", BasePage.user.get(0), rs.get(idx).get(0));
//		}
//
//		execute("update user set u_money=? where u_no = ?", toInt(BasePage.user.get(5)) - price, BasePage.user.get(0));
//		execute("insert into library values(0, ?, ?, ?, ?)", BasePage.user.get(0), BasePage.g_no, price,
//				LocalDate.now());

		setVisible(true);
	}
}
