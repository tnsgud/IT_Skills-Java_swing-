package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditInfo extends BaseFrame {
	JTextField txt[] = new JTextField[7];
	JComboBox<String> com = new JComboBox<>("남,여".split(","));

	public EditInfo() {
		super("정보수정", 400, 600);

		ui();
		data();

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if (e.getOppositeWindow() instanceof JDialog)
					return;

				dispose();
			}
		});

		setVisible(true);
	}

	private void data() {
		var rs = getRows(
				"select m_id, m_pw, m_pw, m_name1, m_name2, m_phone, m_email, m_sex from member where m_no = ?",
				user.get(0)).get(0);
		for (int i = 0; i < rs.size(); i++) {
			if (i == 7) {
				com.setSelectedIndex(toInt(rs.get(i)));
			} else {
				txt[i].setText(rs.get(i).toString());
			}
		}

		txt[0].setEnabled(false);
		com.setEnabled(false);
	}

	private void ui() {
		add(lbl("정보수정", 0, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(1)), "South");

		var cap = "아이디,비밀번호,비밀번호 확인,이름(한글),이름(영문),연락처,이메일,성별".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 5, 5));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));

			if (i == 7) {
				tmp.add(sz(com, 150, 20));
				com.setSelectedIndex(-1);
			} else {
				tmp.add(txt[i] = new JTextField(15));
			}

			c.add(tmp);
		}

		for (var ca : "정보수정,취소".split(",")) {
			s.add(btn(ca, a -> {
				if (ca.equals("정보수정")) {
					for (var t : txt) {
						if (t.getText().isEmpty() || com.getSelectedIndex() == -1) {
							eMsg("빈칸이 있습니다.");
							return;
						}
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

					if (txt[5].getText().matches("^\\d{3}-\\d{4}-\\d{2}$")) {
						eMsg("전화번호를 확인해주세요.");
						return;
					}

					if (txt[6].getText().indexOf("@") == -1 || txt[6].getText().indexOf(".") == -1) {
						eMsg("이메일을 확인해주세요.");
						return;
					}

					iMsg("정보수정이 완료되었습니다.");

					var data = new ArrayList<String>();

					Stream.of(txt).forEach(t -> {
						if (Arrays.asList(txt).indexOf(t) == 4) {
							data.add(t.getText().toUpperCase());
						} else {
							data.add(t.getText());
						}
					});
					data.add(com.getSelectedIndex() + "");

					execute("update member set m_pw = ?, m_name1 = ?, m_name2 = ?, m_phone = ?, m_email = ? ",
							txt[1].getText(), txt[3].getText(), txt[4].getText(), txt[5].getText(), txt[6].getText());

					dispose();
				} else {
					dispose();
				}
			}));
		}
	}
}
