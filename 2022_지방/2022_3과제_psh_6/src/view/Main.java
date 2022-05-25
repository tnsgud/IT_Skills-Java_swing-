package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	JPanel m, m1, m2;
	ArrayList<JPanel> items = new ArrayList<JPanel>();
	JComboBox com = new JComboBox<>(local);
	static JLabel img, name;
	static JButton btn[] = new JButton[4];
	JTextField txt = new JTextField(10);
	Timer timer;

	public Main() {
		super("Main", 300, 500);

		setDefaultCloseOperation(3);

		add(n = new JPanel(), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblH("아르바이트", 0, 0, 20));
		n.add(img = lbl("", 0));
		n.add(name = lbl("", 0, 15));

		c.add(cn = new JPanel(), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		cn.add(lbl("기업검색", 0));
		cn.add(txt);
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
			new Company().addWindowListener(new Before(this));
			search();
		}));

		cc.add(lbl("인기기업", 2), "North");
		cc.add(m = new JPanel(new GridLayout(1, 0, 5, 5)));
		m.add(m1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		for (int i = 0; i < btn.length; i++) {
			m2.add(btn[i] = btn("", a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					logout();
				} else if (a.getActionCommand().equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("닫기")) {
					dispose();
				} else if (a.getActionCommand().equals("채용정보")) {
					new Jobs().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(this));
				}
			}));
		}

		s.add(sn = new JPanel(new FlowLayout(0)), "North");
		s.add(sz(sc = new JPanel(null), 300, 100));
		sn.add(lbl("지역", 0));
		sn.add(com);

		com.addActionListener(a -> load());

		search();
		logout();
		load();

		img.setBorder(new LineBorder(Color.black));

		timer = new Timer(1, a -> {
			items.forEach(i -> {
				i.setLocation(i.getLocation().x - 1, i.getLocation().y);
				if (i.getLocation().x < -100) {
					i.setLocation(items.size() * 100, i.getLocation().y);
				}
			});
		});
		timer.start();

		setVisible(true);
	}

	static void login() {
		Stream.of(btn).forEach(b -> b.setVisible(false));
		Stream.of(img, name).forEach(l -> l.setVisible(true));
		var cap = "로그아웃,채용정보,마이페이지,닫기".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i].setText(cap[i]);
			btn[i].setVisible(true);
		}
	}

	private void logout() {
		Stream.of(btn).forEach(b -> b.setVisible(false));
		Stream.of(img, name).forEach(l -> l.setVisible(false));
		var cap = "로그인,회원가입,닫기".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i].setText(cap[i]);
			btn[i].setVisible(true);
		}

		repaint();
		revalidate();
	}

	private void search() {
		m1.removeAll();
		var rs = rs("select c_name, c_search from company order by c_search desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(rs.indexOf(r) + 1 + "", 2, 12), 30, 20), "West");
			p.add(lbl(r.get(0) + "", 2, 12));
			p.add(lbl(r.get(1) + "", 2, 12), "East");
			m1.add(p);
		}

		repaint();
		revalidate();
	}

	private void load() {
		items.clear();
		sc.removeAll();
		var rs = rs("select c_name, c_img from company "
				+ (com.getSelectedIndex() == 0 ? "" : "where left(c_address, 2) = '" + com.getSelectedItem() + "'"));
		if (rs.isEmpty()) {
			eMsg("선택한 기업정보가 없습니다.");
			com.setSelectedIndex(0);
			load();
			return;
		}
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img(r.get(1), 100, 80)));
			p.add(lbl(r.get(0) + "", 0), "South");
			p.setBorder(new LineBorder(Color.black));
			sc.add(p).setBounds(items.size() * 100, 0, 100, 100);
			items.add(p);
		}

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
