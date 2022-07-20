package view;

import java.awt.BorderLayout;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends BaseFrame {
	JLabel lblTitle;
	JButton btn[] = new JButton[6];

	public Main() {
		super("메인", 550, 350);
		setDefaultCloseOperation(3);

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(new JLabel(getIcon("./Datafiles/로고.png", 150, 150)));
		c.add(lblTitle = lbl("SCHOOL", 0, 30), "South");

		s.add(sc = new JPanel());
		s.add(ss = new JPanel(), "South");

		for (int i = 0; i < btn.length; i++) {
			(i < 3 ? sc : ss).add(btn[i] = sz(btn("", a -> {
				var cap = a.getActionCommand();

				if (cap.equals("로그인")) {
					new Login().addWindowListener(new Before(this));
				} else if (cap.equals("로그아웃")) {
					logout();
				} else if (cap.equals("회원가입")) {
					new Sign().addWindowListener(new Before(this));
				} else if (cap.equals("비밀번호 변경")) {
					new FindPW(true).addWindowListener(new Before(this));
				} else if (cap.equals("강의 신청")) {
					new LectureRegister().addWindowListener(new Before(this));
				} else if (cap.equals("교재 보러가기")) {
					new ViewBook().addWindowListener(new Before(this));
				} else if (cap.equals("나의 강의")) {
					new MyLecture().addWindowListener(new Before(this));
				} else if (cap.equals("인기 강사")) {
					new PopularLecturer().addWindowListener(new Before(this));
				} else if (cap.equals("강의 등록")) {
					new MakeLecture().addWindowListener(new Before(this));
				} else if (cap.equals("교재 등록")) {
					new MakeBook().addWindowListener(new Before(this));
				} else if (cap.equals("학생 현황")) {
					new StudentStatus().addWindowListener(new Before(this));
				} else {
					System.exit(0);
				}
			}), 160, 30));
		}

		logout();

		opeque(((JPanel) getContentPane()), false);

		setVisible(true);
	}

	void setBtn(String cap[]) {
		Stream.of(btn).forEach(b -> b.setVisible(false));

		for (int i = 0; i < cap.length; i++) {
			btn[i].setText(cap[i]);
			btn[i].setVisible(true);
		}

		s.repaint();
		s.revalidate();
	}

	void logout() {
		user = null;
		setBtn("로그인,회원가입,종료".split(","));
	}

	void student() {
		setBtn("로그아웃,비밀번호 변경,강의 신청,교재 보러가기,나의 강의,인기 강사".split(","));
	}

	void teacher() {
		setBtn("로그아웃,비밀번호 변경,강의 등록,교재 등록,학생현황".split(","));
	}

	public static void main(String[] args) {
		new Main();
	}
}
