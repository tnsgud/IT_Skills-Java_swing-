package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class Login extends BaseFrame {
	static boolean isSave = false;
	static String id = "";
	JCheckBox chk = new JCheckBox("아이디 저장");
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 400, 150);

		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자님 환영합니다.");
				new AdminMain().addWindowListener(new Before(this));
				Stream.of(txt).forEach(t -> t.setText(""));
				return;
			}

			var rs = getRows("select * from user where (u_email = ? and u_pw = ?) or (u_id = ? and u_pw = ?)",
					txt[0].getText(), txt[1].getText(), txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				return;
			}

			user = rs.get(0);
			iMsg(user.get(3) + "님 환영합니다.");
			Main.login();
			dispose();
		}), "East");

		s.add(chk, "West");
		s.add(se = new JPanel(new FlowLayout(2, 5, 5)), "East");

		var cap = "아이디 또는 비밀번호를 입력해 주세요.,비밀번호를 입력해주세요.".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(txt[i] = i == 0 ? txt(cap[i], 0) : pw(cap[i], 0));
		}

		cap = "회원가입,아이디 찾기,비밀번호 찾기".split(",");
		for (int j = 0; j < cap.length; j++) {
			se.add(sz(event(lbl(cap[j], 0, 0, 10), e -> {
				var me = (JLabel) e.getSource();

				switch (me.getText()) {
				case "회원가입":
					new Sign().addWindowListener(new Before(this));
					break;
				case "아이디 찾기":
					new FineID().addWindowListener(new Before(this));
					break;
				case "비밀번호 찾기":
					new FinePW().addWindowListener(new Before(this));
					break;
				}
			}), 80, 15));

			if (j < 2) {
				((JComponent) se.getComponent(j)).setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
			}
		}

		if (isSave) {
			chk.setSelected(true);
			txt[0].setText(id);
		}

		chk.addActionListener(e -> {
			isSave = chk.isSelected();
			id = isSave ? txt[0].getText() : "";
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
