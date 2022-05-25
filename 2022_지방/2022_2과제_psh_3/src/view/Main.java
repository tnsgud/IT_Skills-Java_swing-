package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main extends BaseFrame {
	JComboBox<String> com;
	ArrayList<JPanel> panels = new ArrayList<JPanel>();
	Timer timer;
	static JButton btn[] = new JButton[6];

	public Main() {
		super("메인", 600, 500);

		timer = new Timer(1, a -> {
			panels.forEach(l -> {
				l.setLocation(l.getLocation().x, l.getLocation().y - 5);
				if (l.getLocation().y <= -400) {
					l.setLocation(l.getLocation().x, l.getHeight() * 4);
				}
			});
		});

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		n.add(lblH("예약 TOP5", 0, 0, 15));
		n.add(com = new JComboBox<>("지점,테마".split(",")));

		com.addActionListener(a -> load());

		for (int i = 0; i < btn.length; i++) {
			s.add(btn[i] = btn("", a -> {
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
		}

		load();
		logout();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setDefaultCloseOperation(3);
		timer.start();

		setVisible(true);
	}

	private void logout() {
		var c = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		uno = 0;
		for (int i = 0; i < btn.length; i++) {
			btn[i].setText(c[i]);
			btn[i].setEnabled(i == 0 || i == 5);
		}
	}

	static void login() {
		var c = "로그아웃,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		for (int i = 0; i < btn.length; i++) {
			btn[i].setText(c[i]);
			btn[i].setEnabled(true);
		}
	}

	private void load() {
		c.removeAll();
		var t = com.getSelectedIndex() == 0;
		var rs = rs("select count(*) as cnt, " + (t ? "c_name" : "t.t_no, t_name")
				+ " from reservation r, cafe c, theme t where " + (t ? "c.c_no = r.c_no" : "r.t_no=t.t_no")
				+ " group by r.c_no order by cnt desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(
					img((t ? "지점/" + r.get(1).toString().split(" ")[0] : "테마/" + r.get(1)) + ".jpg", 600, 350)));
			p.add(lblH(r.get(t ? 1 : 2).toString(), 0, 0, 25), "South");
			panels.add(p);
			c.add(p);
		}

		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
