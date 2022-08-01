package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = {
			new JComboBox<>(
					getRows("select c_name from city").stream().map(a -> a.get(0).toString()).toArray(String[]::new)),
			new JComboBox<>() };
	JRadioButton rad[] = new JRadioButton[2];
	boolean idCheck = false;

	public Sign() {
		super("회원가입", 500, 500);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,생년월일,지역,세부지역,구분".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2), 60, 20));

			if (i < 4) {
				tmp.add(txt[i] = new JTextField(15));

				txt[i].setEnabled(i != 2);

				if (i == 1) {
					tmp.add(btn("중복확인", a -> {
						if (txt[1].getText().isEmpty()) {
							eMsg("아이디를 입력하세요.");
							idCheck = false;
							return;
						}

						if (!getOne("select * from user where u_id= ?", txt[1].getText()).isEmpty()) {
							eMsg("아미 존재하는 아이디입니다.");
							idCheck = false;
							return;
						}

						iMsg("사용 가능한 아이디입니다.");
						idCheck = true;
					}));
				}
			} else if (i < 6) {
				com[i - 4].insertItemAt("", 0);
				com[i - 4].setSelectedIndex(0);
				tmp.add(sz(com[i - 4], 80, 25));
			} else {
				var bg = new ButtonGroup();
				var ca = "야채,과일".split(",");

				for (int j = 0; j < ca.length; j++) {
					tmp.add(rad[j] = new JRadioButton(ca[j]));
					bg.add(rad[j]);

					rad[j].setOpaque(false);
				}

				rad[0].setSelected(true);
			}

			c.add(tmp);
		}

		txt[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				idCheck = false;
			}
		});
		txt[2].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new KnockCode(txt[2]);
			}
		});
		com[0].addActionListener(a -> {
			com[1].removeAllItems();

			com[1].addItem("");

			getRows("select t_name from town where c_no = ?", com[0].getSelectedIndex())
					.forEach(e -> com[1].addItem(e.get(0).toString()));
		});

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
				eMsg("비밀번호는 4를자로 해주세요.");
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
			execute("insert user values(0, ?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), rad[0].isSelected() ? 1 : 2,
					getOne("select t_no from town where c_no = ? and t_name = ?", com[0].getSelectedIndex(),
							com[1].getSelectedItem()));
			dispose();
		}));

		setVisible(true);
	}
}
