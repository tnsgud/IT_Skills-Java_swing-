package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Login extends BaseFrame {
	JTextField txt[] = { hintField("ID", 25), hintPassField("PASSWORD", 25) };
	JLabel img, lbl[] = new JLabel[3];
	Main main;

	public static void main(String[] args) {
		new Main();
	}
	
	public Login(Main main) {
		super("로그인", 400, 350);

		this.main = main;

		add(lbl("LOGIN", 0, 35), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(cc = new JPanel(new FlowLayout(0)));

		for (int i = 0; i < txt.length; i++) {
			(i == 0 ? cn : cc).add(sz(txt[i], 280, 30));
			txt[i].setHorizontalAlignment(0);
		}
		cc.add(img = new JLabel(getIcon("./Datafiles/돋보기.jpg", 30, 30)));
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				img.setIcon(getIcon("./Datafiles/돋보기금지.png", 30, 30));
				((JPasswordField) txt[1]).setEchoChar('\0');
			}

			@Override
			public void mouseExited(MouseEvent e) {
				img.setIcon(getIcon("./Datafiles/돋보기.jpg", 30, 30));
				((JPasswordField) txt[1]).setEchoChar('●');
			}
		});

		s.add(btn("SIGN IN", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			var rs1 = getRows("select * from student where st_id=? and st_pw=?", txt[0].getText(), txt[1].getText());
			var rs2 = getRows("select * from teacher where t_id=? and t_pw=?", txt[0].getText(), txt[1].getText());

			if (rs1.isEmpty() && rs2.isEmpty()) {
				eMsg("회원정보가 일치하지 않습니다.");
				return;
			}

			if (!rs1.isEmpty()) {
				student = rs1.get(0);
				iMsg(student.get(1) + "학생으로 로그인되었습니다.");
				main.student();
			} else if (!rs2.isEmpty()) {
				teacher = rs2.get(0);
				iMsg(teacher.get(1) + "강사로 로그인되었습니다.");
				main.teacher();
			}

			dispose();
		}), "North");
		s.add(sc = new JPanel(new GridLayout(1, 0)));

		var cap = "회원가입,아이디찾기,비밀번호 찾기".split(",");
		for (int i = 0; i < cap.length; i++) {
			sc.add(lbl[i] = lbl(cap[i], toInt("2,0,4".split(",")[i])));
			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = ((JLabel) e.getSource()).getText();

					if (me.equals("회원가입")) {
						new Sign().addWindowListener(new Before(Login.this));
					} else if (me.equals("아이디찾기")) {
						new FindID().addWindowListener(new Before(Login.this));
					} else {
						new FindPW().addWindowListener(new Before(Login.this));
					}
				}
			});
		}

		((JPanel)getContentPane()).setBorder(new EmptyBorder(30, 30, 50, 30));
		cn.setBorder(new EmptyBorder(0, 20, 0, 0));
		cc.setBorder(new EmptyBorder(0,20,0,0));
		s.setBorder(new EmptyBorder(30, 0, 0, 0));
		sc.setBorder(new EmptyBorder(20, 0, 0, 0));
		((JPasswordField) txt[1]).setEchoChar('●');
		
		opeque((JPanel)getContentPane(), false);

		lbl[1].setBorder(new CompoundBorder(new MatteBorder(0, 2, 0, 2, Color.black), new EmptyBorder(0, 20, 0, 20)));

		setVisible(true);
	}
}
