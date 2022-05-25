package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Posting extends BaseFrame {
	String eno;
	AdminJobs aj;
	JTextField txt[] = new JTextField[3];
	JComboBox<String> com1, com2;
	JButton btn1, btn2;
	JRadioButton rbtn[] = new JRadioButton[3];

	public Posting() {
		super("공고등록", 400, 400);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "회사명,공고내용,시급,모집정원,성별,최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(cap[i], 2, 12), 60, 30));
			if (i == 0) {
				tmp.add(sz(com1 = new JComboBox<String>(), 120, 30));
			} else if (i == 5) {
				tmp.add(sz(com2 = new JComboBox<>(), 120, 30));
			} else if (i == 4) {
				var bg = new ButtonGroup();
				for (int j = 0; j < gender.length; j++) {
					tmp.add(rbtn[j] = new JRadioButton(cap[i]));
					bg.add(rbtn[j]);
				}
			} else {
				tmp.add(txt[i - 1] = new JTextField(20));
			}
			c.add(tmp);
		}

		s.add(btn1 = btn("등록", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (toInt(txt[1].getText()) < 0 || toInt(txt[2].getText()) < 0) {
				eMsg("숫자로 입력하세요.");
				return;
			}

			var rs = getResults(
					"select count(a.a_no) from employment e inner join applicant a on e.e_no=a.e_no where e.e_no=? group by e.e_no",
					eno);
			if (!rs.isEmpty() && toInt(rs.get(0).get(0)) > toInt(txt[2].getText())) {
				eMsg("모집정원이 지원자보다 적습니다.");
				return;
			}

			if (a.getActionCommand().equals("등록")) {
				var cno = getResults("select * from compayn where c_name =?", com1.getSelectedItem() + "").get(0)
						.get(0);

				int g = 0;
				for (int i = 0; i < rbtn.length; i++) {
					if (rbtn[i].isSelected()) {
						g = i + 1;
					}
				}

				try {
					execute("insert employment values(0, ?, ?, ?, ?, ?, ?)", cno, txt[0].getText(), txt[1].getText(),
							txt[2].getText(), g, com2.getSelectedIndex());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					execute("update employment set e_title=? , e_pay=?, e)_people=? where e_no=?", txt[0].getText(),
							txt[1].getText(), txt[2].getText(), eno);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				aj.load();
			}

			iMsg(a.getActionCommand() + "이 완료되었습니다.");
			dispose();
		}));
		s.add(btn2 = btn("삭제", a -> {
			try {
				execute("delete from employment where e_no=?", eno);
				iMsg("삭제가 완료되었습니다.");
				dispose();
				aj.load();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		var rs1 = getResults("select c_name from company where c_no not in (select c_no from employment)");
		for (var r : rs1) {
			com1.addItem(r.get(0) + "");
		}

		for (var g : graduate) {
			com2.addItem(g);
		}

		btn2.setVisible(false);
		rbtn[0].setSelected(true);
		c.setBorder(new TitledBorder(new LineBorder(Color.black), "모집내용"));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public Posting(String eno, AdminJobs aj) {
		this();
		this.eno = eno;
		this.aj = aj;
		setTitle("공고수정");

		com1.removeAllItems();
		com2.setEnabled(false);
		for (var r : rbtn) {
			r.setEnabled(false);
		}
		btn1.setText("수정");

		com1.addActionListener(a -> {
			var rs2 = getResults("select * from employment where e_no=?", eno);
			rbtn[toInt(rs2.get(0).get(5)) - 1].setSelected(true);
			com2.setSelectedIndex(toInt(rs2.get(0).get(6)));
			txt[0].setText(rs2.get(0).get(2) + "");
			txt[1].setText(rs2.get(0).get(3) + "");
			txt[2].setText(rs2.get(0).get(4) + "");
		});

		var cname = getResults("select c_name from employment e inner join company c on e.c_no=c.c_no where e_no=?",
				eno).get(0).get(0);
		var rs1 = getResults("select c_name from employment e inner join company c on e.c_no=c.c_no ");
		for (var r : rs1) {
			com1.addItem(r.get(0) + "");
			if (r.get(0).equals(cname)) {
				com1.setSelectedItem(r.get(0));
			}
		}

		if (getResults("select * from applicant where e_no=?", eno).isEmpty()) {
			btn2.setVisible(false);
		}
	}

	public static void main(String[] args) {
		new Posting();
	}
}
