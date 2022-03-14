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
import java.time.LocalDate;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

public class Sign extends BaseFrame {
	JLabel img;
	JPanel p1, p2;
	JComboBox domain, edu, addr;
	JTextField txt[] = new JTextField[4], mail, detail;
	JRadioButton rbtn[] = new JRadioButton[2];
	File f;

	public Sign() {
		super("회원가입", 550, 500);

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));

		cc.add(p1 = new JPanel(new BorderLayout()));
		cc.add(p2 = new JPanel(new GridLayout(0, 1, 10, 10)));

		var tmp1 = new JPanel(new GridLayout(0, 1, 10, 10));
		var tmp2 = new JPanel(new BorderLayout());

		p1.add(tmp1);
		p1.add(tmp2, "East");

		var cap1 = "이름,아이디,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap1.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap1[i], 2, 12), 80, 20));
			p.add(txt[i] = new JTextField(15));
			tmp1.add(p);
		}
		tmp2.add(sz(img = lbl("", 0), 180, 180));

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap2[i], 2, 12), 80, 20));
			if (i == 0) {
				p.add(mail = new JTextField(7));
				p.add(lbl("@", 2));
				p.add(sz(domain = new JComboBox<>(
						Stream.of("naver,outlook,daum,gmail,nate,kebi,yahoo,korea,empal".split(","))
								.map(a -> a + ".com").toArray(String[]::new)),
						105, 25));
				domain.addItem("hanmail.net");
			} else if (i == 1) {
				var tmp = "남,여".split(",");
				var bg = new ButtonGroup();
				for (int j = 0; j < tmp.length; j++) {
					p.add(rbtn[j] = new JRadioButton(tmp[j]));
					bg.add(rbtn[j]);
				}
				rbtn[0].setSelected(true);
			} else if (i == 2) {
				p.add(sz(edu = new JComboBox<>("대학교 졸업,고등학교 졸업,중학교 졸업".split(",")), 100, 20));
				edu.setSelectedIndex(-1);
			} else {
				p.add(sz(addr = new JComboBox<>(",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",")), 100,
						20));
				p.add(detail = new JTextField(15));
				detail.setVisible(false);
			}
			p2.add(p);
		}

		addr.addItemListener(i -> {
			detail.setVisible(!i.getItem().toString().equals(""));
			if (detail.isVisible())
				detail.requestFocus();
			revalidate();
		});

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
					if (jfc.showOpenDialog(Sign.this) == JFileChooser.APPROVE_OPTION) {
						f = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath())
								.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
					}
				}
			};
		});

		s.add(btn("가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || detail.getText().isEmpty() || mail.getText().isEmpty()
						|| edu.getSelectedIndex() == -1 || addr.getSelectedIndex() == -1 || f == null) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			String pw = txt[2].getText(), id = txt[1].getText(), date = txt[3].getText();

			if (!rs("select * from user where u_id=?", id).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
			}

			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			try {
				if (LocalDate.parse(date).isAfter(LocalDate.now())) {
					eMsg("생년월일 형식이 맞지 않습니다.");
					return;
				}
			} catch (Exception e) {
				eMsg("생년월일 형식이 맞지 않습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			try {
				execute("insert into user values (0, ?,?,?,?,?,?,?,?,?)", txt[0].getText(), id, pw, date,
						mail.getText() + "@" + domain.getSelectedItem(), rbtn[0].isSelected() ? 1 : 2,
						edu.getSelectedIndex(), addr.getSelectedItem() + " " + detail.getText(),
						new FileInputStream(f));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		img.setBorder(new LineBorder(Color.black));
		c.setBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
