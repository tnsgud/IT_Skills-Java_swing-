package view;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
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
	JComboBox com[] = new JComboBox[3];
	JTextField txt[] = new JTextField[2];
	JButton btn[] = new JButton[2];

	public Jobs() {
		super("채용정보", 800, 700);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lbl("채용정보", 0, 30), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		var cap = "공고명,직종,지역".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(i < 2 ? new FlowLayout(0) : new BorderLayout());
			if (i < 2) {
				p.add(sz(lbl(cap[i], 0, 12), 80, 20));
				p.add(txt[i] = new JTextField(15));
			} else {
				var tmpW = new JPanel(new FlowLayout(0));
				var tmpE = new JPanel(new FlowLayout(2));
				p.add(tmpW, "West");
				p.add(tmpE, "East");

				var cap2 = "지역,학력,성별".split(",");
				for (int j = 0; j < cap2.length; j++) {
					tmpW.add(sz(lbl(cap2[j], 0), 60, 20));
					tmpW.add(com[j] = new JComboBox<>(j == 0 ? local : j == 1 ? graduate : gender));
					if (j > 0) {
						com[j].insertItemAt("전체", 0);
						com[j].setSelectedIndex(0);
					}
				}

				for (var c : "검색,지원".split(",")) {
					tmpE.add(btn[c.equals("검색") ? 0 : 1] = btn(c + "하기", a -> {
						if (a.getActionCommand().equals("검색하기")) {
							search();
						} else {
							iMsg("신청이 완료되었습니다.");
							execute("insert applicant values(0, ?, ?, ?)", eno, user.get(0), 0);
							search();
							btn[1].setEnabled(false);
						}
					}));
				}
			}
			nc.add(p);
		}

		txt[1].setEnabled(false);
		btn[1].setEnabled(false);

		t.setRowHeight(50);

		var n = "이미지,모집정원,시급,학력,성별,eno".split(",");
		var w = new int[] { 50, 60, 50, 85, 30, 0 };
		for (int i = 0; i < w.length; i++) {
			t.getColumn(n[i]).setMinWidth(w[i]);
			t.getColumn(n[i]).setMaxWidth(w[i]);
		}

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
				eno = toInt(t.getValueAt(t.getSelectedRow(), 8));
				btn[1].setEnabled(false);
				new Available(btn[1]).addWindowListener(new Before(Jobs.this));
			}
		});

		setVisible(true);
	}

	private void search() {
		m.setRowCount(0);
		String c1 = "", c2 = "", c3 = "";

		if (com[0].getSelectedIndex() != 0) {
			c1 = " and left(c_address, 2) = '" + com[0].getSelectedItem() + "'";
		}

		if (com[1].getSelectedIndex() != 0) {
			c2 = " and e_graduate=" + (com[1].getSelectedIndex() - 1);
		}

		if (com[2].getSelectedIndex() != 0) {
			c3 = " and e_gender=" + com[2].getSelectedIndex();
		}

		createV();
		var rs = rs("select * from v1 where e_title like ? and c_category regexp ? " + c1 + c2 + c3,
				"%" + txt[0].getText() + "%", "(" + (txt[1].getText().isEmpty() ? "":String.join("|", Stream.of(txt[1].getText().split(","))
						.map(a -> Arrays.asList(category).indexOf(a)+"").toArray(String[]::new))) + ")");
		if (rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			Stream.of(com).forEach(c -> c.setSelectedIndex(0));
			Stream.of(txt).forEach(t -> t.setText(""));
			search();
			return;
		}

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
		new Main();
	}
}
