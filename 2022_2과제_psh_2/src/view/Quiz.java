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

import com.mysql.cj.exceptions.WrongArgumentException;


public class Quiz extends BaseFrame {
	JLabel chancelbl, numlbl, timelbl, answer, img;
	JTextField txt = new JTextField(15);
	Timer timer;
	int chacne = 3;
	LocalTime time = LocalTime.of(0, 1, 50);
	JButton btn;

	enum AnswerSate {
		NONE, RIGHT, WORNG
	}

	AnswerSate state = AnswerSate.NONE;

	public Quiz() {
		super("퀴즈", 500, 450);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(img = new JLabel(img("퀴즈/" + qno + ".jpg", 500, 300)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(5));

				if (state == AnswerSate.RIGHT) {
					g2.drawOval(0, 0, img.getWidth(), img.getHeight());
				} else if (state == AnswerSate.WORNG) {
					g2.drawLine(0, 0, img.getWidth(), img.getHeight());
					g2.drawLine(img.getWidth(), 0, 0, img.getHeight());
				}
			}
		});
		add(s = new JPanel(), "South");

		n.add(numlbl = lbl("퀴즈번호 : " + qno, 0, 35));
		n.add(ns = new JPanel(new BorderLayout()), "South");

		ns.add(chancelbl = lbl("기회 :" + chacne + "번", 2, 25), "West");
		ns.add(timelbl = lbl("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("m:ss")), 4, 25), "East");

		s.add(answer = lbl("답 입력 :", 2, 12));
		s.add(txt);
		s.add(btn = btn("확인", a -> {
			if (getResult("select q_answer from quiz where q_no=?", qno).get(0).get(0)
					.equals(txt.getText().replaceAll("\\s+", ""))) {
				state = AnswerSate.RIGHT;
				img.repaint();
				timer.stop();
				iMsg("Q" + qno + "번 문제를 통과하셨습니다.");
				dispose();
			} else {
				chacne--;
			}

			chancelbl.setText("기회 :" + chacne);

			if (chacne == 0) {
				state = AnswerSate.WORNG;
				img.repaint();
				eMsg("남은 기회가 없으므로 종료합니다.");
				dispose();
			}
		}));
	}
}
