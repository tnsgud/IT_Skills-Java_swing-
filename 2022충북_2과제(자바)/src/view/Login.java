package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];
	JCheckBox chk = new JCheckBox("아이디 저장");
	Preferences pref = Preferences.userNodeForPackage(BaseFrame.class);

	public Login() {
		super("로그인", 400, 150);

		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new BorderLayout(5, 5)), "South");

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
				new Admin().addWindowListener(new Before(this));
				txt[0].setText("");
				txt[1].setText("");
				return;
			}

			var rs = getRows("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				return;
			}

			user = rs.get(0);

			iMsg(user.get(3) + "님 환영합니다.");

			Main.lbl[0].setText("로그아웃");
			Main.lbl[0].setIcon(getIcon("./datafile/아이콘/UnLock.png", 15, 15));
			Main.lbl[1].setVisible(false);
			
			if (chk.isSelected()) {
				pref.put("id", txt[0].getText());
			} else {
				pref.remove("id");
			}

			dispose();
		}), "East");

		var p = "아이디 또는 이메일을 입력해 주세요.,비밀번호를 입력해 주세요.".split(",");
		for (int i = 0; i < p.length; i++) {
			txt[i] = i == 0 ? new HintField(p[i], 1) : new HintPassField(p[i], 1);
			cc.add(txt[i]);
		}

		s.add(chk, "West");
		s.add(se = new JPanel(), "East");

		var cap = "회원가입,아이디 찾기,비밀번호 찾기".split(",");
		for (int j = 0; j < cap.length; j++) {
			var l = sz(lbl(cap[j], 0, 0, 12, e -> {
				var me = (JLabel) e.getSource();

				if (me.getText().equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else if (me.getText().equals("회원가입")) {

					new BaseFrame("아이디 찾기", 500, 500).setVisible(true);
				} else {
					new BaseFrame("비밀번호 찾기", 500, 500).setVisible(true);
				}
			}), 80, 15);

			se.add(l);

			if (j < cap.length - 1) {
				l.setBorder(new MatteBorder(0, 0, 0, 1, Color.black));
			}
		}

		if (pref.get("id", null) != null) {
			chk.setSelected(true);
			txt[0].setText(pref.get("id", txt[0].getText()));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
