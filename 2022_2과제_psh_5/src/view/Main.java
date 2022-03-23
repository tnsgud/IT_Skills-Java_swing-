package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends BaseFrame {
	ArrayList<JPanel> items = new ArrayList<>();
	static JButton btn[] = new JButton[6];
	JComboBox com = new JComboBox<>("지점,테마	".split(","));
	Timer timer;

	public Main() {
		super("메인", 650, 500);
		setDefaultCloseOperation(3);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new FlowLayout()));
		add(s = new JPanel(), "South");

		n.add(lblH("예약 TOP5", 2, 0, 15));
		n.add(com);

		load();
		
		timer = new Timer(1, a->{
			items.forEach(i->{
				i.setLocation(i.getLocation().x, i.getLocation().y-5);
				if(i.getLocation().y <= -450) {
					i.setLocation(i.getLocation().x, (items.size()-1) * 450+80);
				}
			});
		});
		
		timer.start();
		
		var c = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		for (int j = 0; j < c.length; j++) {
			s.add(btn[j] = btn(c[j], a->{
				if(a.getActionCommand().equals("로그인")) {
					new Login().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("로그아웃")) {
					logout();
				}else if(a.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("검색")) {
					new Search().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("게시판")) {
					new Notice().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("방탈출게임")) {
					new GameList().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("예약현황")) {
					new Chart().addWindowListener(new Before(this));
				}
			}));
		}
		
		logout();
		
		com.addActionListener(a->load());

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

		var rs = rs(
				"select count(*) as cnt, c_name, t.t_no, t_name from reservation r, cafe c, theme t where c.c_no= r.c_no and t.t_no = r.t_no group by r.c_no order by cnt desc limit 5");
		var t = com.getSelectedIndex() == 0;
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(
					img((t ? "지점/" + r.get(1).toString().split(" ")[0] : "테마/" + r.get(2))
							+ ".jpg", 600, 450)));
			p.add(lblH(r.get(t ? 1:3)+"", 0, 0, 20), "South");
			c.add(sz(p, 600, 450));
			items.add(p);
		}
		
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
