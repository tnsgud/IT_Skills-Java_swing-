package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import db.DB;
import tool.Tool;

public class Find extends BaseDialog implements Tool {
	String[] cap = "아이디,비밀번호".split(","), h = "Name,E-mail,Name,Id,E-mail".split(",");
	JTextField[] idTxt = new JTextField[2], pwTxt = new JTextField[3];

	public Find() {
		super("아이디/비밀번호 찾기", 400, 600);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 50, 50));

		for (int i = 0, idx = 0; i < cap.length; i++) {
			var p = new JPanel(new GridLayout(0, 1, 10, 10));
			p.add(lbl(cap[i] + " 찾기", 2, 25));
			for (int j = 0; j < (i == 0 ? 2 : 3); j++, idx++) {
				var t = (i == 0 ? idTxt : pwTxt)[j] = new JHintField(15, h[idx]);
				p.add(t);
			}

			var b = btn("계속", a -> {
				var obj = new ArrayList<String>();
				var name = toInt(((JButton) a.getSource()).getName());

				for (var t : name == 0 ? idTxt : pwTxt) {
					if (t.getText().isEmpty()) {
						eMsg("공란을 확인해주세요.");
						return;
					}

					obj.add(t.getText());
				}

				var r = DB.getOne("select " + (name == 0 ? "id" : "pwd") + " from user where name=? "
						+ (name == 0 ? "" : " and id=? ") + "and email=?", obj);
				if (r.isEmpty()) {
					eMsg("존재하지 않는 정보입니다.");
					return;
				}

				iMsg("귀하의 id" + (name == 0 ? "는 " : "에 PW는 " + r + "입니다."));
			});
			b.setName(i + "");
			p.add(b);
			add(p);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(50, 40, 50, 40));
	}
}
