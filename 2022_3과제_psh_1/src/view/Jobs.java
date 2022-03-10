package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Jobs extends BaseFrame {
	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);
	ArrayList<ArrayList<Object>> result;
	JTextField txt[] = new JTextField[2];
	JComboBox combo[] = new JComboBox[3];
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
				if (t.getSelectedRow() == -1) {
					return;
				}

				btn[1].setEnabled(false);

				new Available(t.getValueAt(t.getSelectedRow(), 8) + "", btn[1]).addWindowListener(new Before(null));
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
			var tmp = new JPanel(i < 2 ? new FlowLayout(0) : new BorderLayout());
			if (i < 2) {
				tmp.add(sz(lbl(cap[i], 0, 15), 80, 20));
				tmp.add(txt[i] = new JTextField(15));
			} else {
				var p1 = new JPanel(new FlowLayout(1));
				var p2 = new JPanel(new FlowLayout(2));
				tmp.add(p1, "West");
				tmp.add(p2, "East");

				for (int j = 0; j < cap2.length; j++) {
					p1.add(sz(lbl(cap2[i], 0, 15), 80, 20));
					p1.add(combo[j] = new JComboBox<>(j == 0 ? local : j == 1 ? graduate : gender));
					if (j < 2) {
						sz(combo[j], 100, 30);
					}
					if (j > 0) {
						combo[j].insertItemAt("전체", 0);
						combo[j].setSelectedIndex(0);
					}
				}
				int k = 0;
				for (var c : "검색,지원".split(",")) {
					p2.add(btn[k++] = btn(c + "하기", k == 1 ? a -> search() : a -> {
						try {
							execute("insert applicant values(0,?,?,?)", t.getValueAt(t.getSelectedRow(), 8), uno, 0);
							iMsg("신천이 완료되었습니다.");
							search();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}));
				}
				btn[1].setEnabled(false);
			}
			nc.add(tmp);
		}

		txt[1].setEditable(false);

		for (var c : "이미지,모집정원,시급,직종,학력,성별".split(",")) {
			t.getColumn(c).setMinWidth(80);
			t.getColumn(c).setMaxWidth(80);
		}

		t.setRowHeight(80);

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		if (result != null) {
			addRow(result, m);
		} else {
			search();
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void search() {
		String cri1 = "", cri2 = "", cri3 = "";
		if (!txt[1].isEnabled()) {
			var cate = Stream.of(txt[1].getText().split(",")).mapToInt(a -> Arrays.asList(category).indexOf(a))
					.mapToObj(String::valueOf).toArray(String[]::new);
			cri1 = "and c_category in (" + String.join(",", cate) + ")";
		}

		if (combo[1].getSelectedIndex() != 0) {
			cri2 = "and e_graduate = " + (combo[1].getSelectedIndex() - 1);
		}

		if (combo[2].getSelectedIndex() != 0) {
			cri3 = "and e_gender=" + combo[2].getSelectedIndex();
		}

		var rs = getResults(
				"select c_name, e_title, concat((select count(*) from applicant a where a.e_no=e.e_no and (a.a_apply = 0 or a.a_apply=1)), '/', e_people) chk, format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no from company c inner join employment e on e.c_no=c.c_no where e_title like ? and left(c_address, 2) like ? "
						+ cri1 + " " + cri2 + " " + cri3 + " group by e.e_no order by e.e_no",
				"%" + txt[0].getText() + "%",
				"%" + (combo[0].getSelectedIndex() == 0 ? "%" : combo[0].getSelectedItem() + "%"));

		for (var r : rs) {
			if (toInt(r.get(2).toString().split("/")[0]) == toInt(r.get(2).toString().split("/")[1])) {
				rs.remove(r);
			}

			r.set(0, new JLabel(img("기업/" + r.get(0) + "2.jpg", 80, 80)));
			r.set(4, String.join(",",
					Stream.of(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
		}

		if (rs.size() == 0) {
			eMsg("검색 결과가 없습니다.");
			for (int i = 0; i < txt.length; i++) {
				txt[i].setText("");
			}
			for (int i = 0; i < combo.length; i++) {
				combo[i].setSelectedIndex(0);
			}
			search();
			return;
		}

		addRow(rs, m);
	}

	public static void main(String[] args) {
		new Jobs(null);
	}
}
