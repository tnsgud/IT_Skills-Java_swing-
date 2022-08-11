package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[8];
	JComboBox<String> com = new JComboBox<>("남,여".split(","));
	boolean idChk = false;

	public Sign() {
		super("회원가입", 400, 500);

		add(lbl("회원가입", 0, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(1, 10, 10)), "South");

		var cap = "아이디,비밀빈호,비밀번호 확인,이름(한글),이름(영문),연락처,생년월일,이메일,성별".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));

			if (i == 8) {
				tmp.add(com);
			} else {
				tmp.add(txt[i] = new JTextField(15));

				if (i == 0) {
					tmp.add(btn("중복확인", a -> {
						var id = txt[0].getText();
						if (id.isEmpty()) {
							eMsg("아이디를 입력하세요.");
							return;
						}

						if (!getOne("select * from member where m_id = ?", id).isEmpty() || id.equals("admin")) {
							eMsg("아아디가 중복되었습니다.");
							txt[0].setText("");
							idChk = false;
							return;
						}

						iMsg("사용가능한 아이디입니다.");
						idChk = false;
					}));
				}
			}

			c.add(tmp);
		}

		for (var ca : "회원가입,취소".split(",")) {
			s.add(btn(ca, a -> {
				if (a.getActionCommand().equals("회원가입")) {
					for (var t : txt) {
						if (t.getText().isEmpty() || com.getSelectedIndex() == -1) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					if (!idChk) {
						eMsg("아이디 중복확인을 해주요.");
						return;
					}

					var pw = txt[1].getText();
					if (!pw.equals(txt[2].getText())) {
						eMsg("비밀번호 확인이 일치하지 않습니다.");
						return;
					}

					if (txt[3].getText().matches(".*[a-zA-Z].*")) {
						eMsg("한글 이름을 확인해주세요.");
						return;
					}

					if (txt[4].getText().matches(".*[ㄱ-힣].*")) {
						eMsg("영문 이름을 확인해주세요.");
						return;
					}

					if (txt[4].getText().split(" ").length != 2) {
						eMsg("영문 이름은 성과 이름을 구분해주세요.");
						return;
					}

					if (!txt[5].getText().matches("^\\d{3}-\\d{4}-\\d{2}$")) {
						eMsg("전화번호를 확인해주세요.");
						return;
					}
					
					try {
						var date = LocalDate.parse(txt[6].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

						if (date.isAfter(LocalDate.now())) {
							eMsg("생년월일을 확인해주세요.");
							return;
						}
					} catch (Exception e) {
						eMsg("생년월일을 확인해주세요.");
						return;
					}
					
					if (txt[7].getText().indexOf("@") == -1 || txt[7].getText().indexOf(".") == -1) {
						eMsg("이메일을 확인해주세요.");
						return;
					}
					
					iMsg("회원가입이 완료되었습니다.");
					
					var data = new ArrayList<String>();
					Stream.of(txt).filter(t->Arrays.asList(txt).indexOf(t) != 2) .forEach(t -> {
						if (Arrays.asList(txt).indexOf(t) == 4) {
							data.add(t.getText().toUpperCase());
						} else {
							data.add(t.getText());
						}
					});
					data.add(com.getSelectedIndex() + "");

					execute("insert into member values(0, ?, ?, ?, ?, ?, ?, ?, ?)", data.toArray());
				}

				dispose();
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
