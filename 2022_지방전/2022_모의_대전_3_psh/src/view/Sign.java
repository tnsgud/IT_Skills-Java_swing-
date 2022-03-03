package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Sign extends BaseFrame {
	JLabel img = new JLabel(" ");
	JFileChooser ch = new JFileChooser("./Datafiles/");
	String[] cap = "이름,ID,PW".split(",");
	JTextField[] txt = new JTextField[3];
	File f;

	public Sign() {
		super("회원가입", 400, 220);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(w = new JPanel(new BorderLayout(10, 10)), "West");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));

		w.add(sz(img, 120, 120));
		w.add(btn("사진 등록", a -> {
			var an = ch.showOpenDialog(null);
			if (an == JFileChooser.APPROVE_OPTION) {
				f = ch.getSelectedFile();
				img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getPath()).getScaledInstance(120, 120,
						Image.SCALE_SMOOTH)));
			}
		}), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i] + ": ", 2), 50, 20));
			p.add(txt[i] = new JTextField(15));
			c.add(p);
		}

		var p = new JPanel(new FlowLayout(2));

		c.add(p);

		for (var c : "회원가입,취소".split(",")) {
			p.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					if (!getOne("select u_id from user where u_id=?", txt[1].getText()).isEmpty()) {
						eMsg("이미 존재하는 아이디입니다.");
						return;
					}

					var pw = txt[2].getText();
					if (!(pw.matches(".*[\\W].*") && pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*"))
							|| pw.length() < 4) {
						eMsg("비밀번호를 확인해주세요.");
						return;
					}

					if (f == null) {
						eMsg("사진을 등록해주세요.");
						return;
					}

					try {
						execute("insert into user values(0, ?, ?, ?, ?)", txt[0].getText(), txt[1].getText(), txt[2].getText(), new FileInputStream(f));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					iMsg("회원가입이 완료되었습니다.");
				}
			}));
		}

		img.setBorder(new LineBorder(Color.black));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
