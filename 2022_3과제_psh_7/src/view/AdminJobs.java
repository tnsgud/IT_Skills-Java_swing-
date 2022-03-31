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

	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);

	public AdminJobs() {
		super("관리자 채용정보", 900, 500);

		setLayout(new BorderLayout(50, 50));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lblH("관리자 채용정보", 0, 0, 30));
		n.add(ne = new JPanel(), "East");
		ne.add(btn("공고 수정", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("수정할 공고를 선택하세요.");
				return;
			}

			eno = toInt(t.getValueAt(t.getSelectedRow(), 8));
			new Posting().addWindowListener(new Before(this));
		}));

		var n = "이미지,모집정원,시급,학력,성별".split(",");
		var w = new int[] { 50, 60, 50, 85, 30 };
		for (int i = 0; i < w.length; i++) {
			t.getColumn(n[i]).setMinWidth(w[i]);
			t.getColumn(n[i]).setMaxWidth(w[i]);
		}

		t.setRowHeight(50);

		data();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		createV();
		var rs = rs("select c_name, e_title, e_people, `format(e_pay, '#,##0')`, c_category, c_address, e_graduate, e_gender, e_no from v1");
		for (var r : rs) {
			r.set(0, new JLabel(img("기업/" + r.get(0) + "2.jpg", 50, 50)));
			r.set(4, String.join(",",
					Stream.of(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new Admin();
	}
}
