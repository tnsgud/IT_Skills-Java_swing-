package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	JPanel n, c, e, w, s;
	JPanel nn, nc, ne, nw, ns;
	JPanel cn, cc, ce, cw, cs;
	JPanel en, ec, ee, ew, es;
	JPanel wn, wc, we, ww, ws;
	JPanel sn, sc, se, sw, ss;
	static String uno, uname, ugender, ugraduate;
	String[] category = ",편의점,영화관,화장품,음식점,백화점,의류점,커피전문점,은행".split(","),
			local = "전체,서울,부산,대구,인천,광주,대전,울산,세종,경기,강원,충북,전북,전남,경북,경남,제주".split(","),
			graduate = "대학교 졸업,고등학교 졸업,중학교 졸업,무관".split(","), gender = "남자,여자,무관".split(",");

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		try {
			execute("use 2022지방_2");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
}
