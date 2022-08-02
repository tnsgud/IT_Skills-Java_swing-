package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChargeDialog extends BaseDialog {
	JRadioButton radio[] = new JRadioButton[6];

	public ChargeDialog() {
		super("충전하기", 300, 500);

		add(lbl("충전 금액 선택", 0, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var bg = new ButtonGroup();
		int pr[] = new int[] {5, 10, 20, 25, 50, 100};
		for (int i = 0; i < 6; i++) {
			radio[i] = new JRadioButton(format(pr[i] * 1000));

			radio[i].setForeground(Color.white);

			bg.add(radio[i]);
			c.add(radio[i]);
		}

		radio[0].setSelected(true);

		s.add(btn("충전", a -> {
			execute("update user set u_money= u_money+? where u_no = ?",
					toInt(Stream.of(radio).filter(JRadioButton::isSelected).findFirst().get().getText()),
					BasePage.user.get(0));
			BasePage.user = getRows("select * from user where u_no=?", BasePage.user.get(0)).get(0);
			iMsg("충전이 완료되었습니다.\n보유 잔액:" + format(toInt(BasePage.user.get(5))) + "원");
			if (BasePage.mf.c.getComponent(BasePage.mf.c.getComponentCount() - 1) instanceof InfoEditPage) {
				InfoEditPage.pr.setText(format(toInt(BasePage.user.get(5))) + "원");
			}
			dispose();
		}));

		opaque((JComponent) getContentPane(), false);
	}
}
