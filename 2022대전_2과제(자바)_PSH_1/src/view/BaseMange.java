package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class BaseMange extends BaseFrame {
	JLabel img;
	JButton btn[] = new JButton[2];
	JTextField txt[] = { new JTextField(15), new JTextField(15) };
	JComboBox<String> com = new JComboBox<>(getRows("select b_name from base where division = ?", user.get(5)).stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));
	int b_no;

	public BaseMange() {
		super("농산물관리", 500, 200);

		setLayout(new BorderLayout(5, 5));

		add(img = sz(new JLabel(getIcon(getRows("select b_img from base where b_no = ?", 1).get(0).get(0), 150, 150)),
				150, HEIGHT), "West");
		add(c = new JPanel(new GridLayout(0, 1, 0, 25)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		com.addActionListener(a -> {
			b_no = toInt(getOne("select b_no from base where b_name = ?", com.getSelectedItem()));
			var blob = getRows("select b_img from base where b_no= ?", b_no).get(0).get(0);
			var rs = getRows(
					"select b.b_img, b.b_no, f_amount, f_quantity from base b, farm f where f.b_no =  b.b_no and b.b_no = ? and f.u_no = ? group by b.b_no ",
					b_no, user.get(0));

			img.setIcon(getIcon(blob, 150, 150));

			if (rs.isEmpty()) {
				for (var t : txt) {
					t.setText("");
				}

				btn[1].setText("등록");
				btn[1].setVisible(true);
			} else {
				var r = rs.get(0);

				for (int i = 0; i < txt.length; i++) {
					txt[i].setText(r.get(i + 2).toString());
				}

				btn[1].setText("수정");
				btn[1].setVisible(toInt(txt[1].getText()) == 0);
			}
			btn[0].setVisible(!txt[0].getText().isEmpty());
		});

		var cap = "제품명,단가,수량".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout(10, 0));

			tmp.add(sz(lbl(cap[i], 2), 60, 20), "West");
			tmp.add(i == 0 ? com : txt[i - 1]);

			c.add(tmp);
		}

		cap = "수정,삭제".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(btn[i] = btn(cap[i], a -> {
				for (var t : txt) {
					if (t.getText().isEmpty() && !a.getActionCommand().equals("삭제")) {
						eMsg("공백이 있습니다.");
						return;
					}

					if (toInt(t.getText()) < 1) {
						eMsg("1이상의 숫자로 입력해주세요.");
						return;
					}
				}

				iMsg(a.getActionCommand() + "이 완료되었습니다.");

				var f_no = getOne("select f_no from farm where u_no = ? and b_no = ?", user.get(0), b_no);

				if (a.getActionCommand().equals("수정")) {
					execute("update farm set f_amount = ?, f_quantity = ? where f_no = ?", txt[0].getText(),
							txt[1].getText(), f_no);
				} else if (a.getActionCommand().equals("등록")) {
					execute("insert farm values(0,?,?,?,?)", user.get(0), b_no, txt[0].getText(), txt[1].getText());
				} else {
					execute("delete from farm where f_no = ?", f_no);
				}

				dispose();
			}));
		}

		btn[1].setVisible(false);

		com.setSelectedIndex(0);

		img.setBorder(new LineBorder(Color.black));

		setVisible(true);
	}

	public BaseMange(int b_no, boolean isEdit) {
		this();
		this.b_no = b_no;
		
		System.out.println(b_no);

		com.setSelectedItem(getOne("select b_name from base where b_no = ?", b_no));
	}

	public static void main(String[] args) {
		new Main();
	}
}
