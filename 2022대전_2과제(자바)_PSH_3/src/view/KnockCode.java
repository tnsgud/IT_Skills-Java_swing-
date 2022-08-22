package view;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class KnockCode extends BaseFrame {
	String code = "";

	public KnockCode(JTextField txt) {
		super("노크코드", 500, 500);

		add(c = new JPanel(new GridLayout(0, 3)));
		add(s = new JPanel(), "South");

		for (int i = 0; i < 9; i++) {
			var l = event(lbl("●", 0, 30), e -> {
				code += ((JLabel) e.getSource()).getName();
			});
			l.setBorder(new LineBorder(Color.black));
			l.setName(i + 1 + "");
			c.add(l);
		}

		s.add(btn("확인", a -> {
			txt.setText(code);
			dispose();
		}));

		setVisible(true);
	}
}
