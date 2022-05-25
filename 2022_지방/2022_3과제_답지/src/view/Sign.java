package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
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

	JPanel p1, p2;

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15) };
	JRadioButton male, female;
	JComboBox<String> edu, addr, email2;
	JTextField email1 = new JTextField(10), detail = new JTextField(20);
	JLabel img;
	File f;

	public Sign() {
		super("회원가입", 550, 500);
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		s.add(sz(crt_evt_btn("가입", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty() || img.getIcon() == null || email1.getText().isEmpty()
						|| detail.getText().isEmpty() || edu.getSelectedIndex() == -1 || addr.getSelectedIndex() == -1
						|| email2.getSelectedIndex() == -1) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			String pw = txt[2].getText(), date = txt[3].getText(), id = txt[1].getText();
			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			if (getResults("select * from user where u_id = ?", id).size() != 0) {
				eMsg("이미 존재하는 아이디 입니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			try {
				if (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now())) {
					eMsg("생년월일 형식이 맞지 않습니다.");
					txt[3].setText("");
					txt[3].requestFocus();
					return;
				}

			} catch (Exception e1) {
				eMsg("생년월일 형식이 맞지 않습니다.");
				txt[3].setText("");
				txt[3].requestFocus();
				return;
			}

			try {
				setValues("insert user values(0,?,?,?,?,?,?,?,?,?)", txt[0].getText(), id, pw, date,
						email1.getText() + "@" + email2.getSelectedItem(), (male.isSelected() ? 1 : 2),
						edu.getSelectedIndex(), addr.getSelectedItem() + detail.getText(), new FileInputStream(f));
				var no = getResults("select u_no from user where u_id = ?", id).get(0).get(0);
				Files.copy(f.toPath(), new File("./datafiles/회원사진/" + no + ".jpg").toPath());
				iMsg("회원가입이 완료되었습니다.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			dispose();

		}), 120, 30));

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));

		cc.add(p1 = new JPanel(new BorderLayout()));
		cc.add(p2 = new JPanel(new GridLayout(0, 1, 10, 10)));

		var temp1 = new JPanel(new GridLayout(0, 1, 10, 10));
		var temp2 = new JPanel(new BorderLayout());

		p1.add(temp1);
		p1.add(sz(temp2, 200, 0), "East");

		var cap1 = "이름,아이디,비밀번호,생년월일".split(",");

		for (int i = 0; i < cap1.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp.add(sz(crt_lbl(cap1[i], JLabel.LEFT), 80, 30));
			tmp.add(txt[i]);
			temp1.add(tmp);
		}

		temp2.add(img = new JLabel());

		var cap2 = "이메일,성별,최종학력,주소".split(",");

		for (int i = 0; i < 4; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
			tmp.add(sz(crt_lbl(cap2[i], JLabel.LEFT), 80, 30));

			if (i == 0) {
				tmp.add(email1);
				tmp.add(crt_lbl("@", JLabel.CENTER));
				tmp.add(email2 = new JComboBox<String>(new DefaultComboBoxModel<String>(
						"naver.com,outlook.com,daum.com,gmail.com,nate.com,kebi.com,yahoo.com,korea.com,empal.com,hanmail.net"
								.split(","))));
			} else if (i == 1) {
				tmp.add(male = new JRadioButton("남"));
				tmp.add(female = new JRadioButton("여"));
				var bg = new ButtonGroup();
				bg.add(male);
				bg.add(female);
				male.setSelected(true);
			} else if (i == 2) {
				tmp.add(sz(edu = new JComboBox<String>("대학교 졸업,고등학교 졸업,중학교 졸업".split(",")), 120, 30));
			} else if (i == 3) {
				tmp.add(sz(addr = new JComboBox<String>(new DefaultComboBoxModel<String>(
						",서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(","))), 120, 30));
				tmp.add(sz(detail, 200, 30));
			}

			p2.add(tmp);
		}
		// evt
		addr.addItemListener(a -> detail.setEnabled(!a.getItem().toString().isEmpty()));
		img.addMouseListener(new MouseAdapter() {
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
					img.setIcon(getIcon(f.getPath(), 180, 180));
				}
			};
		});

		detail.setEnabled(false);
		edu.setSelectedIndex(-1);

		img.setBorder(new LineBorder(Color.BLACK));
		temp2.setBorder(new EmptyBorder(5, 20, 5, 5));
		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "회원가입"));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
