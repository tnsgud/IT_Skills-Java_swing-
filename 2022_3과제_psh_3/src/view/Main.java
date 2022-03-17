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
	JLabel img, name;
	JTextField txt;
	JPanel m, m1, m2;
	JButton btn[] = new JButton[5];
	JComboBox com;
	ArrayList<JPanel> items = new ArrayList<JPanel>();
	Timer timer;

	public Main() {
		super("Main", 300, 450);

		add(n = new JPanel(new FlowLayout(1)), "North");
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
		m.add(m1 = new JPanel(new GridLayout(0, 1, 5, 5)));
		m.add(m2 = new JPanel(new GridLayout(0, 1, 5, 5)));

		load();

		for (int i = 0; i < btn.length; i++) {
			m2.add(btn[i] = btn("", a -> {
				if (a.getActionCommand().equals("로그인")) {

				} else if (a.getActionCommand().equals("로그인")) {

				} else if (a.getActionCommand().equals("로그인")) {

				} else if (a.getActionCommand().equals("로그인")) {

				} else if (a.getActionCommand().equals("로그인")) {

				} else if (a.getActionCommand().equals("로그인")) {

				}
			}));
		}

		s.add(sn = new JPanel(new FlowLayout(0)), "North");
		s.add(sz(sc = new JPanel(null), 300, 100));

		sn.add(lbl("지역", 2));
		sn.add(com = new JComboBox<>(local));
		com.addActionListener(a -> famous());

		logout();
		famous();

		timer = new Timer(1, a -> {
			items.forEach(i -> {
				i.setLocation(i.getLocation().x - 1, i.getLocation().y);
				if (i.getLocation().x < -100) {
					i.setLocation(items.size() * 100 - 100, 0);
				}
			});
		});

		timer.start();

		setVisible(true);
	}

	void login() {

		var t = "로그아웃,채용정보,마이페이지,닫기".split(",");
		for (int i = 0; i < t.length; i++) {
			btn[i].setText(t[i]);
			btn[i].setVisible(true);
		}
	}

	private void load() {
		m1.removeAll();
		var rs = rs("select c_name, c_search from company order by c_search desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout(10, 10));
			p.add(lbl(rs.indexOf(r) + 1 + "", 2, 15), "West");
			p.add(lbl(r.get(0) + "", 2, 15));
			p.add(lbl(r.get(1) + "", 2, 15), "East");
			m1.add(p);
		}

		repaint();
		revalidate();
	}

	private void famous() {
		sc.removeAll();
		var rs = rs("select c_img, c_name from company where left(c_address,2) like ?",
				com.getSelectedIndex() == 0 ? "%%" : com.getSelectedItem() + "%");
		items.clear();
		if (rs.isEmpty()) {
			eMsg("선택한 기업정보가 없습니다.");
			com.setSelectedIndex(0);
			famous();
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

	private void logout() {
		Stream.of(btn).forEach(b -> b.setVisible(false));

		var t = "로그인,회원가입,닫기".split(",");
		for (int i = 0; i < t.length; i++) {
			btn[i].setText(t[i]);
			btn[i].setVisible(true);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}