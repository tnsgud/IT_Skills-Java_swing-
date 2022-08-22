package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	ButtonGroup group = new ButtonGroup();
	JRadioButton rad[] = Stream.of("야채,과일".split(",")).map(c -> {
		var r = new JRadioButton(c);
		group.add(r);
		return r;
	}).toArray(JRadioButton[]::new);
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = { new JComboBox<>(("," + getRows("select c_name from city").stream().map(a -> a.get(0).toString())
			.collect(Collectors.joining(","))).split(",")), new JComboBox<>(",".split(",")) };
	boolean idCheck = false;

	public Sign() {
		super("회원가입", 350, 300);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,생년월일,지역,세부지역,구분".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));
			if (i < 4) {
				tmp.add(txt[i] = i != 2 ? new JTextField(15) : new JPasswordField(15));

				if (i == 1) {
					tmp.add(btn("중복확인", a -> {
						if (txt[1].getText().isEmpty()) {
							eMsg("아이디를 입력하세요.");
							return;
						}

						if (!getOne("seelct * from user wher u_id = ?", txt[1].getText()).isEmpty()) {
							eMsg("이미 존재하는 아이디입니다.");
							txt[1].setText("");
							return;
						}

						iMsg("사용가능한 아이디입니다.");
						idCheck = true;
					}));
				}
			} else if (i < 6) {
				tmp.add(sz(com[i - 4], 100, 20));
				com[i - 4].setSelectedIndex(-1);
			} else {
				Stream.of(rad).forEach(tmp::add);
			}

			c.add(tmp);
		}

		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("공백이 존재합니다.");
					return;
				}
			}

			for (var c : com) {
				if (c.getSelectedIndex() < 1) {
					eMsg("공백이 존재합니다.");
					return;
				}
			}

			if (!idCheck) {
				eMsg("중복확인을 해주세요.");
				return;
			}

			if (txt[2].getText().length() != 4) {
				eMsg("비밀번호는 4글자로 해주세요.");
				return;
			}

			try {
				if (LocalDate.parse(txt[3].getText()).isAfter(LocalDate.now())) {
					eMsg("생년월일을 확인해주세요.");
					return;
				}
			} catch (Exception e) {
				eMsg("생년월일을 확인해주세요.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			execute("insert user value(0, ?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), rad[0].isSelected() ? 1 : 2,
					getOne("select t_no from town where t_name = ?", com[1].getSelectedItem()));
			dispose();
		}));

		txt[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				idCheck = false;
			}
		});
		txt[2].setEditable(false);
		event(txt[2], e -> new KnockCode(txt[2]));

		com[0].addActionListener(a -> {
			com[1].removeAllItems();

			getRows("select t_name from town where c_no = ?", com[0].getSelectedIndex()).stream()
					.map(t -> t.get(0).toString()).forEach(com[1]::addItem);
		});
		rad[0].setSelected(true);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
