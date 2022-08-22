package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BaseManage extends BaseFrame {
	JComboBox<String> com = new JComboBox<>(getRows("select b_name from base where division = ?", user.get(5)).stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));
	JLabel img;
	JTextField txt[] = new JTextField[2];
	JButton btn[] = new JButton[2];

	public BaseManage() {
		super("농산물 관리", 400, 200);

		add(img = sz(new JLabel(), 150, 150), "West");
		add(c = new JPanel(new GridLayout(0, 1)));
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
						eMsg("공백이 존재합니다.");
						return;
					}

					if (toInt(t.getText()) < 1) {
						eMsg("1이상의 숫자로 입력해주세요.");
						return;
					}
				}

				var bno = getOne("select b_no from base where b_name = ?", com.getSelectedItem());
				var fno = getOne("select f_no from farm where u_no = ? and b_no = ?", user.get(0), bno);
				iMsg(a.getActionCommand() + "이 완료되었습니다.");
				switch (a.getActionCommand()) {
				case "등록":
					execute("insert farm values(0,?,?,?,?)", user.get(0), bno, txt[0].getText(), txt[1].getText());
					break;
				case "수정":
					execute("update farm set f_amount = ?, f_quantity = ? where f_no =?", txt[0].getText(),
							txt[1].getText(), fno);
					break;
				case "삭제":
					execute("delete from farm where f_no = ?", fno);
					break;
				}
			}));
		}

		com.addActionListener(a -> {
			img.setIcon(getIcon(getRows("select b_img from base where b_name = ?", com.getSelectedItem()).get(0).get(0),
					150, 150));
			Stream.of(txt).forEach(t -> t.setText(""));

			var rs = getRows(
					"select b_img, f_amount, f_quantity from farm f, base b where f.b_no = b.b_no and f.u_no= ? and b_name = ?",
					user.get(0), com.getSelectedItem());
			if (rs.isEmpty()) {
				btn[0].setText("등록");
				btn[1].setVisible(false);
				return;
			}

			var data = rs.get(0);
			for (int i = 0; i < txt.length; i++) {
				txt[i].setText(data.get(i + 1).toString());
			}

			btn[0].setText("수정");
			btn[1].setVisible(toInt(txt[1].getText()) == 0);
		});

		com.setSelectedIndex(0);

		setVisible(true);
	}

	public BaseManage(int bno) {
		this();
	}

	public static void main(String[] args) {
		new BaseManage();
	}
}
