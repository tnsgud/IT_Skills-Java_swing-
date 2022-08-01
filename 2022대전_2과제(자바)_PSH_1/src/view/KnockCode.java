package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class KnockCode extends BaseFrame {
	String num = "";

	public KnockCode(JTextField txt) {
		super("노크코드", 500, 500);

		add(c = new JPanel(new GridLayout(0, 3)));
		add(s = new JPanel(), "South");

		for (int i = 0; i < 9; i++) {
			var l = lbl("●", 0, 30);

			l.setName(i + 1 + "");
			l.setBorder(new LineBorder(Color.LIGHT_GRAY));

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					num += l.getName();
				}
			});

			c.add(l);
		}

		s.add(btn("확인", a -> {
			txt.setText(num);
			dispose();
		}));

		setVisible(true);
	}
}
