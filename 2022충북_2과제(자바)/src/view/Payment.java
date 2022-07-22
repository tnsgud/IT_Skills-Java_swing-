package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Payment extends BaseFrame {
	public Payment(String name, int cnt, int price, int tot) {
		super("결제", 400, 500);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 1)));

		n.add(new JLabel(getIcon("./datafile/스토어/" + name + ".jpg", 250, 200)), "North");
		n.add(lbl(name, 0, 15));
		n.add(lbl("수량 " + cnt + "개", 0, 12), "South");

		var cap = "상품단가,결제금액".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(lbl(cap[i], 2), "West");
			tmp.add(lbl(i == 0 ? format(price) + "원" : "총 " + format(tot) + "원", 4), "East");

			tmp.setBackground(Color.black);

			for (var com : tmp.getComponents()) {
				com.setForeground(Color.white);
			}

			c.add(tmp);
		}

		((JComponent) c.getComponent(0)).setBorder(new MatteBorder(0, 0, 1, 0, Color.white));

		c.add(btn("결제하기", a -> {
			var ans = JOptionPane.showConfirmDialog(null, "결제 하시겠습니까?", "결제", JOptionPane.YES_NO_OPTION);

			if (ans == JOptionPane.YES_OPTION) {
				iMsg("결제가 완료되었습니다.");
				execute("insert orderlist values(0,?,?,?,now())", user.get(0), s_no, cnt);
			} else {
				var be1 = (Before) getWindowListeners()[0];
				var be2 = (Before) be1.b.getWindowListeners()[0];

				be2.b.setVisible(true);
				setVisible(false);
			}
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
