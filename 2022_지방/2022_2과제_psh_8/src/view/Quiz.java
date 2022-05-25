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
	int state = 0;

	JTextField txt = new JTextField(10);
	LocalTime time = LocalTime.of(0, 1, 50);
	int chance = 3;
	JLabel chancelbl, timelbl;
	Timer timer;

	public Quiz() {
		super("Q" + qno, 500, 410);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JLabel() {
			@Override
			public void paint(Graphics g) {
				var g2 = (Graphics2D) g;

				g.drawImage(img("퀴즈/" + qno + ".jpg", 500, 300).getImage(), 0, 0, 500, 300, this);

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
		ns.add(chancelbl = lbl("기회 : " + chance + "번", 2), "West");
		ns.add(timelbl = lbl("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("mm:ss")), 4), "East");

		var b = btn("확인", a -> {
			var tmp = rs("select q_answer from quiz where q_no=?", qno).get(0).get(0).toString();
			var s = tmp.substring(0, tmp.length() - 1);
			if (s.equals(txt.getText().replaceAll("\\s", ""))) {
				state = 1;
				repaint();
				iMsg("Q" + qno + "번 문제를 통과하였습니다.");
				execute("update reservation set r_attend=1 where r_no=?", rno);
				timer.stop();
				new GameList();
			} else {
				chance--;
				chancelbl.setText("기회 : " + chance + "번");
				if (chance == 0) {
					state = 2;
					repaint();
					eMsg("남은 기회가 없으므로 종료합니다.");
				}
			}
		});

		var l2 =lbl("답 입력 :", 0);
		s.add(l2);
		s.add(txt);
		s.add(b);

		b.setForeground(Color.black);
		b.setBackground(Color.white);

		timer = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			timelbl.setText("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("mm:ss")));

			if (time.equals(LocalTime.of(0, 0, 0))) {
				state = 2;
				repaint();
				eMsg("제한시간 초과로 종료합니다.");
				execute("update reservation set r_attend=1 where r_no=?", rno);
				timer.stop();
				setVisible(false);
				new GameList();
			}
		});
		timer.start();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				timer.stop();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				if(time.isAfter(LocalTime.of(0, 0, 0)) || chance > 0) {
					var n = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=1 where r_no=?", rno);
						setDefaultCloseOperation(2);
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});
		
		Stream.of(n, ns, s).forEach(n->n.setBackground(Color.black));
		Stream.of(l1, l2, chancelbl, timelbl).forEach(i->i.setForeground(Color.white));
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new GameList();
	}
}
