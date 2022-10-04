package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class MainFrame extends BaseFrame {
	JToggleButton btn[] = new JToggleButton[4];

	public MainFrame() {
		super("", 500, 400);

		var bar = new JMenuBar();

		setJMenuBar(bar);

		add(n = new JPanel(new FlowLayout(0, 0, 0)), "North");
		add(c = new JPanel(new BorderLayout()));

		var bg = new ButtonGroup();
		var cap = "예약하기,예약조회,마이페이지,로그아웃".split(",");
		for (int i = 0; i < cap.length; i++) {
			btn[i] = new JToggleButton(cap[i]);
			btn[i].addActionListener(a -> {
				if (a.getActionCommand().equals("예약하기")) {
					swap(new Reserve());
				} else if (a.getActionCommand().equals("예약조회")) {
					swap(new ReservationInquiry());
				} else if (a.getActionCommand().equals("마이페이지")) {
					swap(new MyPage());
				} else {
					LoginFrame.tray.remove(LoginFrame.icon);
					dispose();
				}
			});

			bg.add(btn[i]);
			bar.add(btn[i]);
		}

		btn[0].doClick();
		
		setVisible(true);
	}

	void swap(BasePage p) {
		c.removeAll();

		c.add(p);

		c.repaint();
		c.revalidate();
	}
}
