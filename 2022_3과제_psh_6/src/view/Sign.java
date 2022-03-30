package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

public class Sign extends BaseFrame {
	JLabel img;
	JComboBox domain, edu, addr;
	JTextField txt[] = new JTextField[4], mail, detail;
	JRadioButton rbtn[] = new JRadioButton[2];
	File f;

	public Sign() {
		super("회원가입", 500, 500);
		var tmp = new JPanel(new GridLayout(0, 1));

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cn = new JPanel(new BorderLayout()));
		c.add(cc = new JPanel(new GridLayout(0, 1)));
		cn.add(tmp);
		cn.add(sz(img = new JLabel(), 150, 0), "East");

		var cap = "이름,아이디,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2, 12), 60, 20));
			p.add(txt[i] = new JTextField(15));
			tmp.add(p);
		}

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap2[i], 2, 12), 60, 20));
			if (i == 0) {
				p.add(mail = new JTextField(10));
				p.add(lbl("@", 0));
				p.add(domain = new JComboBox<>(
						"naver.com, outlook.com, daum.com, gmail.com, nate.com, kebi.com, yahoo.com, korea.com, empal.com, hanmail.net"
								.split(", ")));
			} else if (i == 1) {
				var b = new ButtonGroup();
				var t = "남,여".split(",");
				for (int j = 0; j < t.length; j++) {
					rbtn[j] = new JRadioButton(t[j]);
					b.add(rbtn[j]);
					p.add(rbtn[j]);
				}
				rbtn[0].setSelected(true);
			} else if (i == 2) {
				p.add(edu = new JComboBox<>(", 대학교 졸업, 고등학교 졸업, 중학교 졸업".split(", ")));
			} else {
				p.add(sz(addr = new JComboBox<>(" ,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",")), 105,
						25));
				p.add(detail = new JTextField(15));
				addr.addActionListener(a -> detail.setEnabled(addr.getSelectedIndex() != 0));
				detail.setEnabled(false);
			}
			cc.add(p);
		}

		s.add(btn("가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || mail.getText().isEmpty() || detail.getText().isEmpty()
						|| addr.getSelectedIndex() == 0 || edu.getSelectedIndex() == 0
						|| f == null) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			String id = txt[1].getText(), pw = txt[2].getText(),
					email = mail.getText() + "@" + domain.getSelectedItem();

			if (!rs("select * from user where u_id=?", id).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[!@#$].*") && pw.matches(".*[a-zA-Z].*"))
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

			iMsg("회원가입이 완료되었습니다.");
			try {
				execute("insert user values(0,?,?,?,?,?,?,?,?,?)", txt[0].getText(), id, pw, txt[3].getText(), email,
						rbtn[0].isSelected() ? 1 : 2, edu.getSelectedIndex(),
						addr.getSelectedItem() + " " + detail.getText(), new FileInputStream(f));
				Files.copy(f.toPath(), new File("./datafiles/회원사진/"+rs("select * from user order by u_no desc").get(0).get(0)+".jpg").toPath());
				
				dispose();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		img.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("./datafiles/회원사진");
					jfc.setFileFilter(new FileFilter() {
						@Override
						public String getDescription() {
							return "JPG Images";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith("jpg");
						}
					});
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						f = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath())
								.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
					}
				}
			};
		});

		c.setBorder(
				new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"), new EmptyBorder(5, 5, 5, 5)));
		img.setBorder(new LineBorder(Color.black));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
