package view;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Available extends BaseFrame {
	JTextField txt[] = new JTextField[5];

	public Available(String eno, JButton JobsBtn) {
		super("지원가능여부", 300, 300);

		var rs = rs(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from employment e, company c where c.c_no = e.c_no and e_no=?",
				eno).get(0);

		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기업/" + rs.get(0) + "1.jpg"));

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(10));
			txt[i].setEnabled(false);
			txt[i].setText((i == 3 ? gender[toInt(rs.get(i))]
					: i == 4 ? graduate[toInt(rs.get(i))] : rs.get(i)) + "");
			c.add(p);
		}

		s.add(btn("지원가능여부보기", a -> {
			if(toInt(rs.get(3)) != 3 && !txt[3].getText().equals(ugender)) {
				eMsg("지원이 불가합니다. (성별)");
				return;
			}
			
			if(toInt(rs.get(4)) != 3 && (Arrays.asList(graduate).indexOf(txt[4].getText()) < Arrays.asList(graduate).indexOf(ugraduate))) {
				eMsg("지우너이 불가합니다. (학력)");
				return;
			}
			
			if(rs("select * from applicant where e_no=? and u_no=? and (a_apply=0 or a_apply=1)", eno, uno).isEmpty() ) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}
			
			iMsg("지원 가능한 공고입니다.");
			JobsBtn.setEnabled(true);
			dispose();
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Jobs(null);
	}
}
