package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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
import javax.swing.filechooser.FileFilter;

public class Sign extends BaseFrame {
	JPanel p1, p2;
	JComboBox<String> domain, edu, addr;
	JTextField txt[] = new JTextField[4], email, detail;
	JRadioButton male, female;
	JLabel img;
	File f;

	public Sign() {
		super("회원가입", 550, 500);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		addr.addItemListener(i -> detail.setEnabled(!i.getItem().toString().isEmpty()));
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
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
					img.setIcon(img("회원사진/" + f.getName(), 180, 180));
				}
			};
		});

	}

	private void ui() {
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		cc.add(p1 = new JPanel(new BorderLayout()));
		cc.add(p2 = new JPanel(new GridLayout(0, 1, 10, 10)));

		var tmp1 = new JPanel(new GridLayout(0, 1, 10, 10));
		var tmp2 = new JPanel(new BorderLayout());

		p1.add(tmp1);
		p1.add(tmp2, "East");

		var cap1 = "이름,아이디,비밀번호,생년월일,".split(",");
		for (int i = 0; i < cap1.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			tmp.add(sz(lbl(cap1[i], 2), 80, 20));
			tmp.add(txt[i] = new JTextField(15));
			tmp1.add(tmp);
		}

		tmp2.add(sz(img = new JLabel(), 180, 180));
		img.setBorder(new LineBorder(Color.black));

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp.add(sz(lbl(cap2[i], 2), 80, 20));
			if (i == 0) {
				tmp.add(email = new JTextField(7));
				tmp.add(lbl("@", 2));
				tmp.add(domain = new JComboBox<>(
						Stream.of("naver,outlook,daum,gmail,nate,kebi,yahoo,korea,empal".split(","))
								.map(a -> a + ".com").toArray(String[]::new)));
				domain.addItem("hanmail.net");
			} else if (i == 1) {
				tmp.add(male = new JRadioButton("남"));
				tmp.add(female = new JRadioButton("여"));
				var bg = new ButtonGroup();
				bg.add(male);
				bg.add(female);
				male.setSelected(true);
			} else if (i == 2) {
				tmp.add(edu = new JComboBox<>("대학교 졸업,고등학교 졸업,중학교 졸업".split(",")));
				edu.setSelectedIndex(-1);
			} else {
				tmp.add(sz(addr = new JComboBox<>(",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,전북,전남,경북,경남,제주".split(",")), 105,
						25));
				tmp.add(detail = new JTextField(15));
				addr.setSelectedIndex(-1);
				detail.setEnabled(false);
			}
			p2.add(tmp);
		}

		s.add(btn("가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || email.getText().isEmpty() || detail.getText().isEmpty()
						|| img.getIcon() == null || addr.getSelectedIndex() == -1 || edu.getSelectedIndex() == -1) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (!getOne("select u_id from user where u_id=?", txt[1].getText()).isEmpty()) {
				eMsg("이미 존재하는 아이디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			String pw = txt[2].getText(), id = txt[1].getText(), date = txt[3].getText();
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
				}
			} catch (Exception e) {
				eMsg("생년월일 형식이 맞지 않습니다.");
				txt[3].setText("");
				txt[3].requestFocus();
			}

			try {
				execute("insert user values(0,?,?,?,?,?,?,?,?,?)", txt[0].getText(), id, pw, date,
						email.getText() + "@" + domain.getSelectedItem(), male.isSelected() ? 1 : 2,
						edu.getSelectedItem(), addr.getToolTipText() + " " + detail.getText(), new FileInputStream(f));
				var no = getOne("select u_no from user where u_id=?", id);
				Files.copy(f.toPath(), new File("./datafiles/회원사진/" + no + ".jpg").toPath());
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iMsg("회원가입이 완료되었습니다.");

			dispose();
		}));

		c.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"),
				new EmptyBorder(10, 10, 10, 10)));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		new Sign();
	}
}
