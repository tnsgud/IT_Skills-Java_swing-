package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Jobs extends BaseFrame {
	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);
	ArrayList<ArrayList<Object>> result;
	JTextField txt[] = new JTextField[2];
	JComboBox com[] = new JComboBox[3];
	JButton btn[] = new JButton[2];

	public Jobs(ArrayList<ArrayList<Object>> result) {
		super("채용정보", 1000, 600);
		this.result = result;

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new JobSelection(txt[1]).addWindowListener(new Before(Jobs.this));
			}
		});
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Available(t.getValueAt(t.getSelectedRow(), 8) + "", btn[1])
						.addWindowListener(new Before(Jobs.this));
			}
		});
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout(5, 30)), "North");
		add(new JScrollPane(t));

		n.add(lblH("채용정보", 0, 0, 20), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		var cap = "공고명,직종,지역".split(",");
		var cap2 = "지역,학력,성별".split(",");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(i < 2 ? new FlowLayout(0) : new BorderLayout());
			if (i < 2) {
				p.add(sz(lbl(cap[i], 0, 15), 80, 20));
				p.add(txt[i] = new JTextField(20));
			} else {
				var p1 = new JPanel(new FlowLayout(1));
				var p2 = new JPanel(new FlowLayout(2));

				p.add(p1, "West");
				p.add(p2, "East");
				for (int j = 0; j < cap2.length; j++) {
					p1.add(sz(lbl(cap2[j], 0, 15), 80, 20));
					p1.add(com[j] = new JComboBox<>(j == 0 ? local : j == 1 ? graduate : gender));

					if (j < 2) {
						sz(com[j], 100, 30);
					}
					if (j > 0) {
						com[j].insertItemAt("전체", 0);
						com[j].setSelectedIndex(0);
					}
				}
				int k = 0;
				for (var c : "검색,지원".split(",")) {
					p2.add(btn[k++] = btn(c + "하기", k == 1 ? a -> search() : a -> {
						iMsg("신청이 완료되었습니다.");
						execute("insert into applicant values(0, ?, ?,?)", t.getValueAt(t.getSelectedRow(), 8), uno, 0);
						search();
					}));
				}
				btn[1].setEnabled(false);
			}
			nc.add(p);
		}

		txt[1].setEditable(false);

		for (var c : "이미지,모집정원,직종,학력,성별,시급".split(",")) {
			t.getColumn(c).setMinWidth(80);
			t.getColumn(c).setMaxWidth(80);
		}

		t.setRowHeight(80);

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		search();
	}

	private void search() {
		m.setRowCount(0);
		String cri1 = "", cri2 = "", cri3 = "";
		if (!txt[1].getText().isEmpty()) {
			var cate = Stream.of(txt[1].getText().split(",")).mapToInt(a -> Arrays.asList(category).indexOf(a))
					.mapToObj(String::valueOf).toArray(String[]::new);
			cri1 = "and c_category in (" + String.join(",", cate) + ")";
		}

		if (com[1].getSelectedIndex() != 0) {
			cri2 = "and e_graduate = " + com[1].getSelectedIndex();
		}

		if (com[2].getSelectedIndex() != 0) {
			cri3 = "and e_gender=" + com[2].getSelectedIndex();
		}

		var rs = rs(
				"select c_img, e_title, e_people, format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no, count(a_no) from employment e, company c, applicant a where e.c_no=c.c_no and a.e_no = e.e_no and a_apply < 2 and e_title like ? and left(c_address, 2) like ?"
						+ cri1 + " " + cri2 + " " + cri3 + " group by e.e_no having count(a_no) < e_people",
				"%" + txt[0].getText() + "%",
				com[0].getSelectedIndex() == 0 ? "%%" : "%" + com[0].getSelectedItem() + "%");
		for (var r : rs) {
			r.set(0, new JLabel(img(r.get(0), 50, 50)));
			r.set(2, r.get(9) + "/" + r.get(2));
			r.set(4, String.join(",",
					Stream.of(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
			r.remove(9);
			m.addRow(r.toArray());
		}

		if (m.getRowCount() == 0) {
			eMsg("검색 결과가 없습니다.");
			Stream.of(com).forEach(c -> c.setSelectedIndex(0));
			Stream.of(txt).forEach(t -> t.setText(""));
			search();
		}
	}

	public static void main(String[] args) {
		uno = "1";
		new Jobs(null);
	}
}
