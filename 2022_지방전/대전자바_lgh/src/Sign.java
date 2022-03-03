import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Sign extends BaseFrame {

	JLabel img;
	JTextField txt[] = { new JTextField(20), new JTextField(), new JPasswordField() };

	String str[] = "이름 :,ID :,PW :".split(",");
	String path = "";
	boolean bool;

	public Sign() {
		super("회원가입", 450, 250);

		this.add(w = new JPanel(new BorderLayout()), "West");
		this.add(c = new JPanel(new BorderLayout()));

		var c_c = new JPanel(new BorderLayout());
		var c_s = new JPanel(new FlowLayout(2));

		var grid1 = new JPanel(new GridLayout(0, 1, 10, 30));
		var grid2 = new JPanel(new GridLayout(0, 1, 10, 30));

		w.add(img = new JLabel());
		w.add(btn("사진 등록", e -> {
			var chooser = new JFileChooser("Datafiles/회원사진/");
			chooser.setMultiSelectionEnabled(false);

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile().getPath();
				img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(img.getWidth(),
						img.getHeight(), 4)));
				bool = true;
			}
		}), "South");
		c.add(c_c);
		c.add(c_s, "South");
		c_c.add(grid1);
		c_c.add(grid2, "East");

		for (int i = 0; i < str.length; i++) {
			grid1.add(new JLabel(str[i]));
			grid2.add(txt[i]);
		}
		c_s.add(btn("회원가입", e -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty() || txt[2].getText().isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			if (txt[1].getText().equals(getone("select u_id from user where u_id = '" + txt[1].getText() + "'"))) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (!(txt[2].getText().matches(".*[0-9].*") && txt[2].getText().matches(".*[a-zA-Z].*")
					&& txt[2].getText().matches(".*[\\W].*")) || txt[2].getText().length() < 4) {
				eMsg("비밀번호를 확인해주세요.");
				return;
			}

			if (!bool) {
				eMsg("사진을 등록해주세요.");
				return;
			}

			try {
				var ps = con.prepareStatement("insert into user values(0,?,?,?,?)");
				for (int i = 0; i < str.length; i++) {
					ps.setObject(i + 1, txt[i].getText());
				}

				File f = new File(path);
				ps.setObject(4, new FileInputStream(f));
				ps.execute();

				ImageIO.write(ImageIO.read(f), ".jpg",
						new File("Datafiles/회원사진/" + getone("select max(u_no)+1 from user") + ".jpg"));

				iMsg("회원가입이 완료되었습니다.");
				dispose();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}));

		c_s.add(btn("취소", e -> dispose()));

		sz(img, 150, 150);
		img.setBorder(new LineBorder(Color.BLACK));
		grid1.setBorder(new EmptyBorder(0, 20, 0, 0));
		this.setVisible(true);
	}
}
