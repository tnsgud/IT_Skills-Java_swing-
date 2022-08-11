package view;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;

public class MainFrame extends BaseFrame {
	JToggleButton tog[] = new JToggleButton[4];

	public MainFrame() {
		super("항공권 조회", 800, 600);

		var bar = new JMenuBar();
		setJMenuBar(bar);

		var bg = new ButtonGroup();
		var cap = "예약하기,예약조회,마이페이지,로그아웃".split(",");
		for (int i = 0; i < cap.length; i++) {
			tog[i] = new JToggleButton(cap[i]);
			tog[i].addActionListener(a -> {
				if (a.getActionCommand().equals("예약하기")) {
					swap(new Reserve());
				} else if (a.getActionCommand().equals("예약조회")) {
					swap(new ReservationInquiry());
				} else if (a.getActionCommand().equals("마이페이지")) {
					swap(new MyPage());
				} else {
					Login.tray.remove(Login.icon);
					dispose();
				}
			});

			bg.add(tog[i]);
			bar.add(tog[i]);
		}

		tog[0].doClick();

		setVisible(true);
	}

	void swap(BasePage b) {
		getContentPane().removeAll();

		add(b);

		repaint();
		revalidate();
	}
}
