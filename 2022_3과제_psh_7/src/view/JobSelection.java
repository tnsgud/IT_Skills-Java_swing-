package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
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
	JTextField txt = new JTextField(17);

	public JobSelection(JTextField jTextField) {
		super("직종선택", 300, 350);

		add(c = new JPanel(new GridLayout(0, 2)));
		add(sz(s = new JPanel(), 300, 80), "South");

		txt.setEnabled(false);

		for (int i = 0; i < category.length - 1; i++) {
			c.add(chk[i] = new JCheckBox(category[i + 1]));
			chk[i].addActionListener(a -> {
				txt.setText(String.join(",",
						Stream.of(chk).filter(JCheckBox::isSelected).map(JCheckBox::getText).toArray(String[]::new)));
			});
		}
		s.add(lbl("선택직종명", 0, 15));
		s.add(txt);
		s.add(btn("선택", a -> {
			if(txt.getText().isEmpty()) {
				eMsg("직종을 선택하세요.");
				return;
			}
			
			jTextField.setText(txt.getText());
			dispose();
		}));

		c.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(new LineBorder(Color.black),
				"직종선택", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("", 1, 20))));

		setVisible(true);
	}

	public static void main(String[] args) {
		new JobSelection(new JTextField());
	}
}
