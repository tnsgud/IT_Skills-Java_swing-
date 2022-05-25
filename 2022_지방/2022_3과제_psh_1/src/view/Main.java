package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import view.BaseFrame.Before;

public class Main extends BaseFrame {
	JLabel img, name;
	JTextField txt;
	JPanel m, m1, m2;
	JButton btn[] = new JButton[4];
	JComboBox<String> combo;
	Timer timer;
	ArrayList<JPanel> imgList = new ArrayList<>();

	public Main() {
		super("Main", 400, 500);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout(1)), "North");
		add(c = new JPanel(new BorderLayout(5, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblH("아르바이트", 0, 0, 25));
		n.add(img = lbl("", 0));
		n.add(name = lbl("", 0));

		c.add(cn = new JPanel(new FlowLayout(1)), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		s.add(sn = new JPanel(new FlowLayout(2)), "North");
		s.add(sz(sc = new JPanel(null), 0, 100));

		cn.add(lbl("기업검색", 0, 20));
		cn.add(txt = new JTextField(15));
		cn.add(btn("검색", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("검색할 기업명을 입력하세요.");
				return;
			}

			var rs = getResults("select * from company where c_name like ? order by  c_no asc",
					"%" + txt.getText() + "%");
			if (rs.size() == 0) {
				eMsg("검색한 기없이 없습니다.");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			try {
				execute("update company set c_search = c_search+1 where c_no like ?", rs.get(0).get(0));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			famous();

			new Company(rs.get(0).get(0)+"").addWindowListener(new Before(this));
		}));

		cc.add(m = new JPanel(new GridLayout(1, 0, 20, 5)));
		m.add(m1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		cc.add(lbl("인기기업", 2, 20), "North");

		for (int i = 0; i < btn.length; i++) {
			m2.add(btn[i] = btn("", a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login(this).addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					logout();
				} else if (a.getActionCommand().equals("채용정보")) {
					new Jobs(null).addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(this));
				} else {
					System.exit(0);
				}
			}));
		}

		img.setBorder(new LineBorder(Color.black));

		logout();

		famous();

		sn.add(lbl("지역", 0, 20));
		sn.add(sz(
				combo = new JComboBox<>(
						new DefaultComboBoxModel<>(local)),
				120, 30));

		combo.addActionListener(a -> {
			animation();
		});

		animation();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	private void animation() {
		var loc = combo.getSelectedIndex() == 0 ? "" : combo.getSelectedItem() + "";
		var rs = getResults("select c_name, c_img from company where left(c_address, 2) like ?", "%" + loc + "%");

		if (timer != null) {
			timer.stop();
		}

		if (rs.size() == 0) {
			eMsg("검색한 기업이 없습니다.");
			txt.setText("");
			combo.setSelectedIndex(0);
			animation();
			return;
		}

		sc.removeAll();
		imgList.clear();

		for (int i = 0; i < rs.size(); i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(new JLabel(img(rs.get(i).get(1), 100, 80)));
			tmp.add(lbl(rs.get(i).get(0) + "", 0, 13), "South");
			sc.add(sz(tmp, 100, 100));
			tmp.setBounds(i * 100, 0, 100, 100);
			imgList.add(tmp);
			tmp.setBorder(new LineBorder(Color.black));
		}

		final var length = imgList.stream().mapToInt(a -> a.getWidth()).sum() - 100;

		timer = new Timer(1, a -> {
			imgList.forEach(l -> {
				l.setBounds(l.getLocation().x - 1, 0, 100, 100);
				if (l.getLocation().x <= -100) {
					l.setBounds(length, 0, 100, 100);
				}
			});
		});

		timer.start();
	}

	private void famous() {
		m1.removeAll();
		m1.setLayout(new GridLayout(0, 1));
		var rs = getResults(
				"select c_name,c_img,c_search from company order by company.c_search desc, c_no asc limit 5");
		for (int i = 0; i < rs.size(); i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl(i + 1 + "", 0), 30, 0), "West");
			tmp.add(lbl(rs.get(i).get(0) + "", 2));
			tmp.add(sz(lbl(rs.get(i).get(2) + "", 0), 30, 0), "East");
			m1.add(tmp);
		}

		repaint();
		revalidate();
	}

	private void logout() {
		var tmp = "로그인,회원가입,닫기".split(",");
		Stream.of(new JComponent[] { name, img, btn[3] }).forEach(c -> c.setVisible(false));
		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	void login(Object b) {
		var tmp = "로그아웃,채용정보,마이페이지,닫기".split(",");
		Stream.of(new JComponent[] { name, img, btn[3] }).forEach(c -> c.setVisible(true));
		img.setIcon(img(b, 30, 30));
		name.setText(uname + "님 환영합니다.");
		iMsg(name.getText()
				);
		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
