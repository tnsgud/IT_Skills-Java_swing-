package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

public class Sign extends BaseFrame {
	JPanel c_c, c_s;
	String path;
	JLabel img;
	String[] cap = "이름,ID,PW".split(","), bcap = "회원가입,취소".split(",");
	JTextField txt[] = { new JTextField(12), new JTextField(12), new JTextField(12) };
	JFileChooser chooser = new JFileChooser("./Datafiles/회원사진");

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
			var r = chooser.showOpenDialog(Sign.this);
			if (r == JFileChooser.APPROVE_OPTION) {
				this.path = chooser.getSelectedFile().getPath();
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
			p.add(txt[i]);
			c_c.add(p);
		}

		for (var c : bcap) {
			c_s.add(btn(c, a -> {
				if (a.getActionCommand().contentEquals(bcap[0])) {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					if (DB.getOne("select * from user where u_id like ?", "%" + txt[1].getText() + "%") != null) {
						eMsg("이미 존재하는 아이디 입니다.");
						txt[1].setText("");
						txt[1].requestFocus();
						return;
					}

					var pw = txt[2].getText();
					if (!(pw.matches(".*[0-9].* ") && pw.matches(".*[a-zA-Z].* ") && pw.matches(".*[\\W].* "))
							&& pw.length() < 4) {
						eMsg("비밀번호를 확인해주세요.");
						return;
					}

					if (path == null) {
						eMsg("사진을 등록해주세요.");
						return;
					}
					
					iMsg("회원가입이 완료되었습니다.");
					try {
						var f = new File(path);
						DB.execute("insert into user values(0, ?, ?, ?, ?)", txt[0].getText(), txt[1].getText(), txt[2].getText(), "");
						
						var fis = new FileInputStream(path);
						DB.execute("update user set u_img=? where u_no=?", fis, toInt(DB.getOne("select u_no from user order by u_no desc")));
						
						var image = ImageIO.read(f);
						ImageIO.write(image, "jpg", new File("./Datafiles/회원사진/"+DB.getOne("select u_name from user order by u_no desc")+".jpg"));
						
						dispose();
					} catch ( IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
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
