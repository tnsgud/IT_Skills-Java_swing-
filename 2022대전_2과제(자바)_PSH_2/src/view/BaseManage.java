package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BaseManage extends BaseFrame {
	JButton btn[] = new JButton[2];
	JTextField txt[] = new JTextField[2];
	JComboBox<String> com = new JComboBox<>(getRows("select b_name from base where division = ?", user.get(5)).stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));
	JLabel img;
	int bno;

	public BaseManage() {
		super("", 400, 250);

		add(img = sz(new JLabel(getIcon(1, 150, 150)), 150, 150), "West");
		add(c = new JPanel(new GridLayout(0, 1, 0, 25)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "제품명,단가,수량".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2), 60, 20));
			tmp.add(i != 0 ? txt[i - 1] = new JTextField(15) : com);

			c.add(tmp);
		}

		cap = "수정,삭제".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(btn[i] = btn(cap[i], a -> {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("공백이 있습니다.");
						return;
					}

					if (toInt(t.getText()) < 1) {
						eMsg("1이상의 숫자로 입력해주세요.");
						return;
					}
				}

				iMsg(a.getActionCommand() + "이 완료되었습니다.");

				var fno = getOne("select f_no from farm where u_no = ? and b_no = ?", user.get(0), bno);

				switch (a.getActionCommand()) {
				case "수정":
					execute("update farm set f_amount = ?, f_auntity = ? where f_no = ?", txt[0].getText(),
							txt[1].getText(), fno);
					break;
				case "등록":
					execute("insert farm values(0,?,?,?,?)", user.get(0), bno, txt[0].getText(), txt[1].getText());
					break;
				case "삭제":
					execute("delete from farm where f_no = ?", fno);
					break;
				}

				dispose();
			}));
		}

		com.addActionListener(a -> {
			bno = toInt(getOne("select b_no from base where b_name = ?", com.getSelectedItem()));
			var rs = getRows("select f_amount, f_quantity from farm where b_no = ? and u_no = ?", bno, user.get(0));

			img.setIcon(getIcon(bno, 150, 150));
			Stream.of(txt).forEach(t -> t.setText(""));

			if (rs.isEmpty()) {
				btn[1].setText("등록");
				btn[1].setVisible(true);
			} else {
				var r = rs.get(0);

				for (int i = 0; i < 2; i++) {
					txt[i].setText(r.get(i).toString());
				}

				btn[1].setText("삭제");
				btn[1].setVisible(toInt(txt[1].getText()) == 0);
			}

			btn[0].setVisible(!txt[0].getText().isEmpty());
		});

		com.setSelectedIndex(0);

		setVisible(true);
	}

	public BaseManage(int bno) {
		this();
		this.bno = bno;

		com.setSelectedItem(getOne("select b_name from base where b_no = ?", bno));
	}
}
