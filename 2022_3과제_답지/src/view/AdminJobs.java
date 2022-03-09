package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminJobs extends BaseFrame {

	DefaultTableModel m = crt_model("이미지,공고내용,모집정원,시급,직종,주소,모집학력,성별,eno".split(","));
	JTable t = crt_table(m);

	public AdminJobs() {
		super("관리자 채용정보", 1000, 600);
		setLayout(new BorderLayout(10, 10));
		add(n = new JPanel(new BorderLayout()), "North");

		n.add(crt_lbl("관리자 채용정보", JLabel.CENTER, "HY헤드라인M", 0, 30));
		n.add(ne = new JPanel(new FlowLayout(FlowLayout.CENTER)), "East");
		ne.add(crt_evt_btn("공고수정", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("수정할 공고를 선택하세요.");
			}

			new Posting(t.getValueAt(t.getSelectedRow(), 8) + "", this).addWindowListener(new before(this));
		}));

		add(new JScrollPane(t));
		load();

		t.setRowHeight(60);

		int width[] = { 60, 60, 50, 70, 40 };
		String col[] = "이미지,모집정원,시급,모집학력,성별".split(",");

		for (int i = 0; i < width.length; i++) {
			t.getColumn(col[i]).setMinWidth(width[i]);
			t.getColumn(col[i]).setMaxWidth(width[i]);
		}

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	void load() {
		var rs = getResults(
				"select c_name, e_title, e_people, format(e_pay, '#,##0'),  c_category, c_address, e_graduate, e_gender,e.e_no, count(a.a_no) from company c inner join employment e on c.c_no = e.c_no left join applicant a on e.e_no = a.e_no group by e.e_no");

		var itr = rs.iterator();
		while (itr.hasNext()) {
			var r = itr.next();
			r.set(0, new JLabel(getIcon("./datafiles/기업/" + r.get(0) + "2.jpg", 30, 30)));

			r.set(4, String.join(",",
					Arrays.stream(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
			if (toInt(r.get(2)) == toInt(r.get(9))) {
				itr.remove();
			}
		}

		addRow(rs, m);
	}

	public static void main(String[] args) {
		new AdminJobs();
	}
}
