package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Jobs extends BaseFrame {
	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);
	JTextField txt[] = new JTextField[2];
	JComboBox com[] = new JComboBox[3];
	JButton btn[] = new JButton[2];

	public Jobs() {
		super("채용정보", 900, 500);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lblH("채용정보", 0, 0, 30), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));
		var c = "공고명,직종,지역".split(",");
		for (int i = 0; i < c.length; i++) {
			var p = new JPanel(i == 2 ? new BorderLayout() : new FlowLayout(0));
			if (i == 2) {
				var pw = new JPanel(new FlowLayout(0));
				var pe = new JPanel(new FlowLayout(2));
				p.add(pw, "West");
				p.add(pe, "East");
				var t = "지역,학력,성별".split(",");
				for (int j = 0; j < t.length; j++) {
					pw.add(sz(lbl(t[j], 0), 60, 20));
					pw.add(com[j] = new JComboBox<>(j == 0 ? local : j == 1 ? graduate : gender));

					if (j > 0) {
						com[j].insertItemAt("전체", 0);
						com[j].setSelectedIndex(0);
					}
				}

				for (var s : "검색,지원".split(",")) {
					pe.add(btn[Arrays.asList("검색,지원".split(",")).indexOf(s)] = btn(s + "하기", a -> {
						if (a.getActionCommand().equals("검색하기")) {
							search();
						} else {
							iMsg("신청이 완료되었습니다.");
							execute("insert applicant values(0, ?, ?,0)", eno, user.get(0));
							search();
						}
					}));
				}
				btn[1].setEnabled(false);
			} else {
				p.add(sz(lbl(c[i], 0), 60, 20));
				p.add(txt[i] = new JTextField(15));
			}
			nc.add(p);
		}

		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new JobSelection(txt[1]).addWindowListener(new Before(Jobs.this));
			}
		});

		var col = "이미지,모집정원,시급,학력,성별".split(",");
		var w = new int[] { 50, 60, 50, 80, 30 };
		for (int i = 0; i < w.length; i++) {
			t.getColumn(col[i]).setMinWidth(w[i]);
			t.getColumn(col[i]).setMaxWidth(w[i]);
		}

		t.getColumn("eno").setMinWidth(0);
		t.getColumn("eno").setMaxWidth(0);

		search();

		txt[1].setEnabled(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				btn[1].setEnabled(false);
				eno = toInt(t.getValueAt(t.getSelectedRow(), 8));
				cno = toInt(rs("select c_no from employment where e_no=?", eno));
				new Available(btn[1]).addWindowListener(new Before(Jobs.this));
			}
		});
	}

	void search() {
		String c1 = "", c2 = "", c3 = "", c4 = "";
		if (com[0].getSelectedIndex() != 0) {
			c1 = " and left(c_address, 2) = '" + com[0].getSelectedItem() + "' ";
		}

		if (com[1].getSelectedIndex() != 0) {
			c2 = " and e_graduate=" + (com[1].getSelectedIndex() - 1);
		}

		if (com[2].getSelectedIndex() != 0) {
			c3 = " and e_gender=" + com[2].getSelectedIndex();
		}

		if (!txt[1].getText().isEmpty()) {
			c4 = " and c_category in (" + String.join(",", Stream.of(txt[1].getText().split(","))
					.mapToInt(a -> Arrays.asList(category).indexOf(a)).mapToObj(String::valueOf).toArray(String[]::new))
					+ ")";
		}

		var rs = rs(
				"select c_name, e_title, concat((select count(*) from applicant a where a.e_no=e.e_no and a.a_apply < 2), '/', e_people), format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no from employment e, company c, applicant a where e.c_no=c.c_no and a.e_no = e.e_no and e_title like ? and (select count(*) from applicant a where a.e_no=e.e_no and a.a_apply < 2) < e.e_people "
						+ c1 + c2 + c3 + c4 + " group by c.c_no",
				"%" + txt[0].getText() + "%");
		if (rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			for (int i = 0; i < com.length; i++) {
				com[i].setSelectedIndex(0);
			}
			txt[0].setText("");
		}

		t.setRowHeight(50);
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
		new Jobs();
	}
}
