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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends BaseFrame {
	JTextField txt = new JTextField(15);
	LocalTime time = LocalTime.of(0, 1, 50);
	int answer = 0, chance = 3;
	JLabel timelbl, chancelbl;
	Timer timer;
	ImageIcon img = img("퀴즈/" + qno + ".jpg", 500, 300);
	String a;
	int b = 0;

	public Quiz() {
		super("Q" + qno, 500, 500);

		a = rs("select q_answer from quiz where q_no=?", qno).get(0).get(0).toString();

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(5));

				g.drawImage(img.getImage(), 0, 0, 500, 400, this);

				if (answer == 1) {
					g2.drawOval(0, 0, 500, 300);
				} else if (answer == 2) {
					g2.drawLine(0, 0, getWidth(), getHeight());
					g2.drawLine(getWidth(), 0, 0, getHeight());
					System.out.println("asdf");
				}
			}
		});
		add(s = new JPanel(), "South");

		n.add(lblH("퀴즈번호 : " + qno, 0, 0, 20));
		n.add(ns = new JPanel(new BorderLayout()), "South");

		ns.add(chancelbl = lbl("기회:" + chance + "번", 2), "West");
		ns.add(timelbl = lbl("남은 시간:" + time.format(DateTimeFormatter.ofPattern("mm:ss")), 4), "East");

		var btn = btn("확인", a -> {
			if (!txt.getText().equals(a)) {
				chance--;
			} else {
				iMsg("Q" + qno + "번 문제를 통과하였습니다.");
				answer = 1;
			}

			if (chance == 0) {
				b = 1;
				answer = 2;
				repaint();
				eMsg("남은 기화가 없으므로 종료합니다.");
				dispose();
			}

		});
		s.add(lbl("답 입력 :", 2));
		s.add(txt);
		s.add(btn);
		btn.setForeground(Color.black);
		btn.setBackground(Color.white);

		timer = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			timelbl.setText("남은 시간:" + time.format(DateTimeFormatter.ofPattern("mm:ss")));
			if (time.equals(LocalTime.of(0, 0, 0))) {
				eMsg("제한시간 초과로 종료합니다.");
				b = 1;
				dispose();
			}
		});
		timer.start();

//		n.setBackground(Color.black);
//		ns.setBackground(Color.black);
//		s.setBackground(Color.black);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (b == 0) {
					var y = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (y == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=? where r_no=?", 1, rno);
						setDefaultCloseOperation(2);
						new GameList();
					} else {
						setDefaultCloseOperation(0);
					}
				}else {
					execute("update reservation set r_attend=? where r_no=?", 1, rno);
					setDefaultCloseOperation(2);
					new GameList();
				}
			}
		});
		setVisible(true);
	}
}
