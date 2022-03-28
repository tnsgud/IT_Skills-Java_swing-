package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends BaseFrame {
	JLabel chancelbl, timelbl;
	LocalTime time = LocalTime.of(0, 1, 50);
	int chance = 3;
	Timer timer;
	JTextField txt = new JTextField(15);
	int state = 0;

	public Quiz() {
		super("Q" + qno, 400, 400);

		setDefaultCloseOperation(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JLabel() {
			@Override
			public void paint(Graphics g) {
				var g2 = (Graphics2D) g;

				g.drawImage(img("퀴즈/" + qno + ".jpg", 400, 300).getImage(), 0, 0, 400, 300, this);

				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(5));

				if (state == 1) {
					g2.drawOval(0, 0, getWidth(), getHeight());
				} else if (state == 2) {
					g2.drawLine(0, 0, getWidth(), getHeight());
					g2.drawLine(0, getHeight(), getWidth(), 0);
				}
			}
		});
		add(s = new JPanel(), "South");

		var l1 = lblH("퀴즈번호 : " + qno, 0, 0, 20);
		n.add(l1);
		n.add(ns = new JPanel(new BorderLayout()), "South");
		ns.add(chancelbl = lbl("기회 :" + chance + "번", 2), "West");
		ns.add(timelbl = lbl("남은 시간:" + DateTimeFormatter.ofPattern("mm:ss").format(time), 2), "East");

		var btn = btn("확인", a -> {
			if (rs("select q_answer from quiz where q_no=?", qno).get(0).get(0).equals(txt.getText())) {
				state = 1;
				repaint();
				revalidate();
				iMsg("Q" + qno + "번 문제를 통과하였습니다.");
				execute("update reservation set r_attend=? where r_no=?", 1, rno);
				timer.stop();
				dispose();
			} else {
				chance--;
				chancelbl.setText("기회 :" + chance + "번");
				if (chance == 0) {
					state = 2;
					repaint();
					revalidate();
					eMsg("남은 기회가 없으므로 종료합니다.");
					timer.stop();
					execute("update reservation set r_attend=? where r_no=?", 1, rno);
					dispose();
				}
			}
		});
		btn.setForeground(Color.black);
		btn.setBackground(Color.white);

		var l2 = lbl("탑 입력 : ", 2);
		s.add(l2);
		s.add(txt);
		s.add(btn);

		Stream.of(l1, l2, chancelbl, timelbl).forEach(i -> i.setForeground(Color.white));
		Stream.of(n, ns, s).forEach(i -> i.setBackground(Color.black));

		timer = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			timelbl.setText("남은 시간:" + DateTimeFormatter.ofPattern("mm:ss").format(time));

			if (time.equals(LocalTime.of(0, 0, 0))) {
				state = 2;
				repaint();
				revalidate();
				eMsg("제한시간 초과로 종료합니다.");

			}
		});

		timer.start();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				var y = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 업습니다.\n나가시겠습니까?", "경고",
						JOptionPane.YES_NO_OPTION);
				if (y == JOptionPane.YES_OPTION) {
					execute("update reservation set r_attend=1 where r_no=?", rno);
					var before = (Before) ((Before) getWindowListeners()[1]).b.getWindowListeners()[1];
					before.b.setVisible(true);
					setVisible(false);
				}
			}
		});

		setVisible(true);
	}
}
