package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Applicant extends BaseFrame {
	public Applicant() {
		super("지원자 정보", 400, 400);

		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		load();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void load() {
		c.removeAll();
		var rs = rs(
				"select u.u_no, c_name, u_name, u_birth, u_graduate, u_email, a_no, c.c_no from applicant a, company c, employment e, user u where a.e_no=e.e_no and c.c_no=e.c_no and u.u_no=a.u_no and a_apply = 0");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			var img = new JLabel(img("회원사진/" + r.get(0) + ".jpg", 100, 100));
			var pc = new JPanel(new GridLayout(0, 1));
			var birth = LocalDate.parse(r.get(3) + "");
			var age = LocalDate.now().getYear() - birth.getYear() + 1;

			r.set(4, graduate[toInt(r.get(4))]);

			p.add(img, "West");
			p.add(pc);

			var cap = "지원 회사,이름,생년월일,최종학력,email".split(",");
			for (int i = 0; i < cap.length; i++) {
				if (i == 0) {
					pc.add(lblH(cap[i] + " : " + r.get(i), 2, 0, 15));
				} else if (i == 1) {
					pc.add(lblH(cap[i] + " : " + r.get(i) + "(나이 : " + age + "세)", 2, 0, 13));
				} else if (i == 4) {
					pc.add(lblH(cap[i] + " : " + r.get(i), 2, 0, 13));
				} else {
					pc.add(lbl(cap[i] + " : " + r.get(i), 2));
				}
			}

			var pop = new JPopupMenu();
			for (var i : "합격,불합격".split(",")) {
				var item = new JMenuItem(i);
				item.addActionListener(a -> {
					var result = a.getActionCommand().equals("합격") ? 1 : 2;
					
					execute("update applicant set a_apply=? where a_no=?", result, r.get(6));
					if(result == 1) {
						execute("update company set c_employee = c_employee+1 where c_no=?", r.get(7));
					}
					iMsg("심사가 완료되었습니다.");
					load();
				});
				pop.add(i);
			}

			p.setComponentPopupMenu(pop);
			p.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

			c.add(sz(p, 200, 150));
		}

		
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Applicant();
	}
}
