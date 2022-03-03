package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JPanel r1, r2;
	String[] h = "Id,Password".split(",");
	JTextField txt[] = new JTextField[2];
	JLabel find, sign;

	public Login() {
		super(1000, 500);

		ui();
		event();
		
		setVisible(true);
	}

	private void event() {
		Stream.of(find, sign).forEach(l->l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getSource() == find) {
					new Find();
				}else {
					new Sign();
				}
			}
		}));
	}

	private void ui() {
		var c_c = new JPanel(new GridLayout(0, 1, 5, 5));
		var c_e = new JPanel(new BorderLayout(5, 5));

		add(img("login.jpg", 600, 500), "West");
		add(r1 = new JPanel(new GridBagLayout()));
		r1.add(r2 = new JPanel(new BorderLayout(100, 100)));
		r2.add(c = new JPanel(new BorderLayout(5, 5)));
		r2.add(s = new JPanel(new FlowLayout(0)), "South");

		c.add(lbl("로그인", 2, 35), "North");
		c.add(c_c);
		c.add(c_e, "East");

		for (int i = 0; i < h.length; i++) {
			c_c.add(sz(txt[i] = (i == 0 ? new JHintField(h[i], 15) : new JHintPassword(h[i], 15)), 1, 25));
		}
		c_c.add(find = lbl("아이디 비밀번호 찾기", 2, 12));
		
		c_e.add(btn("다음", a->{}));
		c_e.add(themeBtn(this), "South");
		
		s.add(sign = lbl("새로운 계정 만들기 →", 2, 12));
	}

	public static void main(String[] args) {
		new Login();
	}
}
