package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Avaliable extends BaseFrame {

	String cap[] = "기업이름,대표자,주소,모집성별,모집최종학력".split(",");
	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField() };

	String cname;

	public Avaliable(String eno, Jobs d) {
		super("지원가능여부", 300, 400);
		setLayout(new BorderLayout(5, 20));
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout()), "South");

		var rs = getResults(
				"select c_name, c_ceo, c_address, e_gender, e.e_graduate from company c inner join employment e on c.c_no = e.e_no where e_no = ?",
				eno);

		cname = rs.get(0).get(0).toString();

		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기업/" + cname + "2.jpg").getScaledInstance(20, 20,
				Image.SCALE_SMOOTH));

		for (int i = 0; i < txt.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(crt_lbl(cap[i], JLabel.LEFT), 100, 0), "West");
			temp.add(txt[i]);
			txt[i].setEnabled(false);
			if (i == 3) {
				txt[i].setText(gender[toInt(rs.get(0).get(i)) - 1]);
			} else if (i == 4) {
				txt[i].setText(graduate[toInt(rs.get(0).get(i))]);
			} else {
				txt[i].setText(rs.get(0).get(i).toString());

			}
			c.add(temp);
		}

		s.add(crt_evt_btn("지원가능여부보기", a -> {
			if (!txt[3].getText().equals(ugender)) {
				eMsg("지원이 불가합니다.");
				return;
			}

			if (!txt[4].getText().equals("무관")
					&& (Arrays.asList(graduate).indexOf(txt[4].getText()) < Arrays.asList(graduate).indexOf(ugender))) {
				eMsg("지원이 불가합니다.");
				return;
			}

			if (getResults("select * from applicant where e_no =? and u_no = ? and (a_apply = 0 or a_apply = 1)", eno,
					uno).size() > 0) {
				eMsg("합격자 또는 심사중입니다.");
				return;
			}

			iMsg("지원 가능한 공고입니다.");
			d.btn[1].setEnabled(true);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

}
