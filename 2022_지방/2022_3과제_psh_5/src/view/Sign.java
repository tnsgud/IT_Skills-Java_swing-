package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Sign extends BaseFrame {
	File f;
	JTextField txt[] = new JTextField[4], mail = new JTextField(7), detail = new JTextField(10);
	JComboBox domain = new JComboBox<>(
			" naver.com, outlook.com, daum.com, gmail.com, nate.com, kebi.com, yahoo.com, korea.com, empal.com, hanmail.net"
					.split(", ")),
			edu = new JComboBox<>("대학교 졸업,고등학교 졸업,중학교 졸업".split(",")),
			addr = new JComboBox(" , 서울, 부산, 대구, 인천, 광주, 대전, 울산, 세종, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주".split(", "));
	JLabel img = new JLabel();
	JRadioButton rbtn[] = new JRadioButton[2];

	public Sign() {
		super("회원가입", 600, 400);

		var m = new JPanel(new GridLayout(0, 1));

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(4)), "South");

		c.add(cn = new JPanel(new BorderLayout()));
		c.add(cc = new JPanel(new GridLayout(0, 1)));
		cn.add(m);
		cn.add(sz(img, 180, 180), "East");

		var cap = "이름,아이디,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 60, 20));
			p.add(txt[i] = new JTextField(15));
			m.add(p);
		}

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap2[i], 2), 60, 20));
			if (i == 0) {
				p.add(mail);
				p.add(lbl("@", 0));
				p.add(domain);
			} else if (i == 1) {
				var b = new ButtonGroup();
				var t = "남,여".split(",");
				for (int j = 0; j < t.length; j++) {
					p.add(rbtn[j] = new JRadioButton(t[j]));
					b.add(rbtn[i]);
				}
				rbtn[0].setSelected(true);
			} else if (i == 2) {
				p.add(edu);
			} else {
				p.add(sz(addr, 105, 25));
				p.add(detail);
				detail.setEnabled(false);
				addr.addActionListener(a -> detail.setEnabled(addr.getSelectedIndex() != 0));
			}
			cc.add(p);
		}

		s.add(btn("가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || f == null || mail.getText().isEmpty() || detail.getText().isEmpty()
						|| addr.getSelectedIndex() == 0) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (!rs("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			var pw = txt[2].getText();
			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			try {
				if (LocalDate.parse(txt[3].getText()).isAfter(LocalDate.now())) {
					eMsg("생년월일 형식이 맞지 않습니다.");
					txt[3].setText("");
					txt[3].requestFocus();
					return;
				}
			} catch (Exception e) {
				eMsg("생년월일 형식이 맞지 않습니다.");
				txt[3].setText("");
				txt[3].requestFocus();
				return;
			}

			execute("insert user values(0,?,?,?,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
					txt[3].getText(), mail.getText() + "@" + domain.getSelectedItem(), rbtn[0].isSelected() ? 1 : 2,
					edu.getSelectedIndex(), addr.getSelectedItem() + " " + detail.getText());
			try {
				Files.copy(f.toPath(),
						new File("./datafiles/회워사진/"
								+ rs("select u_no from user where u_id=?", txt[0].getText()).get(0).get(0) + ".jpg")
										.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {

			}
			iMsg("회원가입이 완료되었습니다.");
		}));

		c.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"),
				new EmptyBorder(10, 10, 10, 10)));
		img.setBorder(new LineBorder(Color.black));

		img.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				var jfc = new JFileChooser("./datafiles/회원사진");
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					img.setIcon(img("회원사진/" + f.getName(), 180, 180));
				}
			};
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
