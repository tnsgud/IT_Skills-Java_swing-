package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class LoginFrame extends BaseFrame {
	JPanel bag = new JPanel(new GridBagLayout()), m = new JPanel(new BorderLayout());
	JTextField txt[] = new JTextField[2];
	ArrayList<JLabel> list = new ArrayList<>();
	static SystemTray tray = SystemTray.getSystemTray();
	static TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/비행기.jpg"));
	{
		var pop = new JPopupMenu();
		for (var cap : "일정보기,닫기".split(",")) {
			var i = new JMenuItem(cap);
			i.addActionListener(a -> {
				if (cap.equals("일정보기")) {
					BasePage.mf.btn[1].doClick();
				} else {
					tray.remove(icon);
				}
			});

			pop.add(i);
		}

		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 3)
					return;

				pop.setLocation(e.getLocationOnScreen());
				pop.setInvoker(pop);
				pop.setVisible(true);
			}
		});
	}

	public LoginFrame() {
		super("메인", 800, 700);

		add(n = sz(new JPanel(null), 800, 200), "North");
		add(bag);

		for (int i = 0; i < 5; i++) {
			var l = new JLabel(getIcon("./datafiles/구름.jpg", 500, 200));

			n.add(l).setBounds(list.size() * 500, 0, 500, 200);
			list.add(l);
		}

		bag.add(m);

		m.add(c = new JPanel(new BorderLayout()));
		m.add(s = new JPanel(new BorderLayout()), "South");

		c.add(lbl("SKY AIRLINE", 0, 30), "North");
		c.add(new JLabel(getIcon("./datafiles/비행기.jpg", 200, 200)));

		s.add(sc = new JPanel(new GridLayout(0, 1, 5, 5)));
		s.add(ss = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i] + ":", 2, 15), 80, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			sc.add(tmp);
		}

		for (var ca : "회원가입,로그인".split(",")) {
			ss.add(btn(ca, a -> {
				if (ca.equals("회원가입")) {
					new SignFrame().addWindowListener(new Before(this));
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 존재합니다.");
							return;
						}
					}

					if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
						iMsg("관리자님 환영합니다.");
						new AdminFrame().addWindowListener(new Before(this));
						return;
					}

					var rs = getRows("select * from member where m_id = ? and m_pw = ?", txt[0].getText(),
							txt[1].getText());
					if (rs.isEmpty()) {
						eMsg("일치하는 회원이 업습니다.");
						Stream.of(txt).forEach(t -> t.setText(""));
						return;
					}

					member = rs.get(0);
					iMsg(member.get(3) + "님 환영합니다.");

					BasePage.mf = new MainFrame();
					BasePage.mf.addWindowListener(new Before(this));

					try {
						tray.add(icon);
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					rs = getRows(
							"select a1_name, a2_name, r_date, s_time from v1, reservation r where v1.s_no = r.s_no and now() < r_date and m_no = ? order by r_date",
							member.get(0));
					if (rs.isEmpty())
						return;

					var r = rs.get(0);

					icon.displayMessage(r.get(0) + " → " + r.get(1), r.get(2) + " " + r.get(3), MessageType.INFO);
					txt[0].setText("");
					txt[1].setText("");
				}
			}));
		}

		((JPasswordField) txt[1]).setEchoChar('●');

		setVisible(true);

		animation();
	}

	private void animation() {
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				int j = 0;
				while (true) {
					for (var img : list) {
						int x = img.getX();
						int y = (int) (Math.sin(x / 10) * 8);

						img.setLocation(x -= 1, y);
						if (x < -500) {
							img.setLocation(2000, 0);
						}
					}

					Thread.sleep(5);
				}
			}
		}.execute();
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}
