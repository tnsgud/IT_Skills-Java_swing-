package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	JLabel img, name;
	JTextField txt;
	JButton btn[] = new JButton[4];
	JPanel m, m1, m2;
	JComboBox<String> com;
	ArrayList<JPanel> imgList = new ArrayList<>();
	Timer timer;

	public Main() {
		super("Main", 400, 500);

		ui();
		com.addActionListener(a -> animation());

		setVisible(true);
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout()), "North");
		add(c = new JPanel(new BorderLayout(5, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblH("아르바이트", 0, 0, 25));
		n.add(sz(img = lbl("", 0), 30, 30));
		n.add(name = lbl("", 0));

		c.add(cn = new JPanel(new FlowLayout()), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		s.add(sn = new JPanel(new FlowLayout(0)), "North");
		s.add(sz(sc = new JPanel(null), 0, 100));

		cn.add(lbl("기업검색", 0, 15));
		cn.add(txt = new JTextField(15));
		cn.add(btn("검색", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("검색할 기업명을 입력하세요.");
				return;
			}

			var rs = rs("select * from company where c_name like ? order by c_no asc", "%" + txt.getText() + "%");
			if (rs.isEmpty()) {
				eMsg("검색한 기업이 없습니다.");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			execute("update company set c_search = c_search+1 where c_no=?", rs.get(0).get(0));
			famous();

			new Company(rs.get(0).get(0) + "").addWindowListener(new Before(this));
		}));

		cc.add(lbl("인기기업", 2, 15), "North");
		cc.add(m = new JPanel(new GridLayout(1, 0, 5, 5)));
		m.add(m1 = new JPanel(new GridLayout(0, 1)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

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

		sn.add(lbl("지역", 0, 20));
		sn.add(sz(com = new JComboBox<>(local), 120, 30));

		img.setBorder(new LineBorder(Color.black));

		logout();
		famous();
		animation();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	private void animation() {
		var loc = com.getSelectedIndex() == 0 ? "" : com.getSelectedItem() + "";
		var rs = rs("select c_name, c_img from company where left(c_address, 2) like ?", "%" + loc + "%");

		if (timer != null) {
			timer.stop();
		}

		if (rs.isEmpty()) {
			eMsg("검색한 결과가 없습니다.");
			txt.setText("");
			com.setSelectedIndex(0);
			return;
		}

		sc.removeAll();
		imgList.clear();

		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img(r.get(1), 100, 80)));
			p.add(lbl(r.get(0) + "", 0, 12), "South");
			p.setBorder(new LineBorder(Color.black));
			sc.add(p).setBounds(rs.indexOf(r) * 100, 0, 100, 100);
			imgList.add(p);
		}

		final var leng = imgList.stream().mapToInt(a -> a.getWidth()).sum() - 100;

		timer = new Timer(1, a -> {
			imgList.forEach(i -> {
				i.setLocation(i.getLocation().x - 1, 0);
				if (i.getLocation().x <= -100) {
					i.setLocation(leng, 0);
				}
			});
		});

		repaint();
		revalidate();

		timer.start();
	}

	private void famous() {
		m1.removeAll();
		var rs = rs("select c_name, c_search from company order by c_search desc, c_no asc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(rs.indexOf(r) + 1 + "", 2, 12), 30, 0), "West");
			p.add(lbl(r.get(0) + "", 2, 12));
			p.add(sz(lbl(r.get(1) + "", 2, 12), 30, 0), "East");
			m1.add(p);
		}

		m1.repaint();
		m1.revalidate();
	}

	private void logout() {
		var tmp = "로그인,회원가입,닫기".split(",");
		Stream.of(new JComponent[] { img, name, btn[3] }).forEach(c -> c.setVisible(false));
		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	void login(Object b) {
		var tmp = "로그아웃,채용정보,마이페이지,닫기".split(",");
		Stream.of(new JComponent[] { img, name, btn[3] }).forEach(a -> a.setVisible(true));
		img.setIcon(img(b, 30, 30));
		name.setText(uname + "님 환영합니다.");
		iMsg(name.getText());

		for (int i = 0; i < tmp.length; i++) {
			btn[i].setText(tmp[i]);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
