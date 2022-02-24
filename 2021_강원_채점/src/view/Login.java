package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	String[] h = "Id,Password".split(",");
	JTextField txt[] = new JTextField[2];
	JPanel r1, r2;
	JLabel find, sign;

	public Login() {
		super(1200, 600);

		ui();
		event();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

		setVisible(true);
	}

	private void event() {
		Stream.of(sign, find).forEach(l -> {
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getSource() == find) {
						new Find();
					} else {
						new Sign();
					}
				}
			});
		});
	}

	private void ui() {
		var c_c = new JPanel(new GridLayout(0, 1, 5, 5));
		var c_e = new JPanel(new BorderLayout(5, 5));

		add(img("login.jpg", 800, 600), "West");
		add(r1 = new JPanel(new GridBagLayout()));
		r1.add(r2 = new JPanel(new BorderLayout(100, 100)));
		r2.add(c = new JPanel(new BorderLayout(5, 5)));
		r2.add(s = new JPanel(new FlowLayout(0)), "South");

		c.add(lbl("로그인", 2, 25), "North");
		c.add(c_c);
		c.add(c_e, "East");

		s.add(sign = lbl("새로운 계정 만들기", 2, 12));

		for (int i = 0; i < h.length; i++) {
			c_c.add(sz(txt[i] = (i == 0 ? new JHintField(h[i], 15) : new JHintPassword(h[i], 15)), 1, 25));
		}
		c_c.add(find = lbl("아이디 비밀번호 찾기", 2, 12));

		c_e.add(btn("다음", a -> {
			Stream.of(txt).forEach(t -> {
				if (t.getText().isEmpty()) {
					eMsg((t == txt[0] ? "아이디" : "비밀번호") + "를 입력해주세요.");
					return;
				}
			});

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				new AdminMain();
			}

			var rs = rs("select * from user where id=? and pwd=?", txt[0].getText(), txt[1].getText());
			try {
				if (rs.next()) {
					Stream.of(txt).forEach(t -> t.setText(""));
					uno = rs.getInt(1);
					new UserMain().addWindowListener(new Before(this));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));
		c_e.add(themeBtn(this), "South");
	}

	public static void main(String[] args) {
		new Login();
	}
}
