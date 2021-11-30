package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SignPage extends BasePage {

	JComboBox<String> locCombo;
	JTextField id, pw, pwchk, birth, pos, phone, rrfn, rrln;
	String cap[] = "아이디,비밀번호,지역,위치,전화번호,주민번호 앞자리".split(",");

	public SignPage() {
		init();
	}

	void init() {
		add(m = new JPanel(new GridBagLayout()));
		m.add(sz(c = new JPanel(new GridLayout(0, 1, 5, 5)), 250, 350));
		setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		JComponent jc[] = { id = new JTextField(), pw = new JTextField(), locCombo = new JComboBox<String>(),
				birth = new JTextField(), phone = new JTextField(), rrfn = new JTextField() };

		for (int i = 0; i < cap.length; i++) {
			var tmp_n = new JPanel(new GridLayout(1, 0, 5, 5));
			var tmp_c = new JPanel(new GridLayout(1, 0, 5, 5));
			if (i == 1) {
				tmp_n.add(lbl(cap[i], JLabel.LEFT));
				tmp_n.add(lbl("비밀번호 확인", JLabel.LEFT));
				c.add(tmp_n);
				tmp_c.add(jc[i]);
				tmp_c.add(pwchk = new JTextField());
				c.add(tmp_c);
			} else if (i == 2) {
				tmp_n.add(lbl(cap[i], JLabel.LEFT));
				tmp_n.add(lbl("위치", JLabel.LEFT));
				c.add(tmp_n);
				tmp_c.add(jc[i]);
				tmp_c.add(pos = new JTextField());
				c.add(tmp_c);
			} else if (i == 3) {

			} else if (i == 5) {
				tmp_n.add(lbl(cap[i], JLabel.LEFT));
				tmp_n.add(lbl("주민번호 뒷자리", JLabel.LEFT));
				c.add(tmp_n);
				tmp_c.add(jc[i]);
				tmp_c.add(rrln = new JTextField());
				c.add(tmp_c);
			} else {
				c.add(lbl(cap[i], JLabel.LEFT));
				c.add(jc[i]);
			}

		}
		c.add(btn("회원가입", a -> {

		}), "South");
		c.add(hyplbl("이미 계정이 있으십니까?", JLabel.LEFT, 13, Font.PLAIN, Color.ORANGE, new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mf.swapPage(new LoginPage());
				super.mousePressed(e);
			}
		}));

		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}
