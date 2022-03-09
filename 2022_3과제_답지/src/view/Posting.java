package view;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Posting extends BaseFrame {

	JComboBox<String> box1, box2;
	JTextField txt[] = { new JTextField(20), new JTextField(20), new JTextField(20) };

	JRadioButton rbtn[] = { new JRadioButton("남"), new JRadioButton("여"), new JRadioButton("무관") };

	JButton btn1, btn2;
	String eno;

	AdminJobs aj;

	public Posting() {
		super("공고등록", 400, 400);
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		s.add(btn1 = crt_evt_btn("등록", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[1].getText().matches(".*[^0-9].*") || txt[2].getText().matches(".*[^0-9].*")) {
				eMsg("숫자로 입력하세요.");
				return;
			}
			if (a.getActionCommand().equals("등록")) {
				var cno = getResults("select * from company where c_name = ?", box1.getSelectedItem() + "").get(0)
						.get(0);

				int g = 0;
				for (int i = 0; i < rbtn.length; i++) {
					if (rbtn[i].isSelected())
						g = i;
				}

				g += 1;

				setValues("insert employment values(0,?,?,?,?,?,?)", cno, txt[0].getText(), txt[1].getText(),
						txt[2].getText(), g, box2.getSelectedIndex());
				iMsg("등록이 완료되었습니다");
				dispose();

			} else if (a.getActionCommand().equals("수정")) {
				var rs = getResults(
						"select count(a.a_no) from employment e inner join applicant a on e.e_no = a.e_no where e.e_no = ? group by e.e_no",
						eno);
				if (!rs.isEmpty()) {
					if (toInt(rs.get(0).get(0)) > toInt(txt[2].getText())) {
						eMsg("모집정원이 지원자보다 적습니다.");
						return;
					}
				}

				setValues("update employment set e_title = ?, e_pay = ?, e_people = ? where e_no = ?", txt[0].getText(),
						txt[1].getText(), txt[2].getText(), eno);

				aj.load();
				iMsg("수정이 완료되었습니다.");
				dispose();
			}
		}));

		var cap = "회사명,공고내용,시급,모집정원,성별,최종학력".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(crt_lbl(cap[i], JLabel.LEFT), 60, 30));
			if (i == 0)
				temp.add(sz(box1 = new JComboBox<String>(), 120, 30));
			else if (i == 5)
				temp.add(sz(box2 = new JComboBox<String>(), 120, 30));
			else if (i == 4) {
				var bg = new ButtonGroup();
				for (var r : rbtn) {
					temp.add(r);
					bg.add(r);
				}

			} else
				temp.add(txt[i - 1]);

			c.add(temp);
		}
		rbtn[0].setSelected(true);
		var rs1 = getResults("select c_name from company where c_no not in (select c_no from employment)");
		for (var r : rs1) {
			box1.addItem(r.get(0) + "");
		}

		for (var g : graduate) {
			box2.addItem(g);
		}
		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "모집내용"));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public Posting(String eno, AdminJobs aj) {
		this();
		this.aj = aj;
		this.eno = eno;
		setTitle("공고수정");

		box1.removeAllItems();
		for (var r : rbtn) {
			r.setEnabled(false);
		}
		box2.setEnabled(false);
		btn1.setText("수정");
		var cname = getResults("select c_name from employment e inner join company c on e.c_no = c.c_no where e_no = ?",
				eno).get(0).get(0);
		var rs = getResults("select c_name from employment e inner join company c on e.e_no = c.c_no");

		box1.addItemListener(i -> {

		});

		var rs2 = getResults("select * from employment where e_no = ?", eno);
		rbtn[toInt(rs2.get(0).get(5)) - 1].setSelected(true);
		box2.setSelectedIndex(toInt(rs2.get(0).get(6)));
		txt[0].setText(rs2.get(0).get(2) + "");
		txt[1].setText(rs2.get(0).get(3) + "");
		txt[2].setText(rs2.get(0).get(4) + "");

		for (var r : rs) {
			box1.addItem(r.get(0) + "");
		}

		for (int i = 0; i < rs.size(); i++) {
			if (box1.getItemAt(i).equals(cname)) {
				box1.setSelectedIndex(i);
			}
		}

		if (getResults("select * from applicant where e_no = ?", eno).size() == 0) {
			s.add(btn2 = crt_evt_btn("삭제", a -> {
				setValues("delete from employment where e_no = ?", eno);
				iMsg("삭제가 완료되었습니다.");
				dispose();
				aj.load();
			}));
		}

	}

}
