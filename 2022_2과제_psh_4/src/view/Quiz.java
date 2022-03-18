package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends BaseFrame {
	LocalTime time;
	Timer timer;
	JLabel img, chancelbl;
	JTextField txt;

	int chance = 3;

	enum AnswerState {
		NONE, RIGHT, WORNG
	}

	AnswerState state = AnswerState.NONE;
	private JLabel timelbl;
	private JLabel answer;
	private JButton btn;

	public Quiz() {
		super("퀴즈", 400, 400);
		timer = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			if (time.equals(LocalTime.of(0, 0, 0))) {
				state = AnswerState.WORNG;
				timer.stop();
				repaint();
				revalidate();
				eMsg("남은 시간이 없으므로 종료합니다.");
				dispose();
				return;
			}
		});

		var se = toInt(rs("select t_time from theme where t_no=?", tno).get(0).get(0));
		time = LocalTime.of(0, se / 60, se % 60);

		add(n = new JPanel(new BorderLayout()), "North");
		add(img = new JLabel(img("퀴즈/" + qno + ".jpg", 400, 300)) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(5));
				g2.setColor(Color.red);

				if (state == AnswerState.RIGHT) {
					g2.drawOval(0, 0, img.getWidth(), img.getHeight());
				} else if (state == AnswerState.WORNG) {
					g2.drawLine(0, 0, img.getWidth(), img.getHeight());
					g2.drawLine(img.getWidth(), 0, img.getHeight(), 0);
				}
			}
		});
		add(s = new JPanel(), "South");

		n.add(lblH("퀴즈번호 : " + qno, 0, 0, 25));
		n.add(chancelbl = lbl("기회 : " + chance, 2));

		ns.add(chancelbl = lbl("기회 :" + chance + "번", 2, 25), "West");
		ns.add(timelbl = lbl("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("m:ss")), 4, 25), "East");

		s.add(answer = lbl("답 입력 :", 2, 12));
		s.add(txt);
		s.add(btn = btn("확인", a -> {
			if (rs("select q_answer from quiz where q_no=?", qno).get(0).get(0)
					.equals(txt.getText().replaceAll("\\s+", ""))) {
				state = AnswerState.RIGHT;
				img.repaint();
				timer.stop();
				iMsg("Q" + qno + "번 문제를 통과하셨습니다.");
				dispose();
			} else {
				chance--;
			}

			chancelbl.setText("기회 :" + chance);

			if (chance == 0) {
				state = AnswerState.WORNG;
				img.repaint();
				eMsg("남은 기회가 없으므로 종료합니다.");
				dispose();
			}
		}));
		setVisible(true);
	}
}
