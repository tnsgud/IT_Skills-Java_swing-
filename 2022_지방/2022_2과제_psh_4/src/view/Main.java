package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends BaseFrame {
	JComboBox com = new JComboBox<>("지점,테마".split(","));
	static JButton btn[] = new JButton[6];
	Timer timer;
	ArrayList<JPanel> items = new ArrayList<JPanel>();

	public Main() {
		super("메인", 700, 500);

		setDefaultCloseOperation(3);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		n.add(lblH("예약 TOP5", 0, 0, 15));
		n.add(com);
		com.addActionListener(a -> load());

		var c = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		for (int i = 0; i < c.length; i++) {
			s.add(btn[i] = btn(c[i], a -> {
				if (a.getActionCommand().equals("로그인")) {
					new Login().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("로그아웃")) {
					logout();
				} else if (a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("검색")) {
					new Search().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("게시판")) {
					new Notice().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("방탈출게임")) {
					new GameList().addWindowListener(new Before(this));
				} else {
					new Chart().addWindowListener(new Before(this));
				}
			}));
			btn[i].setEnabled(i == 0 || i == 5);
		}

		load();

		timer = new Timer(1, a -> {
			items.forEach(i -> {
				i.setLocation(i.getLocation().x, i.getLocation().y - 5);
				if (i.getLocation().y < -450) {
					i.setLocation(i.getLocation().x, items.size() * 450);
				}
			});
		});

		timer.start();

		setVisible(true);
	}

	static void login() {
		btn[0].setText("로그아웃");
		for (int i = 0; i < btn.length; i++) {
			btn[i].setEnabled(true);
		}
	}

	void logout() {
		var c = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		for (int i = 0; i < c.length; i++) {
			btn[i].setText(c[i]);
			btn[i].setEnabled(i == 0 || i == 5);
		}
	}

	private void load() {
		c.removeAll();
		var t = com.getSelectedIndex() == 0;
		var rs = rs("select count(*) as cnt, " + (t ? "c_name" : "t.t_no, t_name")
				+ " from reservation r, cafe c, theme t where t.t_no = r.t_no and c.c_no = r.c_no group by r.c_no order by cnt desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img((t ? "지점" : "테마") + "/" + r.get(1).toString().split(" ")[0] + ".jpg", 700, 400)));
			p.add(lblH(r.get(t ? 1 : 2) + "", 0, 1, 35), "South");
			items.add(p);
			c.add(sz(p, 700, 450));
		}

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
