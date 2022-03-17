package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Jobs extends BaseFrame {
	DefaultTableModel m = model("이미지,공고명,모집정원,시급,직종,지역,학력,성별,eno".split(","));
	JTable t = table(m);
	JTextField txt[] = new JTextField[2];
	JComboBox com[] = new JComboBox[3];
	static JButton btn[] = new JButton[2];

	public Jobs() {
		super("채용정보", 1000, 600);

		add(n = new JPanel(new BorderLayout(50, 50)), "North");
		add(new JScrollPane(t));

		n.add(lbl("채용정보", 0, 35), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		var cap = "공고명,직종,지역".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(i < 2 ? new FlowLayout(0) : new BorderLayout());
			if (i == 2) {
				JPanel t1 = new JPanel(new FlowLayout()), t2 = new JPanel(new FlowLayout(2));
				var tcap = "지역,학력,성별".split(",");
				for (int j = 0; j < tcap.length; j++) {
					t1.add(sz(lbl(tcap[j], 0), 80, 20));
					t1.add(com[j] = new JComboBox<>(j == 0 ? local : j == 1 ? graduate : gender));
				}
				var idx = 0;
				for (var c : "검색,지원".split(",")) {
					t2.add(btn[idx++] = btn(c + "하기", a -> {
						if (c.equals("지원하기")) {
							iMsg("신청이 완료되었습니다.");
							execute("insert into applicant values(0, ?, ?, 0)", t.getValueAt(t.getSelectedRow(), 8),
									uno);
						}

						search();
					}));
				}
				p.add(t1, "West");
				p.add(t2, "East");
			} else {
				p.add(sz(lbl(cap[i], 0), 80, 20));
				p.add(txt[i] = new JTextField(15));
			}

			nc.add(p);
		}

		txt[1].setEnabled(false);

		search();

		txt[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new JobSelection(txt[1]).addWindowListener(new Before(Jobs.this));
			}
		});
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;
				eno = toInt(t.getValueAt(t.getSelectedRow(), 8));
				btn[1].setEnabled(false);
				new Available().addWindowListener(new Before(Jobs.this));
			}
		});

		setVisible(true);
	}

	private void search() {
		String c1 = "", c2 = "", c3 = "";
		if (com[0].getSelectedIndex() != 0) {
			c1 = "and c_address like '" + com[0].getSelectedItem() + "%' ";
		}

		if (com[1].getSelectedIndex() != 0) {
			c2 = "and e_graduate = " + com[1].getSelectedIndex() + " ";
		}

		if (com[2].getSelectedIndex() != 0) {
			c3 = "and e_gender=" + com[2].getSelectedIndex() + " ";
		}

		var rs = rs(
				"select c_name, e_title, concat((select count(*) from applicant a where a.e_no=e.e_no and (a_apply=0 or a_apply=1)), '/', e.e_people), format(e_pay, '#,##0'), c_category, c_address, e_graduate, e_gender, e.e_no from employment e, company c, applicant a where e.c_no = c.c_no and a.e_no=e.e_no and e_title like ? and (select count(*) from applicant a where a.e_no=e.e_no and (a_apply=0 or a_apply=1)) < e.e_people "
						+ c1 + c2 + c3 + "group by c.c_no",
				"%" + txt[0].getText() + "%");
		m.setRowCount(0);
		t.setRowHeight(80);
		for (var r : rs) {
			r.set(0, new JLabel(img("기업/"+r.get(0)+"2.jpg", 80, 80)));
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
