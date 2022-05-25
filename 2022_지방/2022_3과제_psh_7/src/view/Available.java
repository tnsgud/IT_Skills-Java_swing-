package view;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Available extends BaseFrame {
	JTextField txt[] = new JTextField[5];

	public Available(JButton jButton) {
		super("지원가능여부", 300, 350);

		var info = rs(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from company c, employment e where c.c_no=e.e_no and e.e_no=?",
				eno).get(0);

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(), "South");

		var cap = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(10));
			txt[i].setText(
					i == 4 ? graduate[toInt(info.get(i))] : i == 3 ? gender[toInt(info.get(i)) - 1] : info.get(i) + "");
			txt[i].setEnabled(false);
			c.add(p);
		}

		s.add(btn("지원가능여부보기", a -> {
			if (toInt(info.get(3)) - 1 != toInt(user.get(6)) && toInt(info.get(3)) != 3) {
				eMsg("지원이 불가합니다. (성별)");
				return;
			}

			if (toInt(info.get(4)) < toInt(user.get(7)) && toInt(info.get(4)) != 3) {
				eMsg("지원이 불가합니다. (학력)");
				return;
			}

			if (toInt(rs("select * from applicant where e_no=? and u_no=?", eno, user.get(0)).get(0).get(0)) < 2) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}

			iMsg("지원 가능한 공고입니다.");
			jButton.setEnabled(true);
			dispose();
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		eno = 1;
		new Available(null);
	}
}