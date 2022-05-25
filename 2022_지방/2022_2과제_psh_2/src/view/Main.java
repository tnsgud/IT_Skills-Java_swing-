package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends BaseFrame {
	String[] cap = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
	ArrayList<JPanel> panels = new ArrayList<>();
	JComboBox<String> com;
	static ArrayList<JButton> btns = new ArrayList<>();
	Timer timer;

	public Main() {
		super("메인", 550, 450);

		timer = new Timer(1, a -> {
			panels.forEach(p -> {
				p.setLocation(p.getLocation().x, p.getLocation().y - 5);
				if (p.getLocation().y <= -420) {
					p.setLocation(p.getLocation().x, p.getHeight() * 4 + 40);
				}
			});
		});

		ui();

		com.addActionListener(a -> load());

		timer.start();

		setDefaultCloseOperation(3);

		setVisible(true);
	}

	void logout() {
		uno = 0;
		btns.get(0).setText("로그인");
		btns.forEach(b -> b.setEnabled(btns.indexOf(b) == 0 || btns.indexOf(b) == 5));
	}

	static void login() {
		btns.get(0).setText("로그아웃");
		btns.forEach(b -> b.setEnabled(true));
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		n.add(lblH("예약 TOP5", 2, 20));
		n.add(com = new JComboBox<>("지점,테마".split(",")));

		load();

		for (int i = 0; i < cap.length; i++) {
			btns.add(btn(cap[i], a -> {
				if (a.getActionCommand().equals(cap[0])) {
					new Login().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals(cap[1])) {
					new MyPage().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals(cap[2])) {
					new Search().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals(cap[3])) {
					new NoticeBoard().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals(cap[4])) {
					new GameList().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals(cap[5])) {
					new Chart().addWindowListener(new Before(this));
				} else {
					logout();
				}
			}));
			s.add(btns.get(i));
		}
	}

	private void load() {
		timer.stop();
		c.removeAll();
		panels.clear();
		var t = com.getSelectedIndex() == 0;
		var rs = getResult("select count(*) as cnt, " + (t ? "c_name" : "t.t_no, t_name")
				+ " from reservation r, cafe c, theme t where " + (t ? "c.c_no=r.c_no" : "t.t_no=r.t_no")
				+ " and r_date <= '2022-04-05' group by r.c_no order by cnt desc limit 5");
		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(new JLabel(img(
					(t ? "지점" : "테마") + "/" + (t ? r.get(1).toString().split(" ")[0] : r.get(1)) + ".jpg", 450, 400)));
			tmp.add(lbl(r.get((t ? 1 : 2)) + "", 0, 25), "South");
			panels.add(tmp);
			c.add(tmp);
		}

		c.repaint();
		c.revalidate();
		timer.restart();
	}

	public static void main(String[] args) {
		new Main();
	}
}
