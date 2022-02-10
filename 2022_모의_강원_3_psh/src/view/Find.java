package view;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Find extends BaseDialog {
	String[] cap = "아이디,비밀번호".split(","), h = "Name,E-mail,Name,Id,E-mail".split(",");
	JHintField[] idTxt = new JHintField[2], pwTxt = new JHintField[3];

	public Find() {
		super("아이디/비밀번호찾기", 400, 600);

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
				var list = new ArrayList<String>();
				var name = toInt(((JButton) a.getSource()).getName()) == 0;

				for (var t : name ? idTxt : pwTxt) {
					if (t.getText().isEmpty()) {
						eMsg("공란을 확인해주세요.");
						return;
					}

					list.add(t.getText());
				}

				var r = getOne("select " + (name ? "id" : "pwd") + " from user where name=? "
						+ (name ? "" : " and id=? ") + "and email=?", list);
				if (r.isEmpty()) {
					eMsg("존재하지 않는 정보입니다.");
					return;
				}

				iMsg("귀하의 id" + (name ? "는 " : "에 PW는 ") + r + "입니다.");
			});
			b.setName(i + "");
			p.add(b);
			add(p);
		}
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(40, 40, 40, 40));
	}
}
