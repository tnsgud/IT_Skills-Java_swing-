package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {

	JTextField txt;
	JPanel m, m1, m2;
	JComboBox<String> combo;
	JLabel img, name;
	JButton btnArray[] = { new JButton(), new JButton(), new JButton(), new JButton() };
	Timer timer;
	ArrayList<JPanel> imgList = new ArrayList<JPanel>();

	public Main() {
		super("Main", 400, 500);
		add(n = new JPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		add(c = new JPanel(new BorderLayout(5, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(crt_lbl("아르바이트", JLabel.CENTER, "HY헤드라인M", 0, 25));
		n.add(img = new JLabel());
		n.add(name = new JLabel());

		c.add(cn = new JPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		s.add(sn = new JPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		s.add(sz(sc = new JPanel(null), 0, 100));

		cn.add(crt_lbl("기업검색", JLabel.LEFT, Font.TYPE1_FONT, 20));
		cn.add(txt = new JTextField(15));
		cn.add(crt_evt_btn("검색", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("검색할 기업명을 입력하세요.");
				return;
			}

			var rs = getResults("SELECT * from company where c_name like ? order by c_no asc",
					"%" + txt.getText() + "%");
			if (rs.size() == 0) {
				eMsg("기업이 없습니다.");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			var row = rs.get(0);
			setValues("update company set c_search = c_search + 1 where c_no = ?", row.get(0));
			famous();

			new Company(row.get(0) + "").addWindowListener(new before(this));

		}));

		cc.add(m = new JPanel(new GridLayout(1, 0, 20, 5)));
		m.add(m1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		cc.add(crt_lbl("인기기업", JLabel.LEFT, Font.TYPE1_FONT, 20), "North");

		// 버튼

		for (int i = 0; i < 4; i++) {
			m2.add(btnArray[i]);
			btnArray[i].addActionListener(a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login(this).addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					logout();
				} else if (a.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("채용정보")) {
					new Jobs(null).addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("닫기")) {
					System.exit(0);
				}
			});
		}

		logout();

		// 인기기업...
		famous();

		sn.add(crt_lbl("지역", JLabel.CENTER, Font.TYPE1_FONT, 20));
		sn.add(combo = new JComboBox<String>(
				new DefaultComboBoxModel<String>("전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,충남,전북,전남,경북,경남,제주".split(","))));
		sz(combo, 120, 30);

		combo.addItemListener(i -> {
			if (i.getStateChange() == ItemEvent.SELECTED)
				animation();
		});

		// 초밥..
		animation();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		setVisible(true);
	}

	void logout() {
		String temp[] = "로그인,회원가입,닫기".split(",");
		img.setBorder(null);
		img.setIcon(null);
		name.setText("");
		btnArray[3].setVisible(false);
		for (int i = 0; i < temp.length; i++) {
			btnArray[i].setText(temp[i]);
		}
	}

	void login(Object pic) {
		String temp[] = "로그아웃,채용정보,마이페이지,닫기".split(",");
		btnArray[3].setVisible(true);
		img.setBorder(new LineBorder(Color.BLACK));
		img.setIcon(toIcon(pic, 30, 30));
		name.setText(uname + "님 환영합니다.");
		for (int i = 0; i < temp.length; i++) {
			btnArray[i].setText(temp[i]);
		}
	}

	void animation() {
		var local = combo.getSelectedItem().toString().equals("전체") ? "" : combo.getSelectedItem().toString();
		var rs = getResults("select c_name , c_img from company where left(c_address,2) like ?", "%" + local + "%");

		if (timer != null)
			timer.stop();

		if (rs.size() == 0) {
			eMsg("선택한 기업정보가 없습니다.");
			combo.setSelectedIndex(0);
			animation();
			return;
		}

		sc.removeAll();
		imgList.clear();

		for (int i = 0; i < rs.size(); i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(new JLabel(toIcon(rs.get(i).get(1), 100, 80)));
			temp.add(crt_lbl(rs.get(i).get(0) + "", JLabel.CENTER, Font.BOLD, 13), "South");
			sz(temp, 100, 100);
			sc.add(temp);
			temp.setBounds(i * 100, 0, 100, 100);
			imgList.add(temp);
			temp.setBorder(new LineBorder(Color.BLACK));
		}

		final int length = imgList.stream().mapToInt(a -> a.getWidth()).sum() - 100;

		timer = new Timer(1, a -> {
			for (int i = 0; i < imgList.size(); i++) {
				imgList.get(i).setBounds(imgList.get(i).getX() - 1, 0, 100, 100);
				if (imgList.get(i).getX() <= -100)
					imgList.get(i).setBounds(length, 0, 100, 100);

			}
			revalidate();
			repaint();
		});

		timer.start();
	}

	void famous() {
		m1.removeAll();
		m1.setLayout(new GridLayout(0, 1));
		var rs = getResults(
				"SELECT c_name,c_img,c_search FROM 2022지방_2.company order by company.c_search desc, c_no asc limit 5");
		for (int i = 0; i < rs.size(); i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(crt_lbl(i + 1 + "", JLabel.CENTER), 30, 0), "West");
			temp.add(crt_lbl(rs.get(i).get(0) + "", JLabel.LEFT));
			temp.add(sz(crt_lbl(rs.get(i).get(2) + "", JLabel.CENTER), 30, 0), "East");
			m1.add(temp);
		}
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		new Main();
	}

}
