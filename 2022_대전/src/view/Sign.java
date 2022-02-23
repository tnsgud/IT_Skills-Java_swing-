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

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Sign extends BaseFrame {
	JLabel img;
	JTextField[] txt = { new JTextField(15), new JTextField(15), new JTextField(15) };
	String[] cap = "이름,ID,PW".split(",");
	File f;

	public Sign() {
		super("회원가입", 400, 220);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(w = new JPanel(new BorderLayout(5, 5)), "West");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

		w.add(sz(img = new JLabel(), 130, 130));
		w.add(btn("사진 등록", a -> {
			var ch = new JFileChooser("./Datafiles/회원사진");
			var an = ch.showOpenDialog(null);
			if (an == JFileChooser.APPROVE_OPTION) {
				f = ch.getSelectedFile();
				img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getPath()).getScaledInstance(130, 130,
						Image.SCALE_SMOOTH)));
			}
		}), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i]);
			c.add(p);
		}

		var p = new JPanel(new FlowLayout(4));
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

					if (!getOne("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
						eMsg("이미 존재하는 아이디입니다.");
						txt[1].setText("");
						txt[1].requestFocus();
					}

					var pw = txt[2].getText();
					if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[\\W].*"))
							|| pw.length() < 5) {
						eMsg("비밀번호를 확인해주세요.");
						return;
					}

					if (f == null) {
						eMsg("사진을 등록해주세요.");
						return;
					}

					iMsg("회원가입이 완료되었습니다.");
					try {
						execute("insert into user values(0, ?, ?, ?, ?)", txt[0].getText(), txt[1].getText(),
								txt[2].getText(), new FileInputStream(f));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
		}

		c.add(p);

		img.setBorder(new LineBorder(Color.black));
	}
}
