import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JLabel title;
	JTextField txt[] = { new JTextField(), new JPasswordField() };
	JCheckBox chk;

	Preferences pre = Preferences.userNodeForPackage(BaseFrame.class);

	String str[] = "ID,PW".split(",");

	public Login() {
		super("로그인", 400, 250);

		this.add(n = new JPanel(new GridLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new BorderLayout()), "South");

		var c_w = new JPanel(new GridLayout(0, 1, 10, 20));
		var c_c = new JPanel(new GridLayout(0, 1, 10, 20));
		var c_e = new JPanel(new GridLayout());
		var s_w = new JPanel();
		var s_e = new JPanel();

		n.add(title = new JLabel("Orange Ticket", 0));
		c.add(c_w, "West");
		c.add(c_c);
		c.add(c_e, "East");
		s.add(s_w, "West");
		s.add(s_e, "East");

		for (int i = 0; i < str.length; i++) {
			c_w.add(new JLabel(str[i]));
			c_c.add(txt[i]);
		}
		c_e.add(btn("로그인", e -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where u_id = '" + txt[0].getText() + "' and u_pw = '"
						+ txt[1].getText() + "'");
				if (!rs.next()) {
					eMsg("ID 또는 PW가 일치하지 않습니다.");
					return;
				} else {
					uno = rs.getString(1);
					Main.img.setVisible(true);
					Main.img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit()
							.createImage(rs.getBlob("u_img").getBinaryStream().readAllBytes())
							.getScaledInstance(30, 30, 4)));
					Main.lbl[3].setText("LOGOUT");
					if (chk.isSelected()) {
						pre.put("id", txt[0].getText());
					}
					iMsg(rs.getString("u_name") + "님 환영합니다.");
					this.dispose();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		s_w.add(chk = new JCheckBox("아이디 저장"));
		s_e.add(btn("회원가입", e -> {
			new Sign().addWindowListener(new Before(Login.this));
		}));

		chk.addActionListener(e -> {
			pre.remove("id");
		});

		if (!pre.get("id", "").equals("")) {
			chk.setSelected(true);
			txt[0].setText(pre.get("id", ""));
		}

		title.setFont(new Font("HY헤드라인M", Font.BOLD + Font.ITALIC, 30));
		c.setBorder(new EmptyBorder(20, 5, 20, 5));
		this.setVisible(true);
	}
}
