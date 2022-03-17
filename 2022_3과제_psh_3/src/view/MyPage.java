package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.print.PrinterException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부".split(","));
	JTable t = table(m);

	public MyPage() {
		super("마이페이지", 700, 400);
		user = rs("select * from user where u_no=?", 1).get(0);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout(50, 50)), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(lblH("Mypage", 0, 0, 25), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		nc.add(lbl("성명 : " + user.get(1), 2, 15));
		nc.add(lbl("성별 : " + gender[toInt(user.get(6)) - 1], 2, 15));
		nc.add(lbl("최종학력 : " + graduate[toInt(user.get(7))], 2, 15));

		s.add(btn("PDF인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		var rs = rs(
				"select c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply from company c, employment e, applicant a, user u where e.e_no = a.e_no and e.c_no = c.c_no and u.u_no= a.u_no and u.u_no = ?",
				user.get(0));
		for (var r : rs) {
			r.set(4, graduate[toInt(r.get(4))]);
			r.set(5, gender[toInt(r.get(5)) - 1]);
			r.set(6, toInt(r.get(6)) == 0 ? "심사중" : toInt(r.get(6)) == 1 ? "합격" : "불합격");
			r.add(0, rs.indexOf(r));
			m.addRow(r.toArray());
		}

		setVisible(true);
	}

	public static void main(String[] args) {

		new MyPage();
	}
}
