package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Posting extends BaseFrame {
	JComboBox com[] = new JComboBox[2];
	JTextField txt[] = new JTextField[3];
	JRadioButton rbtn[] = new JRadioButton[3];
	JButton btn[] = new JButton[2];
	String eno;

	public Posting() {
		super("공고 등록", 400, 400);

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(1)), "South");

		var cap = "회사명,공고내용,시급,모집정원,성별,최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));

			if (i == 0) {
				p.add(com[0] = new JComboBox<>(
						rs("select c_name from company where c_no not in (select c_no from employment)").stream()
								.flatMap(a -> a.stream()).toArray(String[]::new)));
			} else if (i == 4) {
				var tmp = "남,여,무관".split(",");
				var bg = new ButtonGroup();
				for (int j = 0; j < tmp.length; j++) {
					p.add(rbtn[j] = new JRadioButton(tmp[j]));
					bg.add(rbtn[j]);
				}
				rbtn[0].setSelected(true);
			} else if (i == 5) {
				p.add(com[1] = new JComboBox<>(graduate));
			} else {
				p.add(txt[i - 1] = new JTextField(20));
			}

			c.add(p);
		}

		var bcap = "등록,삭제".split(",");
		for (int i = 0; i < bcap.length; i++) {
			s.add(btn[i] = btn(bcap[i], a -> {
				if (a.getActionCommand().equals("삭제")) {
					iMsg("삭제가 완료되었습니다.");
					execute("delete from employment where e_no=?", eno);
					dispose();
					return;
				}

				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				if ((toInt(txt[1].getText()) + "").length() != txt[1].getText().length()
						|| (toInt(txt[2].getText()) + "").length() != txt[2].getText().length()) {
					eMsg("숫자로 입력하세요.");
					return;
				}

				if (toInt(rs("select count(*) from applicant where e_no=?", eno).get(0).get(0)) > toInt(
						txt[2].getText())) {
					eMsg("모집정원이 지원자보다 적습니다.");
					return;
				}

				iMsg(a.getActionCommand() + "이 완료 되었습니다.");

				if (a.getActionCommand().equals("수정")) {
					execute("update employment setd e_title=?, e_pay=?, e_people=?", txt[0].getText(), txt[1].getText(),
							txt[2].getText());
				} else {
					var cno = rs("select c_no from company where c_name=?", com[0].getSelectedItem());
					execute("insert into employment values(0, ? ,? ,? ,? ,? ,?)", cno, txt[0].getText(),
							txt[1].getText(), txt[2].getText(), rbtn[0].isSelected() ? 0 : rbtn[1].isSelected() ? 1 : 2,
							com[1].getSelectedIndex());
				}

				dispose();
			}));
		}

		btn[1].setVisible(false);

		setVisible(true);
	}

	public Posting(String eno, AdminJobs aj) {
		this();
		this.eno = eno;
		var rs = rs(
				"select c_name, e_title, e_pay, e_people, e_gender, e_graduate from company c, employment e where e.c_no=c.c_no and e_no=?",
				eno).get(0);
		com[0].removeAllItems();
		for (var r : rs("select c_name from employment e, company c where c.c_no=e.c_no order by e_no asc")) {
			com[0].addItem(r.get(0));
		}

		com[0].setSelectedItem(rs.get(0));

		com[1].setEnabled(false);
		com[1].setSelectedIndex(toInt(rs.get(5)));

		for (int i = 0; i < txt.length; i++) {
			txt[i].setText(rs.get(i + 1) + "");
		}

		for (var r : rbtn) {
			r.setEnabled(false);
		}

		rbtn[toInt(rs.get(4)) - 1].setSelected(true);

		btn[1].setVisible(rs("select * from applicant where e_no=?", eno).isEmpty());

		repaint();
		revalidate();
	}
}
