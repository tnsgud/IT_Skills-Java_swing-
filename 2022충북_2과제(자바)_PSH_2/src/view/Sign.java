package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField[] txt = new JTextField[3], txtNum = new JTextField[3], txtEmail = new JTextField[2];
	JComboBox com = new JComboBox<>(
			"naver.com,daum.net,gmail.com,nate.com,yahoo.com,outlook.com,kebi.com,korea.com,empal.com,hanmail.net,사용자 지정"
					.split(",")),
			comDate[] = new JComboBox[3];
	JRadioButton rad[] = new JRadioButton[2];

	public Sign() {
		super("회원가입", 400, 450);

		add(c = new JPanel(new GridLayout(0, 1, 0, 20)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,이메일,전화번호,생년월일,성별".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2, 15), 60, 20));

			if (i < 3) {
				tmp.add(txt[i] = new JTextField(20));
			} else if (i == 3) {
				for (int j = 0; j < txtNum.length; j++) {
					tmp.add(txtNum[j] = new JTextField(5));

					if (j < 2) {
						tmp.add(lbl("-", 0));
					}
				}
			} else if (i == 4) {
				tmp.add(txtEmail[0] = new JTextField(8));
				tmp.add(lbl("@", 0));
				tmp.add(com);
				tmp.add(txtEmail[1] = new JTextField(8));

				txtEmail[1].setVisible(false);
			} else if (i == 5) {
				var ca = "년,월,일".split(",");
				for (int j = 0; j < ca.length; j++) {
					tmp.add(comDate[j] = new JComboBox<>());
					tmp.add(lbl(ca[j], 0));
				}
			} else {
				var ca = "남자,여자".split(",");
				var bg = new ButtonGroup();
				for (int j = 0; j < ca.length; j++) {
					tmp.add(rad[j] = new JRadioButton(ca[j]));
					bg.add(rad[j]);
				}
			}

			c.add(tmp);
		}

		s.add(btnBlack("취소", a -> dispose()));
		s.add(btn("회원가입", a -> {
			for (var arr : new JTextField[][] { txt, txtNum, txtEmail }) {
				for (var t : arr) {
					if (t.getText().isEmpty() && t.isVisible()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}
			}

			if (!getOne("select * from user where u_id = ?", txt[1].getText()).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				return;
			}

			var phone = Stream.of(txtNum).map(t -> t.getText()).collect(Collectors.joining("-"));
			var email = txtEmail[0].getText() + "@"
					+ (txtEmail[1].isVisible() ? txtEmail[1].getText() : com.getSelectedItem());
			var date = LocalDate.of(toInt(comDate[0].getSelectedItem()), toInt(comDate[1].getSelectedItem()),
					toInt(comDate[2].getSelectedItem()));

			if (!phone.matches("\\d{3}-\\d{4}-\\d{4}")) {
				eMsg("전화번호 형식이 일치하지 않습니다.");
				return;
			}

			if (txtEmail[1].isVisible() && !email.matches("^\\w{3,}@\\w{2,}\\.\\w{2,}$")) {
				eMsg("이메일 형식이 일치하지 않습니다.");
				return;
			}

			execute("insert user values(0,?,?,?,?,?,?,?)", txt[1].getText(), txt[2].getText(), txt[0].getText(), phone,
					email, date.toString(), rad[0].isSelected() ? 0 : 1);
			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
		}));

		for (int i = 1900; i < 2023; i++) {
			comDate[0].addItem(i);
		}
		for (int i = 0; i < LocalDate.now().getMonthValue(); i++) {
			comDate[1].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < LocalDate.now().getDayOfMonth(); i++) {
			comDate[2].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < 3; i++) {
			comDate[i].setSelectedIndex(comDate[i].getItemCount() - 1);
		}

		comDate[0].addActionListener(a -> {
			for (int i = 0; i < 2; i++) {
				comDate[i + 1].removeAllItems();
			}

			var flag = LocalDate.now().getYear() == toInt(comDate[0].getSelectedItem());

			for (int i = 0; i < (flag ? LocalDate.now().getMonthValue() : 12); i++) {
				comDate[1].addItem(String.format("%02d", i + 1));
			}
		});
		comDate[1].addActionListener(a -> {
			if (comDate[1].getItemCount() == 0) {
				return;
			}

			var ld = LocalDate.of(toInt(comDate[0].getSelectedItem()), toInt(comDate[1].getSelectedItem()), 1);
			for (int i = 0; i < ld.lengthOfMonth(); i++) {
				comDate[2].addItem(String.format("%02d", i + 1));
			}
		});
		com.addActionListener(a -> {
			txtEmail[1].setVisible(com.getSelectedIndex() == com.getItemCount() - 1);
			repaint();
			revalidate();
		});

		rad[0].setSelected(true);

		setVisible(true);
	}
}
