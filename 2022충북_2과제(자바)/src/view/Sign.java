package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[3], txtNum[] = new JTextField[3], txtEmail = new JTextField(15),
			txtDomain = new JTextField(15);
	JComboBox comDomain, comDate[] = new JComboBox[3];
	JRadioButton rad[] = new JRadioButton[2];
	LocalDate date;

	public Sign() {
		super("회원가입", 400, 450);

		add(c = new JPanel(new GridLayout(0, 1, 0, 20)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,전화번호,이메일,생년월일,성별".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			var temp = new JPanel();

			tmp.add(sz(lbl(cap[i], 2), 60, 20), "West");
			tmp.add(temp);

			if (i < 3) {
				tmp.add(txt[i] = new JTextField());
			} else if (i == 3) {
				for (int j = 0; j < 3; j++) {
					temp.add(txtNum[j] = sz(new JTextField(), 90, 30));

					if (j < 2) {
						temp.add(lbl("-", 0));
					}
				}
			} else if (i == 4) {
				temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));

				temp.add(txtEmail);
				temp.add(lbl("@", 0));
				temp.add(comDomain = new JComboBox<>(
						"naver.com,daum.net,gmail.com,nate.com,yahoo.com,outlook.com,kebi.com,korea.com,empal.com,hanmail.net,사용자 지정"
								.split(",")));
				temp.add(txtDomain);

				comDomain.addActionListener(a -> {
					txtDomain.setVisible(comDomain.getSelectedIndex() == comDomain.getItemCount() - 1);
					txtDomain.setText(txtDomain.isVisible() ? txtDomain.getText() : "");
					temp.revalidate();
				});
			} else if (i == 5) {
				temp.setLayout(new FlowLayout(0));
				var ca = "년,월,일".split(",");

				for (int j = 0; j < comDate.length; j++) {
					temp.add(comDate[j] = new JComboBox<>());
					temp.add(lbl(ca[j], 0));
				}
			} else {
				temp.setLayout(new FlowLayout(0));
				var bg = new ButtonGroup();

				for (int j = 0; j < rad.length; j++) {
					temp.add(rad[j] = new JRadioButton("남,여".split(",")[j]));
					bg.add(rad[j]);
				}
			}

			c.add(tmp);
		}

		for (int i = 1900; i <= 2022; i++) {
			comDate[0].addItem(i + "");
		}

		for (int i = 1; i <= now.getMonthValue(); i++) {
			comDate[1].addItem(String.format("%02d", i));
		}

		for (int i = 1; i <= now.getDayOfMonth(); i++) {
			comDate[2].addItem(String.format("%02d", i));
		}

		for (int i = 0; i < comDate.length; i++) {
			comDate[i].setSelectedIndex(comDate[i].getItemCount() - 1);
		}

		comDate[0].addActionListener(a -> {
			date = LocalDate.of(toInt(comDate[0].getSelectedItem()), 1, 1);

			comDate[1].removeAllItems();
			comDate[2].removeAllItems();

			var flag = date.getYear() == now.getYear();

			for (int i = 0; i < (flag ? now.getMonthValue() : 12); i++) {
				comDate[1].addItem(String.format("%02d", i + 1));
			}

			for (int i = 0; i < (flag ? now.getDayOfMonth() : date.lengthOfMonth()); i++) {
				comDate[2].addItem(String.format("%02d", i + 1));
			}
		});

		comDate[1].addActionListener(a -> {
			if (comDate[2].getItemCount() == 0 || comDate[1].getItemCount() == 0)
				return;

			comDate[1].removeAllItems();

			date = LocalDate.of(toInt(comDate[0].getSelectedItem()), toInt(comDate[1].getSelectedItem()), 1);

			for (int i = 0; i < date.lengthOfMonth(); i++) {
				comDate[2].addItem(String.format("%02d", i + 1));
			}
		});

		rad[0].setSelected(true);

		s.add(btnBlack("취소", a -> dispose()));
		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (!getOne("select * from userr where u_id =?", txt[1].getText()).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				return;
			}

			var phone = Stream.of(txtNum).map(JTextField::getText).collect(Collectors.joining("-"));

			if (!phone.matches("^\\d{3}-\\d{4}-\\d{4}$")) {
				eMsg("전화번호 형식이 일치하지 않습니다.");
				return;
			}

			var email = txtEmail.getText() + "@" + (comDomain.getSelectedItem().equals("사용자 지정") ? txtDomain.getText()
					: comDomain.getSelectedItem());
			if (!email.matches("^.{3,}@.{2,}\\..{2,]$")) {
				eMsg("이메일 형식이 일치하지 않습니다.");
				return;
			}

			var birth = Stream.of(comDate).map(c -> c.getSelectedObjects().toString()).collect(Collectors.joining("-"));
//			안해도 됨
//			try {
//				if (LocalDate.parse(birth).isAfter(now)) {
//					eMsg("생년월일 형식이 일치하지 않습니다.");
//					return;
//				}
//			} catch (Exception e) {
//				eMsg("생년월일 형식이 맞지 않습니다.");
//				return;
//			}

			execute("insert user values(0, ?, ?, ?, ?, ?, ?, ?)", txt[1].getText(), txt[2].getText(), txt[0].getText(),
					phone, email, birth, rad[0].isSelected() ? 0 : 1);
			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
		}));

		txtDomain.setVisible(false);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
