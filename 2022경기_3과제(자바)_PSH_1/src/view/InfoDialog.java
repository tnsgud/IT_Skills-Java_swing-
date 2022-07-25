package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InfoDialog extends BaseDialog {
	public InfoDialog(List<Object> g_nos) {
		super("정보", 300, 300);

		add(new JScrollPane(n = new JPanel(new GridLayout(0, 1))), "North");
		add(lbl("결제가 완료되었습니다.", 0, 20));
		add(lbl("보유 잔액:" + format(toInt(BasePage.user.get(5))) + "원", 0), "South");

		for (var gNo : g_nos) {
			var rs = getRows("select * from item where g_no=? order by rand() limit 1", gNo);
			if (rs.isEmpty())
				continue;

			var r = rs.get(0);
			var tmp = new JPanel(new BorderLayout());

			tmp.add(new JLabel(getIcon(r.get(3), 50, 50)), "West");
			tmp.add(lbl("아이템명 : " + r.get(2), 2, 15));

			n.add(tmp);
		}

		setVisible(true);
	}
}
