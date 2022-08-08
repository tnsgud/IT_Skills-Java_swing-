package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class Login extends BaseFrame {
	ArrayList<JLabel> list = new ArrayList<>();
	JTextField txt[] = new JTextField[2];
	JPanel bag = new JPanel(new GridBagLayout()), m = new JPanel(new BorderLayout());
	static SystemTray tray = SystemTray.getSystemTray();
	static TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/비행기.jpg"));
	{
		var pop = new JPopupMenu();
		for (var cap : "일정보기,닫기".split(",")) {
			var i = new JMenuItem(cap);

			i.addActionListener(a -> {
				if (cap.equals("일정보기")) {
					BasePage.main.btn[1].doClick();
				} else {
					tray.remove(icon);
				}
			});

			pop.add(i);
		}

		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					pop.setLocation(e.getLocationOnScreen());
					pop.setInvoker(pop);
					pop.setVisible(true);
				}
			}
		});
	}
	
	public static void main(String[] args) {
		var login = new Login();
		login.txt[0].setText("sky01");
		login.txt[1].setText("sky01!");
	}

	public Login() {
		super("메인", 800, 700);

		setResizable(true);

		add(sz(n = new JPanel(null), 800, 200), "North");
		add(bag = new JPanel(new GridBagLayout()));

		bag.add(m);

		m.add(c = new JPanel(new BorderLayout()));
		m.add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < 5; i++) {
			var l = new JLabel(getIcon("./datafiles/구름.jpg", 500, 200));

			n.add(l).setBounds(list.size() * 500, 0, 500, 200);
			list.add(l);
		}

		c.add(lbl("SKY AIRLINE", 0, 30), "North");
		c.add(new JLabel(getIcon("./datafiles/비행기.jpg", 200, 200)));

		s.add(sz(sc = new JPanel(new GridLayout(0, 1)), 150, 40));
		s.add(ss = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i] + ":", 2), 80, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			sc.add(sz(tmp, 150, 20));
		}

		for (var ca : "회원가입,로그인".split(",")) {
			ss.add(btn(ca, a -> {
				if (ca.equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 존재합니다.");
							return;
						}
					}

					if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
						iMsg("관리자님 환영합니다.");
						new Admin().addWindowListener(new Before(this));
						return;
					}

					var rs = getRows("select * from member where m_id =? and  m_pw=?", txt[0].getText(),
							txt[1].getText());

					if (rs.isEmpty()) {
						eMsg("일치하는 회원이 없습니다.");
						for (var t : txt) {
							t.setText("");
						}
						return;
					}

					user = rs.get(0);

					iMsg(user.get(3) + "님 환영합니다.");

					BasePage.main = new Main();
					BasePage.main.addWindowListener(new Before(this));

					try {
						tray.add(icon);
					} catch (AWTException e) {
						e.printStackTrace();
					}

					rs = getRows(
							"select a1.a_name, a2.a_name, r_date, s_time from reservation r, schedule s, airport a1, airport a2 where r.s_no = s.s_no and s.s_depart = a1.a_no and s.s_arrival = a2.a_no and m_no = ? and r_date > now() order by r_date, s.s_no, r.r_no limit 1",
							user.get(0));

					if (rs.isEmpty()) {
						return;
					}

					var r = rs.get(0);

					icon.displayMessage(r.get(0) + "→" + r.get(1), r.get(2) + " " + r.get(3), MessageType.INFO);
				}
			}));
		}

		setVisible(true);

		NorthAnimation();
	}

	private void NorthAnimation() {
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				int j = 0;
				var flag = true;
				while (true) {
					for (int i = 0; i < list.size(); i++) {
						int x = list.get(i).getX();
						int y = (int) (Math.sin(list.get(i).getX() / 10) * 8);

						list.get(i).setLocation(x -= 1, y);

						if (x < -500) {
							list.get(i).setLocation(2000, 0);
						}
					}

					if (++j % 50 == 0) {
						flag = !flag;
					}

					Thread.sleep(5);
				}
			}
		}.execute();
	}
}
