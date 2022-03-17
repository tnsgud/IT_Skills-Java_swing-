package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Available extends BaseFrame {
	JTextField txt[] = new JTextField[5];

	public Available() {
		super("지원가능여부", 300, 300);

		var user = rs("select * from user where u_no=?", uno).get(0);
		var rs = rs(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from employment e, company c where c.c_no = e.c_no and e.e_no=?",
				eno).get(0);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(15));
			txt[i].setEnabled(false);
			c.add(p);
		}
		s.add(btn("지원가능여부보기", a -> {
			if (user.get(6) != rs.get(3)) {
				eMsg("지원이 불가합니다. (성별)");
				return;
			}

			if (toInt(user.get(0)) > toInt(rs.get(4))) {
				eMsg("지원이 불가합니다. (학력)");
				return;
			}

			if (toInt(rs("select a_apply from applicant where u_no=? and e_no=?", uno, eno).get(0).get(0)) < 2) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}
			
			iMsg("지원 가능한 공고입니다.");
			Jobs.btn[1].setEnabled(true);
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		for (int i = 0; i < rs.size(); i++) {
			if (i == 3) {
				txt[i].setText(gender[toInt(rs.get(i)) - 1]);
			} else if (i == 4) {
				txt[i].setText(graduate[toInt(rs.get(i))]);
			} else {
				txt[i].setText(rs.get(i) + "");
			}
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		eno = 5;
		uno = 1;
		new Available();
	}
}
