package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import db.DB;

public class Find extends BaseDialog {
	String[] cap = "아이디,비밀번호".split(","), h = "Name,E-mail,Name,Id,E-mail".split(",");
	JTextField[] idTxt = new JTextField[2], pwTxt = new JTextField[3];

	public Find() {
		super("아이디/비밀번호 찾기", 600, 800);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 20, 20));
		add(n = new JPanel(new BorderLayout()));
		add(c = new JPanel(new BorderLayout()));
		for (int i = 0, idx = 0; i < cap.length; i++) {
			var p = new JPanel(new GridLayout(0, 1, 20, 20));
			p.add(BaseFrame.lbl(cap[i] + " 찾기", 2, 35));
			for (int j = 0; j < (i == 0 ? idTxt : pwTxt).length; j++, idx++) {
				var t = (i == 0 ? idTxt : pwTxt)[j] = BaseFrame.txt(JTextField.class, 15, h[idx]);
				t.setName(h[idx]);
				p.add(t);
			}
			var b = BaseFrame.btn("계속", a -> {
				var obj = new ArrayList<String>();
				var name = BaseFrame.toInt(((JButton) a.getSource()).getName());
				for (var t : name == 0 ? idTxt : pwTxt) {
					if (t.getText().equals(t.getName()) || t.getText().isEmpty()) {
						BaseFrame.eMsg("공란을 확인해주세요.");
						return;
					} else {
						obj.add(t.getText());
					}
				}

				var result = DB.getOne("select " + (name == 0 ? "id" : "pwd") + " from user where name=? "
						+ (name == 0 ? "" : "and id=?") + " and email=?", obj.toArray());

				if (result == null) {
					BaseFrame.eMsg("존재하지 않는 정보입니다.");
					return;
				} else {
					BaseFrame.iMsg("귀한의 id" + (name == 0 ? "는 " : "에 PW는 ") + result + "입니다.");
				}
			});
			b.setName(i + "");
			p.add(b);
			(i == 0 ? n : c).add(p);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(40, 50, 40, 50));
	}
}
