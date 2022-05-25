package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Available extends BaseFrame {
	JTextField txt[] = new JTextField[5];
	String eno, cname;
	JButton jobsbtn;

	public Available(String eno, JButton jobsbtn) {
		super("지원가능여부", 500, 500);
		this.eno = eno;
		this.jobsbtn = jobsbtn;

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(5, 20));

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout()), "South");

		var rs = getResults(
				"select c_name, c_ceo, c_address, e_gender, e_graduate from company c, inner join employment e on c.c_no=e.e_no where e_no=?",
				eno);
		cname = rs.get(0).get(0).toString();

		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기업/" + cname + "2.jpg").getScaledInstance(20, 20,
				Image.SCALE_SMOOTH));

		var cap = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
		for (int i = 0; i < txt.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl(cap[i], 2), 100, 0), "West");
			tmp.add(txt[i] = new JTextField());
			txt[i].setEnabled(false);

			if (i == 3) {
				txt[i].setText(gender[toInt(rs.get(0).get(i)) - 1]);
			} else if (i == 4) {
				txt[i].setText(graduate[toInt(rs.get(0).get(i)) - 1]);
			} else {
				txt[i].setText(rs.get(0).get(i).toString());
			}
			c.add(tmp);
		}

		s.add(btn("지원가능여부보기", a -> {
			if (!txt[3].getText().equals(ugender) && !txt[3].getText().equals("무관")) {
				eMsg("지원이 불가합니다.");
				return;
			}

			if (!txt[4].getText().equals("무관") && (Arrays.asList(graduate).indexOf(txt[4].getText()) < Arrays
					.asList(graduate).indexOf(ugraduate))) {
				eMsg("지원이 불가합니다.");
				return;
			}
			
			if(!getOne("select * from applicant where e_no=? and u_no=? and (a_apply = 0 or a_apply=1)", eno, uno).isEmpty()) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}
			
			iMsg("지원 가능한 공고입니다.");
			jobsbtn.setEnabled(true);
			dispose();
		}));
	}
}
