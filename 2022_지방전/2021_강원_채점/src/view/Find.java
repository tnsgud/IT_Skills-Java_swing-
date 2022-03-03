package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Find extends BaseDialog {
	String[] h1 = "Name,E-mail".split(","), h2 = "Name,Id,E-mail".split(",");
	JTextField t1[] = new JTextField[2], t2[] = new JTextField[3];

	public Find() {
		super("아이디/비밀번호 찾기", 400, 700);

		add(c = new JPanel(new GridLayout(0, 1)));

		for (int i = 0; i < h1.length; i++) {
			var p = new JPanel(new GridLayout(0, 1, 10, 10));
			var btn = btn("계속", a -> {
				var name = toInt(((JButton) a.getSource()).getName()) == 0;

				Stream.of(name ? t1 : t2).forEach(t -> {
					if (t.getText().isEmpty()) {
						eMsg("공란을 확인해주세요.");
						return;
					}
				});

				var rs = rs("select " + (name ? "id" : "pwd") + " from user where name=? and " + (name ? "" : "id=? and")
						+ " email=?", Stream.of((toInt(((JButton) a.getSource()).getName()) < 1 ? t1 : t2)).map(e->e.getText()).toArray());

				try {
					if (rs.next()) {
						iMsg("귀하의 " + (name ? "id" : "id에 PW") + "는 " + rs.getString(1)+"입니다.");
						Stream.of((toInt(((JButton) a.getSource()).getName()) < 1 ? t1 : t2)).forEach(e->e.setText(""));
					} else {
						eMsg("존재하지 않는 정보입니다.");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			p.add(lbl((i == 0 ? "아이디" : "비밀번호") + " 찾기", 2, 35), "North");
			for (int j = 0; j < (i < 1 ? h1 : h2).length; j++) {
				p.add((i < 1 ? t1 : t2)[j] = new JHintField((i < 1 ? h1 : h2)[j], 15));
				(i < 1 ? t1 : t2)[j].setName(j + "");
			}
			btn.setName(i + "");
			p.add(btn);
			c.add(p);
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Find();
	}
}
