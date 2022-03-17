package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Posting extends BaseFrame {
	JTextField txt[] = new JTextField[3];
	JComboBox com[] = new JComboBox[2];
	JRadioButton rbtn[] = new JRadioButton[3];
	JButton btn[] = new JButton[2];

	public Posting() {
		super("공고 등록", 500, 400);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "회사명,공고내용,시급,모집정원,성별,최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			if (i == 0) {
				p.add(com[0] = new JComboBox<>(
						rs("select c_name from company where c_no not in (select c_no from employment)").stream()
								.flatMap(a -> a.stream()).toArray()));
			} else if (i == 4) {
				var t = new JPanel(new FlowLayout(0));
				var bg = new ButtonGroup();
				var rcap = "남,여,무관".split(",");
				for (int j = 0; j < rcap.length; j++) {
					t.add(rbtn[j] = new JRadioButton(rcap[j]));
					bg.add(rbtn[j]);
				}
				rbtn[0].setSelected(true);
				p.add(t);
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

		c.setBorder(
				new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "모집내용"), new EmptyBorder(5, 5, 5, 5)));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public Posting(String string) {
		this();
		setTitle("공고 수정");
		btn[1].setText("수정");
		com[0].removeAllItems();
		for (var r : rs("select c_name from company where c_no in (select c_no from employment)")) {
			com[0].addItem(r.get(0));
		}
		com[0].addActionListener(a -> {
			eno = toInt(rs("select e_no from company c, employment e where c.c_no=e.c_no and c_name=?",
					com[0].getSelectedItem()).get(0).get(0));
			load();
		});
		load();
	}

	private void load() {
		var rs = rs(
				"select c_name, e_title, e_pay, e_people, e_gender, e_graduate from employment e, company c where c.c_no=e.c_no and e_no=?",
				eno).get(0);
		com[0].setSelectedItem(rs.get(0));

		Stream.of(rbtn).forEach(b -> b.setEnabled(false));
		rbtn[toInt(rs.get(4)) - 1].setSelected(true);
		for (int i = 1; i < 4; i++) {
			txt[i - 1].setText(rs.get(i) + "");
		}
		com[1].setEnabled(false);
		com[1].setSelectedIndex(toInt(rs.get(5)));
	}
}
