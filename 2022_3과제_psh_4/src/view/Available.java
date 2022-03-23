package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Available extends BaseFrame {
	JTextField txt[] = new JTextField[5];

	public Available(JButton jButton) {
		super("지원가능여부", 300, 300);

		var info = rs(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from company c, employment e where e.c_no=c.c_no and c.c_no=?",
				cno).get(0);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기업/" + info.get(0) + "1.jpg"));

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(15));
			if (i == 4) {
				txt[i].setText(graduate[toInt(info.get(i))]);
			} else if (i == 3) {
				txt[i].setText(gender[toInt(info.get(i))-1]);
			} else {
				txt[i].setText(info.get(i) + "");
			}
			txt[i].setEnabled(false);
			c.add(p);
		}

		s.add(btn("지원가능여부보기", a -> {
			if(!info.get(3).equals(user.get(6)) && toInt(info.get(3)) != 3) {
				eMsg("지원이 불가합니다. (성별)");
				return;
			}
			
			if(toInt(info.get(4)) < toInt(user.get(7)) && toInt(info.get(4)) != 3) {
				eMsg("지원이 불가합니다. (학력)");
				return;
			}
			
			if(!rs("select * from applicant where u_no=? and e_no=? and a_apply < 2", eno).isEmpty()) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}
			
			iMsg("지원가능한 공고입니다.");
			jButton.setEnabled(true);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}
}
