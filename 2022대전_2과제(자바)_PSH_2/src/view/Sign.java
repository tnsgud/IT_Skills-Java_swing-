package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = {
			new JComboBox<>(getRows("select c_name from city").stream().map(a -> a.get(0)).toArray(String[]::new)),
			new JComboBox<>() };
	JRadioButton rad[] = new JRadioButton[2];
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
				tmp.add(txt[i] = new JTextField(15));

				if (i == 1) {
					tmp.add(btn("중복확인", a -> {
						if (txt[1].getText().isEmpty()) {
							eMsg("아이디를 입력하세요.");
							return;
						}

						if (!getOne("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
							eMsg("이미 존재하는 아이디입니다.");
							return;
						}

						iMsg("사용 가능한 아이디입니다.");
						idCheck = true;
					}));
				}
			} else if (i < 6) {
				tmp.add(sz(com[i - 4], 80, 20));
			} else {
				var bg = new ButtonGroup();

				for (int j = 0; j < 2; j++) {
					bg.add(rad[j] = new JRadioButton("야채,과일".split(",")[j]));
					tmp.add(rad[j]);
				}
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
				if (c.getSelectedIndex() == 0) {
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
			execute("isnert user values(0,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), rad[0].isSelected() ? 1 : 2,
					getOne("select t_no from town where c_no=? and t_name = ?", com[0].getSelectedItem(),
							com[1].getSelectedItem()));
			dispose();
		}));

		txt[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				idCheck = false;
			}
		});
		evt(txt[2], e -> {
			new KnockCode(txt[2]).addWindowListener(new Before(this));
		});
		com[0].addActionListener(a -> {
			com[1].removeAllItems();

			getRows("select t_name from town where c_no = ?", com[0].getSelectedIndex()).stream()
					.forEach(e -> com[1].addItem(e.get(0)));
			com[1].insertItemAt("", 0);
			com[1].setSelectedIndex(0);
		});

		txt[2].setEditable(false);
		com[0].insertItemAt("", 0);
		com[0].setSelectedIndex(0);
		rad[0].setSelected(true);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
