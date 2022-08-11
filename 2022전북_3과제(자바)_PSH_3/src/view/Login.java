package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class Login extends BaseFrame {
	JPanel bag = new JPanel(new GridBagLayout()), m = new JPanel(new BorderLayout());
	JTextField txt[] = new JTextField[2];
	static SystemTray tray = SystemTray.getSystemTray();
	static TrayIcon icon = new TrayIcon(
			Toolkit.getDefaultToolkit().getImage("./datafiles/비행기.png").getScaledInstance(100, 100, 4));

	public Login() {
		super("메인", 800, 700);
		
		setBackground(Color.white);

		add(n = sz(new JPanel(null), 800, 200), "North");
		add(bag);

		for (int i = 0; i < 5; i++) {
			var l = new JLabel(getIcon("./datafiles/구름.jpg", 500, 200));

			n.add(l).setBounds(i * 500, 0, 500, 200);
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

			tmp.add(sz(lbl(cap[i] + ":", 2, 15), 50, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			sc.add(tmp);
		}

		for (var ca : "회원가입,로그인".split(",")) {
			ss.add(btn(ca, a -> {
				if (ca.equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
					return;
				}

				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
					iMsg("관리자님 환영합니다.");
					return;
				}

				var rs = getRows("select * from member where m_id =? and m_pw = ?", txt[0].getText(), txt[1].getText());
				if (rs.isEmpty()) {
					eMsg("일치하는 회원이 없습니다.");
					Stream.of(txt).forEach(t -> t.setText(""));
					return;
				}

				BasePage.member = rs.get(0);
				iMsg(rs.get(0).get(3) + "님 환영합니다.");

				BasePage.mf = new MainFrame();
				BasePage.mf.addWindowListener(new Before(this));

				try {
					tray.add(icon);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				rs = getRows(
						"select concat(a1_name, '→', a2_name), concat(r_date, ' ', s_time) from v1, reservation r where v1.s_no = r.s_no and now() < r_date and r.m_no = ? order by r_date limit 1",
						BasePage.member.get(0));
				if (rs.isEmpty())
					return;

				var r = rs.get(0);

				icon.displayMessage(r.get(0).toString(), r.get(1).toString(), MessageType.INFO);
				Stream.of(txt).forEach(t -> t.setText(""));
			}));
		}

		new SwingWorker<>() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					for (var com : n.getComponents()) {
						int x = com.getX();
						int y = (int) (Math.sin(x / 10) * 8);

						com.setLocation(x -= 1, y);

						if (x < -500) {
							com.setLocation(2000, y);
						}
					}

					Thread.sleep(5);
				}
			}
		}.execute();


		var d = UIManager.getLookAndFeelDefaults();
		for (var k : d.keySet()) {
			if(k.toString().contains("back")) {
				d.put(k, new ColorUIResource(Color.white));
			}
		}
		
		SwingUtilities.updateComponentTreeUI(this);
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
