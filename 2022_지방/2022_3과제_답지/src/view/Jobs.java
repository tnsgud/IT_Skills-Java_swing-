package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.util.StringUtils;

public class Jobs extends BaseFrame {

	JTextField txt[] = { new JTextField(20), new JTextField(20) };

	DefaultTableModel m = crt_model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,e_no".split(","));
	JTable t = crt_table(m);
	JComboBox combo[] = { crt_combo("전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",")),
			crt_combo("전체,대학교 졸업,고등학교 졸업,중학교 졸업,무관".split(",")), crt_combo("전체,남자,여자,무관".split(",")) };

	JButton btn[] = new JButton[2];

	public Jobs(ArrayList<ArrayList<Object>> result) {
		super("채용정보", 1000, 600);
		add(n = new JPanel(new BorderLayout(5, 30)), "North");
		n.add(crt_lbl("채용정보", JLabel.CENTER, "HY헤드라인M", 0, 20), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		var cap = "공고명,직종,지역".split(",");
		var cap2 = "지역,학력,성별".split(",");
		var cap3 = "검색하기,지원하기".split(",");
		for (int i = 0; i < 3; i++) {
			var tmp = new JPanel();
			if (i < 2) {
				tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
				tmp.add(sz(crt_lbl(cap[i], JLabel.CENTER, 1, 15), 80, 20));
				tmp.add(txt[i]);
				nc.add(tmp);
			} else {
				tmp.setLayout(new BorderLayout());
				var p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				tmp.add(p1);
				tmp.add(p2, "East");
				int width[] = { 80, 50, 50 };
				for (int j = 0; j < cap2.length; j++) {
					p1.add(sz(crt_lbl(cap2[j], JLabel.CENTER, 1, 15), width[j], 20));
					p1.add(combo[j]);
				}
				sz(combo[0], 100, 30);
				sz(combo[1], 100, 30);

				for (int k = 0; k < cap3.length; k++) {
					p2.add(btn[k] = new JButton(cap3[k]));
				}
				btn[1].setEnabled(false);
			}
			nc.add(tmp);
		}

		txt[1].setEnabled(false);
		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new JobSelection(Jobs.this).addWindowListener(new before(Jobs.this));
				super.mousePressed(e);
			}
		});
		btn[0].addActionListener(a -> search());
		btn[1].addActionListener(a -> {
			setValues("insert applicant values(0,?,?,?)", t.getValueAt(t.getSelectedRow(), 8), uno, 0);
			iMsg("신청이 완료되었습니다.");
			search();
		});

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				btn[1].setEnabled(false);

				new Avaliable(t.getValueAt(t.getSelectedRow(), 8).toString(), Jobs.this)
						.addWindowListener(new before(Jobs.this));
			};
		});

		if (result != null)
			addRow(result, m);
		else
			search();

		add(new JScrollPane(t));
		t.getColumn("이미지").setMinWidth(80);
		t.getColumn("이미지").setMaxWidth(80);
		t.setRowHeight(80);

		t.getColumn("모집정원").setMinWidth(80);
		t.getColumn("모집정원").setMaxWidth(80);

		t.getColumn("시급").setMinWidth(50);
		t.getColumn("시급").setMaxWidth(50);

		t.getColumn("직종").setMinWidth(150);
		t.getColumn("직종").setMaxWidth(150);

		t.getColumn("학력").setMinWidth(100);
		t.getColumn("학력").setMaxWidth(100);

		t.getColumn("성별").setMinWidth(40);
		t.getColumn("성별").setMaxWidth(40);

		t.getColumn("e_no").setMinWidth(0);
		t.getColumn("e_no").setMaxWidth(0);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void search() {
		String criteria1 = "", criteria2 = "", criteria3 = "";
		if (!txt[1].getText().isEmpty()) {
			var cate = Arrays.stream(txt[1].getText().split(",")).mapToInt(a -> Arrays.asList(category).indexOf(a))
					.mapToObj(String::valueOf).toArray(String[]::new);
			criteria1 = "and c_category in (" + String.join(",", cate) + ") ";
		}

		if (combo[1].getSelectedIndex() != 0) {
			criteria2 = "and e_graduate =" + (combo[1].getSelectedIndex() - 1);
		}

		if (combo[2].getSelectedIndex() != 0) {
			criteria3 = "and e.e_gender = " + combo[2].getSelectedIndex() + 1;
		}

		var rs = getResults(
				"SELECT c.c_name, e.e_title, CONCAT((select count(*) from applicant a where a.e_no = e.e_no and (a.a_apply = 0 or a.a_apply = 1)), '/', e.e_people) chk, format(e.e_pay,'#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no FROM company c INNER JOIN employment e ON e.c_no = c.c_no where e.e_title like ? and left(c.c_address,2) like ? "
						+ criteria1 + " " + criteria2 + " " + criteria3 + " group by e.e_no order by e.e_no ",
				"%" + txt[0].getText() + "%",
				"%" + (combo[0].getSelectedItem().equals("전체") ? "" : combo[0].getSelectedItem()) + "%");

		for (var r : rs) {
			if (toInt(r.get(2).toString().split("/")[0]) == toInt(r.get(2).toString().split("/")[1]))
				rs.remove(r);

			r.set(0, new JLabel(getIcon("./datafiles/기업/" + r.get(0) + "2.jpg", 80, 80)));
			r.set(4, String.join(",",
					Arrays.stream(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
			r.set(6, graduate[toInt(r.get(6))]);
			r.set(7, gender[toInt(r.get(7)) - 1]);
		}

		if (rs.size() == 0) {
			eMsg("검색 결과가 없습니다.");
			txt[0].setText("");
			txt[1].setText("");
			combo[0].setSelectedIndex(0);
			combo[1].setSelectedIndex(0);
			combo[2].setSelectedIndex(0);
			search();
			return;
		}

		addRow(rs, m);
	}

	public static void main(String[] args) {
		new Jobs(null);
	}
}
