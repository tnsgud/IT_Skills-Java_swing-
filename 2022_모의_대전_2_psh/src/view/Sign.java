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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import db.DB;
import tool.Tool;

public class Sign extends BaseFrame implements Tool {
	JLabel img;
	String path;
	JPanel c_c, c_s;
	String[] cap = "이름,ID,PW".split(",");
	JTextField txt[] = new JTextField[3];
	JFileChooser ch = new JFileChooser("./Datafiles/회원사진/");

	public Sign() {
		super("회원가입", 400, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(w = new JPanel(new BorderLayout(5, 5)), "West");
		add(c = new JPanel(new BorderLayout()));

		w.add(sz(img = new JLabel(), 130, 130));
		w.add(btn("사진 등록", a -> {
			var r = ch.showOpenDialog(this);
			if (r == JFileChooser.APPROVE_OPTION) {
				this.path = ch.getSelectedFile().getPath();
				img.setIcon(new JLabel(new ImageIcon(
						Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(130, 130, Image.SCALE_SMOOTH)))
								.getIcon());
				repaint();
			}
		}), "South");

		c.add(c_c = new JPanel(new GridLayout(0, 1)));
		c.add(c_s = new JPanel(new FlowLayout(2)), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i] + ":", 2), 40, 20));
			p.add(txt[i] = new JTextField(15));
			c_c.add(p);
		}

		for (var c : "회원가입,취소".split(",")) {
			c_s.add(btn(c, a -> {
				if (c.equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					if (!DB.getOne("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
						eMsg("이미 존재하는 아이디 입니다.");
						txt[1].setText("");
						txt[1].requestFocus();
						return;
					}

					var pw = txt[2].getText();
					if (!(pw.matches(".*[0-9].*") || pw.matches(".*[\\W].*") || pw.matches(".*[a-zA-Z].*"))
							&& pw.length() < 4) {
						eMsg("비밀번호를 확인해주세요.");
						return;
					}

					if (path == null) {
						eMsg("사진을 등록해주세요.");
						return;
					}

					try {
						var f = new File(path);

						DB.execute("insert into user values(0, ?, ?, ?, ?)", txt[0].getText(), txt[1].getText(),
								txt[2].getText(), new FileInputStream(f));

						var image = ImageIO.read(f);
						ImageIO.write(image, "jpg", new File("./Datafiles/회원사진/"
								+ DB.getOne("select u_name from user order by u_no desc") + ".jpg"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					iMsg("회원가입이 완료되었습니다.");
					dispose();
				}
			}));
		}

		img.setBorder(new LineBorder(Color.black));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		new Sign();
	}
}
