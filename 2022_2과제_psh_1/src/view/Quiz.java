package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends BaseFrame {
	JLabel chancelbl, numlbl, timelbl, answer, img;
	JTextField txt = new JTextField(15);
	Timer timer;
	int chance = 3;
	LocalTime time = LocalTime.of(0, 1, 50);
	JButton btn;

	enum AnswerState {
		NONE, RIGHT, WRONG
	};

	AnswerState answerState = AnswerState.NONE;

	public Quiz() {
		super("퀴즈", 500, 450);

		ui();
		event();

		timer = new Timer(1000, a -> {
			time = time.minusSeconds(1);
			timelbl.setText("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("m:ss")));
			if (time.isBefore(LocalTime.of(0, 0, 1))) {
				eMsg("제한시간 초과로 종료합니다.");
				dispose();
			}
		});
		timer.start();

		setVisible(true);
	}

	private void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				timer.stop();
				execute("update reservation set r_attend=1 where r_no=?", rno);
			}
		});
	}

	private void ui() {
		var ns = new JPanel(new BorderLayout());

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(img = new JLabel(img("퀴즈/" + qno + ".jpg", 500, 300).getIcon()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(5));

				if (AnswerState.RIGHT == answerState) {
					g2.drawOval(0, 0, img.getWidth(), img.getHeight());
				} else if (AnswerState.WRONG == answerState) {
					g2.drawLine(0, 0, img.getWidth(), img.getHeight());
					g2.drawLine(img.getWidth(), 0, 0, img.getHeight());
				}
			}
		});
		add(s = new JPanel(new FlowLayout(1)), "South");

		n.add(numlbl = lbl("퀴즈번호 : " + qno, 0, 35));
		n.add(ns, "South");

		ns.add(chancelbl = lbl("기회 : " + chance + "번", 2, 25), "West");
		ns.add(timelbl = lbl("남은 시간 : " + time.format(DateTimeFormatter.ofPattern("m:ss")), 4, 25), "East");

		s.add(answer = lbl("답 입력 : ", 2, 12));
		s.add(txt);
		s.add(btn = btn("확인", a -> {
			if (getOne("select q_answer from quiz where q_no=?", qno).equals(txt.getText().replaceAll("\\s+", ""))) {
				answerState = AnswerState.RIGHT;
				img.repaint();
				timer.stop();
				iMsg("Q"+qno+"번 문제를 통과하였습니다.");
				dispose();
			} else {
				chance--;
			}

			chancelbl.setText("기회 : " + chance + "번");

			if (chance == 0) {
				answerState = AnswerState.WRONG;
				img.repaint();
				eMsg("남은 기회가 없으므로 종료합니다.");
				dispose();
			}
		}));

		setBackground(Color.black);

		Stream.of(numlbl, chancelbl, timelbl, n, s, ns, answer).forEach(l -> {
			l.setForeground(Color.white);
			l.setBackground(Color.black);
		});
	}

	public static void main(String[] args) {
		qno = 1;
		new Quiz();
	}
}
