package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import view.BaseFrame.Before;

public class Login extends BaseFrame {
	JPanel r1, r2;
	String[] h = "Id,Password".split(",");
	JTextField txt[] = new JTextField[h.length];
	JLabel find, sign;

	public Login() {
		super(1000, 500);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		Stream.of(find, sign).forEach(l -> l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getSource() == find) {
					new Find();
				} else {
					new Sign();
				}
			}
		}));
	}

	private void ui() {
		add(img("login.jpg", 600, 500), "West");
		add(r1 = new JPanel(new GridBagLayout()));
		r1.add(r2 = new JPanel(new BorderLayout(100, 100)));
		r2.add(c = new JPanel(new BorderLayout(5, 5)));
		r2.add(s = new JPanel(new FlowLayout(0)), "South");
		c.add(lbl("로그인", 2, 35), "North");

		{
			var p = new JPanel(new GridLayout(0, 1));
			for (int i = 0; i < h.length; i++) {
				txt[i] = i == 0 ? new JHintField(15, h[i]) : new JHintPassword(15, h[i]);
				p.add(sz(txt[i], 1, 30));
			}
			p.add(find = lbl("아이디 비밀번호 찾기", 2, 12));
			c.add(p);
		}

		{
			var p = new JPanel(new BorderLayout(5, 5));
			p.add(btn("다음", a -> {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg((t == txt[0] ? "아이디" : "비밀번호") + "를 입력해주세요.");
						return;
					}
				}

				if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
					iMsg("관리자로 로그인합니다.");
					new AdminMain().addWindowListener(new Before(Login.this));
					return;
				}

				var rs = rs("select * from user where id=? and pwd=?", txt[0].getText(), txt[1].getText());
				try {
					if (rs.next()) {
						uno = rs.getInt(1);
						Stream.of(txt).forEach(t -> t.setText(""));
						new UserMain().addWindowListener(new Before(Login.this));
						return;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}));
			p.add(themeBtn(Login.this), "South");
			c.add(p, "East");
		}

		s.add(sign = lbl("새로운 계정 만들기→", 2, 15));
	}

	public static void main(String[] args) {
		new Login();
	}
}
