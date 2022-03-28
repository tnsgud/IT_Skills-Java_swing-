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
	JComboBox com = new JComboBox<>("지점,테마".split(","));
	static JButton btn[] = new JButton[6];
	Timer timer;
	ArrayList<JPanel> items = new ArrayList<>();

	public Main() {
		super("메인", 550, 500);
		
		setDefaultCloseOperation(3);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");

		n.add(lblH("예약 TOP5", 2, 0, 15));
		n.add(com);
		com.addActionListener(a -> load());

		load();

		c.add(new JLabel("1"));

		var cap = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(btn[i] = btn(cap[i], a -> {
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
				} else if (a.getActionCommand().equals("예약현황")) {
					new Chart().addWindowListener(new Before(this));
				}
			}));
		}

		logout();
		timer = new Timer(1, a -> {
			items.forEach(i -> {
				i.setLocation(i.getLocation().x, i.getLocation().y - 5);
				if (i.getLocation().y < -300) {
					i.setLocation(i.getLocation().x, items.size() * 250);
				}
			});
		});

		timer.start();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void logout() {
		btn[0].setText("로그인");
		for (int i = 0; i < btn.length; i++) {
			btn[i].setEnabled(i == 0 || i == 5);
		}
	}

	static void login() {
		btn[0].setText("로그아웃");
		for (int i = 0; i < btn.length; i++) {
			btn[i].setEnabled(true);
		}
	}

	private void load() {
		c.removeAll();
		items.clear();
		var t = com.getSelectedIndex() == 0;
		var rs = rs(t
				? "select c_name, count(*) as cnt  from cafe c, reservation r where r.c_no= c.c_no group by c.c_no order by cnt desc limit 5"
				: "select t.t_no, t_name, count(*) as cnt from  theme t, reservation r where r.t_no = t.t_no group by t.t_no order by cnt desc limit 5");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img((t ? "지점/" : "테마/") + r.get(0).toString().split(" ")[0] + ".jpg", 500, 400)));
			p.add(lblH(r.get(t ? 0 : 1) + "", 0, 0, 30), "South");
			items.add(p);
			c.add(sz(p, 500, 300));
		}
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
