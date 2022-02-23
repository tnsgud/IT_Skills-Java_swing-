import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Purchase extends BaseFrame {

	JLabel chk;
	JButton btn;

	public Purchase(HashMap<String, Stage.Item> items, int sum) {
		super("결제", 500, 500);

		this.add(lbl("결제", 0, 30), "North");
		this.add(c = new JPanel(new GridLayout(0, 1)));
		c.add(new JLabel("공연명 : " + getone("select p_name from perform where p_no = " + pno)));
		c.add(new JLabel("장소 : " + getone("select p_place from perform where p_no = " + pno)));
		c.add(new JLabel("날짜 : " + getone("select p_date from perform where p_no = " + pno)));
		for (var k : items.keySet()) {
			c.add(new JLabel(k + " : " + df.format(items.get(k).price)));
		}
		c.add(new JLabel("총금액 : " + df.format(sum)));

		var tmp = new JPanel(new FlowLayout(0));
		c.add(tmp);
		tmp.add(btn = btn("본인 인증", e -> {
			String input = JOptionPane.showInputDialog("비밀번호를 입력해주세요.");

			if (input.equals(getone("select u_pw from user where u_pw = '" + input + "'"))) {
				chk.setVisible(true);
			}
			btn.setEnabled(false);
		}));
		tmp.add(chk = new JLabel("V"));

		var tmp2 = new JPanel(new GridLayout(1, 2, 5, 5));
		c.add(tmp2);
		tmp2.add(btn("결제하기", e -> {
			iMsg("결제가 완료되었습니다.");
		}));
		tmp2.add(btn("취소", e -> dispose()));

		chk.setVisible(false);
		chk.setForeground(Color.GREEN);
		this.setVisible(true);
	}

}
