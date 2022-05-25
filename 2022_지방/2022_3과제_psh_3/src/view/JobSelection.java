package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class JobSelection extends BaseFrame {
	JCheckBox chk[] = new JCheckBox[8];
	JTextField txt = new JTextField(12);

	public JobSelection(JTextField t) {
		super("직종선택", 250, 400);

		add(c = new JPanel(new GridLayout(0, 2)));
		add(sz(s = new JPanel(), 250, 80), "South");

		var cap = "편의점,영화관,화장품,음식점,백화점,의류점,커피전문점,은행".split(",");
		for (int i = 0; i < cap.length; i++) {
			c.add(chk[i] = new JCheckBox(cap[i]));
			chk[i].addActionListener(a -> {
				txt.setText("");
				txt.setText(String.join(",",
						Stream.of(chk).filter(JCheckBox::isSelected).map(c -> c.getText()).toArray(String[]::new)));
			});
		}

		txt.setEnabled(false);

		c.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "직종선택", TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION, new Font("", 1, 25)), new EmptyBorder(5, 5, 5, 5)));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		s.add(lbl("선택직종명", 2));
		s.add(txt);
		s.add(btn("선택", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("직종을 선택하세요.");
				return;
			}

			t.setText(txt.getText());
			dispose();
		}));

		if (!t.getText().isEmpty()) {
			txt.setText(String.join(",",
					Stream.of(chk).filter(c -> Arrays.asList(t.getText().split(",")).contains(c.getText())).map(c -> {
						c.setSelected(true);
						return c.getText();
					}).toArray(String[]::new)));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Jobs();
	}
}
