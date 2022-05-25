package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	static JButton btn[] = new JButton[5];
	static JLabel img, name;
	ArrayList<JPanel> items = new ArrayList<>();
	JComboBox com = new JComboBox<>(local);
	JTextField txt;
	JPanel m, m1, m2;
	Timer timer;

	public Main() {
		super("Main", 350, 500);

		add(n = new JPanel(), "North");

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblH("아르바이트", 0, 0, 25));
		n.add(sz(img = lbl("", 0), 30, 30));
		n.add(name = lbl("", 0));

		c.add(cn = new JPanel(new FlowLayout(1)), "North");
		c.add(m = new JPanel(new GridLayout(1, 0)));

		cn.add(lbl("기업검색", 2));
		cn.add(txt = new JTextField(10));
		cn.add(btn("검색", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("검색할 기업명을 입력하세요.");
				return;
			}

			var rs = rs("select * from company where c_name like ?", "%" + txt.getText() + "%");
			if (rs.isEmpty()) {
				eMsg("검색한 기업이 없습니다.");
				txt.setText("");
				txt.requestFocus();
				return;
			}

			cno = toInt(rs.get(0).get(0));
			execute("update company set c_search=c_search+1 where c_no=?", cno);

			load();
			new Company().addWindowListener(new Before(this));
		}));

		cc.add(lbl("인기기업", 2), "North");
		cc.add(m = new JPanel(new GridLayout(1, 0, 5, 5)));
		m.add(m1 = new JPanel(new GridLayout(0, 1)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		famous();

		for (int i = 0; i < btn.length; i++) {
			m2.add(btn[i] = btn("", a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					logout();
				} else if (a.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("채용정보")) {
					new Jobs().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(this));
				} else {
					setDefaultCloseOperation(3);
				}
			}));
		}

		s.add(sz(sc = new JPanel(null), 350, 150));

		sn.add(lbl("지역", 0));
		sn.add(com);

		com.addActionListener(a -> load());

		img.setBorder(new LineBorder(Color.black));
		img.setVisible(false);

		logout();

		load();

		timer = new Timer(1, a -> {
			items.forEach(p -> {
				p.setLocation(p.getLocation().x - 1, p.getLocation().y);
				if (p.getLocation().x <= -100) {
					p.setLocation(items.size() * 100 - 100, p.getLocation().y);
				}
			});
		});

		timer.start();

		setVisible(true);
	}

	private void load() {
		items.clear();
		sc.removeAll();
		var rs = rs("SELECT * FROM company where c_address like ?",
				com.getSelectedIndex() == 0 ? "%%" : com.getSelectedItem() + "%");
		if (rs.isEmpty()) {
			eMsg("선택한 기업정보가 없습니다.");
			com.setSelectedIndex(0);
			load();
			return;
		}
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());

			p.add(new JLabel(img(r.get(0), 100, 100)));
			p.add(lbl(r.get(1) + "", 0), "South");
			p.setBorder(new LineBorder(Color.black));
			sc.add(p).setBounds(rs.indexOf(r) * 100, 0, 100, 100);
			items.add(p);
		}

		repaint();
		revalidate();
	}

	private void famous() {
		m1.removeAll();
		var rs = rs("select c_name, c_search from company order by c_search desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout(10, 10));
			p.add(lbl(rs.indexOf(r) + 1 + "", 2), "West");
			p.add(lbl(r.get(0) + "", 2));
			p.add(lbl(r.get(1) + "", 2), "East");
			m1.add(p);
		}

		repaint();
		revalidate();
	}

	private void logout() {
		Stream.of(btn).forEach(b -> b.setVisible(false));
		var cap = "로그인,회원가입,닫기".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i].setText(cap[i]);
			btn[i].setVisible(true);
		}
	}

	static void login() {
		img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/회원사진/" + uno + ".jpg")
				.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		img.setVisible(true);
		Stream.of(btn).forEach(b -> b.setVisible(false));
		var cap = "로그아웃,채용정보,마이페이지,닫기".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i].setText(cap[i]);
			btn[i].setVisible(true);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}