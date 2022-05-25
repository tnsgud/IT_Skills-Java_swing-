package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Iterator;

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
		var rs = getResults(
				"select c_name, u_name, u_birth, u_graduate, u_email, u_img,a_no, c.c_no from applicant a inner join user u on a.u_no=u.u_no inner join employment e on e.e_no=u.u_no inner join company c on c.c_no = e.c_no where a.a_apply=0");
		var now = LocalDate.now();
		for (var r : rs) {
			var p = new JPanel(new BorderLayout(20, 0));
			var img = new JLabel(img(r.get(5), 100, 100));
			var pc = new JPanel(new GridLayout(0, 1));
			var birth = LocalDate.parse(r.get(2).toString());
			var age = now.getYear() - birth.getYear() + 1;

			r.set(3, graduate[toInt(r.get(3))]);

			p.add(img, "West");
			p.add(pc);

			var cap = "지원 회사,이름,생년월일,최동학력,email".split(",");
			for (int i = 0; i < cap.length; i++) {
				if (i == 0) {
					pc.add(lblH(cap[i] + " : " + r.get(i), 2, 0, 15));
				} else if (i == 4) {
					pc.add(lblH(cap[i] + " : " + r.get(i), 2, 0, 13));
				} else if (i == 1) {
					pc.add(lbl(cap[i] + " : " + r.get(i) + "(나이 :" + age + ")", 2));
				} else {
					pc.add(lbl(cap[i] + " : " + r.get(i), 2));
				}
			}

			var pop = new JPopupMenu();
			for (var i : "합격,불합격".split(",")) {
				var item = new JMenuItem(i);
				pop.add(item);
				item.addActionListener(a -> {
					var result = a.getActionCommand().equals("합격") ? 1 : 2;
					
					try {
						execute("update applicant set a_apply = ? where a_no=?", result, r.get(6));
						if(result == 1) {
							execute("update company set c_employee = c_employee + 1 where c_no=?", r.get(7));
						}
						iMsg("심사가 완료되었습니다.");
						load();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
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
