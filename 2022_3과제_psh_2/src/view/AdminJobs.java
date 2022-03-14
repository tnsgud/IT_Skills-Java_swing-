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
	DefaultTableModel m = model("이미지,공고내용,모집정원,시급,직종,주소,모집학력,성별,eno,ano".split(","));
	JTable t = table(m);

	public AdminJobs() {
		super("관리자 채용정보", 1000, 600);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lbl("관리자 채용정보", 0, 35));
		n.add(btn("공고수정", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("수정할 공고를 선택하세요.");
				return;
			}

			new Posting(t.getValueAt(t.getSelectedRow(), 8)+"", this).addWindowListener(new Before(this));
		}), "East");

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);
		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);

		for (var c : "이미지,모집정원,모집학력,시급,성별".split(",")) {
			t.getColumn(c).setMinWidth(60);
			t.getColumn(c).setMaxWidth(60);
		}

		t.setRowHeight(80);

		load();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);
	}

	void load() {
		m.setRowCount(0);
		var rs = rs(
				"select c_img, e_title, e_people, format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no, count(a_no) from applicant a, employment e, company c where a.e_no=e.e_no and c.c_no=e.c_no group by c.c_no having count(a_no) < e_people");
		for (var r : rs) {
			r.set(0, new JLabel(img(r.get(0), 70, 70)));
			r.set(4, String.join(",",
					Stream.of(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new AdminJobs();
	}
}
