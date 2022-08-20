package view;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class KnockCode extends BaseFrame {
	String num = "";

	public KnockCode(JTextField jTextField) {
		super("", 500, 500);

		add(c = new JPanel(new GridLayout(0, 3)));
		add(s = new JPanel(), "South");

		for (int i = 0; i < 9; i++) {
			var l = lbl("●", 0, 1, 30, e -> {
				num += ((JLabel) e.getSource()).getName();
			});

			l.setName(i + 1 + "");
			l.setBorder(new LineBorder(Color.LIGHT_GRAY));
			
			c.add(l);
		}

		s.add(btn("확인", a -> {
			jTextField.setText(num);
			dispose();
		}));

		setVisible(true);
	}
}
