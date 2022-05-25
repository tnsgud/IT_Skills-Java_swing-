package view;

import java.awt.BorderLayout;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminJobs extends BaseFrame {
	DefaultTableModel m = model("이미지,공고내용,모집정원,시급,직종,주소,모집학력,성별,eno".split(","));
	JTable t = table(m);

	public AdminJobs() {
		super("관리자 채용정보", 1000, 600);

		setLayout(new BorderLayout(50, 50));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lblH("관리자 채용정보", 0, 0, 25));
		n.add(btn("공고수정", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 공고를 선택하세요.");
				return;
			}

			eno = toInt(t.getValueAt(t.getSelectedRow(), 8));
			new Posting("수정").addWindowListener(new Before(this));
		}), "East");

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		t.setRowHeight(30);

		var rs = rs(
				"select c_name, e_title, e_people, format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no from employment e, company c, applicant a where e.c_no = c.c_no and a.e_no=e.e_no and e_title like '%%' and (select count(*) from applicant a where a.e_no=e.e_no and (a_apply=0 or a_apply=1)) < e.e_people group by c.c_no");
		for (var r : rs) {
			r.set(4, String.join(",",
					Stream.of(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
			r.set(0, new JLabel(img("기업/" + r.get(0) + "2.jpg", 30, 30)));
			m.addRow(r.toArray());
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminJobs();
	}
}
