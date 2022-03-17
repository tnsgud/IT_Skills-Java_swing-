package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Sign extends BaseFrame {
	JComboBox domain, edu, addr;
	JTextField txt[] = new JTextField[4], mail, detail;
	JRadioButton rbtn[] = new JRadioButton[2];
	JLabel img;
	File f;

	public Sign() {
		super("회원가입", 500, 500);

		setLayout(new BorderLayout(5, 5));

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var tmp = new JPanel(new GridLayout(0, 1));
		cn.add(tmp);
		cn.add(sz(img = lbl("", 0), 150, 150), "East");

		var cap = "이름,아이디,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(15));
			tmp.add(p);
		}

		var cap2 = "이메일,성별,최종학력,주소".split(",");
		for (int i = 0; i < cap2.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap2[i], 2), 80, 20));
			if (i == 0) {
				var t = new JPanel(new FlowLayout(0, 0, 0));
				t.add(mail = new JTextField(7));
				t.add(lbl("@", 0));
				t.add(domain = new JComboBox<>(
						"naver.com, outlook.com, daum.com, gmail.com, nate.com, kebi.com, yahoo.com, korea.com, empal.com, hanmail.net"
								.split(", ")));
				p.add(t);
			} else if (i == 1) {
				var t = new JPanel(new FlowLayout(0, 0, 0));
				var bg = new ButtonGroup();
				var rcap = "남,여".split(",");
				for (int j = 0; j < rcap.length; j++) {
					t.add(rbtn[j] = new JRadioButton(rcap[j]));
					bg.add(rbtn[j]);
				}
				p.add(t);
			} else if (i == 2) {
				p.add(edu = new JComboBox<>(graduate));
				edu.setSelectedIndex(-1);
			} else {
				var t = new JPanel(new FlowLayout(0));
				t.add(sz(addr = new JComboBox<>(",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(",")), 105,
						25));
				t.add(detail = new JTextField(15));
				p.add(t);
				detail.setVisible(false);
				addr.setSelectedIndex(-1);
				addr.addActionListener(a -> {
					detail.setVisible(addr.getSelectedIndex() != 0);
					repaint();
					revalidate();
				});
			}
			cc.add(p);
		}

		s.add(btn("가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || f == null || detail.getText().isEmpty() || edu.getSelectedIndex() == -1
						|| addr.getSelectedIndex() == -1 || mail.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			String id = txt[1].getText(), pw = txt[2].getText(), date = txt[3].getText();

			if (!rs("select * from user whee u_id=?", id).isEmpty()) {
				eMsg("이미 존재하는 아아디입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (!(pw.matches(".*[a-zA-Z].*") && pw.matches(".*[0-9].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			try {
				if (LocalDate.parse(date).isAfter(LocalDate.now())) {
					eMsg("생년월일 형시이 맞지 않습니다.");
					return;
				}
			} catch (Exception e) {
				eMsg("생년월일 형시이 맞지 않습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			try {
				execute("insert into user values(0, ?, ?, ?, ?, ?, ?, ?, ?, ?)", txt[0].getText(), id, pw, date,
						mail.getText() + "@" + domain.getSelectedItem(), rbtn[0].isSelected() ? 1 : 2,
						edu.getSelectedIndex(), addr.getSelectedItem() + " " + detail.getText(),
						new FileInputStream(f));
				Files.copy(f.toPath(),
						new File("./datafiles/회원사진/" + rs("select * from user where u_id=?", id).get(0).get(0) + ".jpg")
								.toPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			dispose();
		}));

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("./dtatfiles/회원사진/");
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						f = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath())
								.getScaledInstance(130, 130, Image.SCALE_SMOOTH)));
					}
				}
			}
		});

		img.setBorder(new LineBorder(Color.black));
		c.setBorder(
				new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "회원가입"), new EmptyBorder(5, 5, 5, 5)));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
