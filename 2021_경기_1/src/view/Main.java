package view;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends BaseFrame {
	String cap[] = "로그인,회원가입,위치정보,예매,매직패스(0),Mypage,놀이기구 인기순위 TOP5,놀이기구 등록/수정,월별 수정,종료".split(",");
	JLabel lbls[] = new JLabel[cap.length];
	Timer timer;
	int idx = 1;

	public Main() {
		super("메인", 700, 350);

		ui();
		event();

		timer = new Timer(1000, a -> {
			c.removeAll();
			c.add(new JLabel(img("./datafiles/캐릭터/로리" + idx + ".jpg", 250, 250)));
			c.add(new JLabel(img("./datafiles/캐릭터/로티" + idx + ".jpg", 250, 250)));

			idx = idx == 3 ? 1 : idx == 1 ? 2 : 3;

			c.repaint();
			c.revalidate();

			
			setEnable();
		});
		timer.start();

		setVisible(true);
	}

	private void event() {
		for (int i = 0; i < cap.length; i++) {
			lbls[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					var l = (JLabel) e.getSource();

					if (!l.isEnabled()) {
						return;
					}

					if (l.equals(lbls[0])) {
						if(l.getText().equals("로그인")) {
							new Login().addWindowListener(new Before(Main.this));							
						}else {
							isLoginned = 0;
							iMsg("로그아웃이 완료되었습니다.");
							setEnable();
						}
					} else if (l.equals(lbls[1])) {
						new Sign().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[2])) {
						new LocationInfo().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[3])) {
						new Reserve().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[4])) {
						new MagicPass().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[5])) {
						new MyPage().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[6])) {
						new Chart().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[7])) {
					 new Ride().addWindowListener(new Before(Main.this));
					} else if (l.equals(lbls[8])) {
						new Calendar().addWindowListener(new Before(Main.this));
					} else {
						System.exit(0);
					}
				}
			});
		}
	}

	private void ui() {
		add(w = new JPanel(new GridLayout(0, 1)), "West");
		add(c = new JPanel(new GridLayout(1, 0)));

		c.add(new JLabel(img("./datafiles/캐릭터/로리" + idx + ".jpg", 250, 250)));
		c.add(new JLabel(img("./datafiles/캐릭터/로티" + idx + ".jpg", 250, 250)));

		for (int i = 0; i < cap.length; i++) {
			lbls[i] = lblB(cap[i], JLabel.CENTER, 15);
			w.add(lbls[i]);
		}

		setEnable();
	}

	void setEnable() {
		for (int i = 0; i < cap.length; i++) {
			lbls[i].setEnabled(true);
		}

		if (isLoginned == 0) {
			lbls[0].setText("로그인");
			for (int i = 0; i < 6; i++) {
				lbls[i + 2 < 6 ? i + 2 : i + 3].setEnabled(false);
			}
		} else if (isLoginned == 1) {
			lbls[0].setText("로그아웃");
			for (int i = 0; i < 3; i++) {
				lbls[i+1 < 2 ? i+1:i+6].setEnabled(false);
			}
		} else if (isLoginned == 2) {
			lbls[0].setText("로그아웃");
			for (int i = 0; i < 5; i++) {
				lbls[i+1].setEnabled(false);
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
