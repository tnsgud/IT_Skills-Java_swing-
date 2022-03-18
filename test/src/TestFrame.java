import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class TestFrame extends BaseFrame {
	JTextField txt[] = new JTextField[4], detail, mail;
	JComboBox domain, edu, addr;
	JRadioButton rbtn[] = new JRadioButton[2];
	JLabel img;
	JPanel m;

	public TestFrame() {
		super("회원가입", 500, 400);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cc = new JPanel(new BorderLayout()));
		c.add(cs = new JPanel(new GridLayout(0, 1)));

		cc.add(m = new JPanel(new GridLayout(0, 1)));
		cc.add(sz(img = new JLabel(), 130, 130), "East");

		var cap = "이름,아아디,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(15));
			m.add(p);
		}

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap2[i], 2), 80, 20));
			if (i == 0) {
				p.add(mail = new JTextField(7));
				p.add(lbl("@", 0));
				p.add(domain = new JComboBox<>(
						"naver.com, outlook.com, daum.com, gmail.com, nate.com, kebi.com, yahoo.com, korea.com, empal.com, hanmail.net"
								.split(", ")));
			} else if (i == 1) {
				var bg = new ButtonGroup();
				var r = "남,여".split(",");
				for (int j = 0; j < r.length; j++) {
					p.add(rbtn[j] = new JRadioButton(r[j]));
					bg.add(rbtn[j]);
				}
				rbtn[0].setSelected(true);
			} else if (i == 2) {
				p.add(edu = new JComboBox<>(graduate));
			} else {
				p.add(sz(addr = new JComboBox<>(",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",")), 105, 25));
			}
			cs.add(p);
		}

		s.add(btn("가입", a -> {
		}));

		c.setBorder(
				new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"), new EmptyBorder(5, 5, 5, 5)));
		img.setBorder(new LineBorder(Color.black));

		setVisible(true);
	}

	public static void main(String[] args) {
		new TestFrame();
	}
}
