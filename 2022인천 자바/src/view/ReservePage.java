package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ReservePage extends BasePage {
	JComboBox<String> com;
	JTextField txt;

	JTextField[] txt1 = new JTextField[3];
	JTextField[] txt2 = new JTextField[2];
	JComboBox<String> type;

	String cap[] = "이름,주민등록 번호,휴대전화 번호,백신 종류,의료기간".split(",");
	String bname;

	public ReservePage(String bname) {
		super();
		this.bname = bname;

		ui();
	}

	void ui() {
		setLayout(new GridBagLayout());

		add(sz(c = new JPanel(new GridLayout(0, 1, 5, 5)), 250, 350));

		for (int i = 0; i < cap.length; i++) {
			var tmp1 = new JPanel(new GridLayout(1, 0, 0, 5));
			var tmp2 = new JPanel(new GridLayout(1, 0, 0, 5));
			tmp1.add(BasePage.lbl(cap[i], JLabel.LEFT, 15));
			if (i < 3) {
				tmp2.add(txt1[i] = new JTextField(15));
			} else if (i == 3) {
				tmp2.add(type = new JComboBox<String>(new DefaultComboBoxModel<>("아스트라제네카,얀센,화이자,모더나".split(","))));
			} else {
				tmp1.add(BasePage.lbl("날짜", JLabel.LEFT, 15));
				tmp2.add(txt2[0] = new JTextField());
				tmp2.add(txt2[1] = new JTextField());
			}
			c.add(tmp1);
			c.add(tmp2);
		}

		txt2[0].setText(bname);
		
		txt2[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});

		txt2[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new CalendarDialog(txt2[1]).setVisible(true);
			}
		});

		c.add(btn("확인", a -> {
			if (txt1[0].getText().isEmpty() || txt1[1].getText().isEmpty() || txt1[2].getText().isEmpty()
					|| txt2[0].getText().isEmpty() || txt2[1].getText().isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			if ((txt1[1].getText().length() != 14)) {
				eMsg("주민번호 길이를 맞추세요.");
				return;
			}

			if (!txt1[1].getText().matches(".*[0-9].*")) {
				eMsg("주민번호는 숫자로 입력해주세요.");
				return;
			}

			String building = getOne("select * from building where point = '" + bpoint + "'");
			int price = (type.getSelectedIndex() == 0) ? 4500
					: (type.getSelectedIndex() == 1) ? 12000 : (type.getSelectedIndex() == 2) ? 26000 : 30000;

			iMsg("예약이 완료되었습니다.");
			execute("insert into purchase values(0, '" + uno + "','" + txt2[1].getText() + "','" + building + "','"
					+ price + "'");
			mf.swapPage(new SearchPage());
		}));
	}
}
