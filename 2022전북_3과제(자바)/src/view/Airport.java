package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Airport extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	
	public Airport() {
		super("공항등록", 500, 500);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(btn("확인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().replaceAll("[^A-Z]", "").length() != 3) {
				eMsg("코드는 영어대문자 3자리로 입력하세요.");
				return;
			}

			if (!getOne("select * from airport where a_code=?", txt[0].getText()).isEmpty()) {
				eMsg("이미 존재하는 공항코드입니다.");
				return;
			}

			double la = Double.parseDouble(txt[2].getText()), lo = Double.parseDouble(txt[3].getText());
			if (!txt[2].getText().matches("\\d{2}.\\d{8}") || !(-91 < la && la < 91)) {
				eMsg("위도를 알맞게 입력해주세요.");
				return;
			}

			if (!txt[3].getText().matches("\\d{3}.\\d{8}") || !(-181 < la && la < 181)) {
				eMsg("경도를 알맞게 입력해주세요.");
				return;
			}

			iMsg("등록이 완료되었습니다.");
			execute("insert into airport values(0, ?, ?, ?, ?)",  Stream.of(txt).map(JTextField::getText).toArray());
			dispose();
		}), "South");

		var cap = "코드,이름,위도,경도".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i], 2, 12), 80, 20), "West");
			tmp.add(txt[i] = new JTextField());

			c.add(tmp);
		}

		setVisible(true);
	}
}
