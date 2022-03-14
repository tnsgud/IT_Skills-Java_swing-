package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
	JTextField txt = new JTextField(20);

	public JobSelection(JTextField JobsTxt) {
		super("직종선택", 350, 400);

		setLayout(new BorderLayout(10, 10));

		add(c = new JPanel(new GridLayout(0, 2)));
		add(sz(s = new JPanel(new FlowLayout(1)), 350, 100), "South");

		for (int i = 0; i < chk.length; i++) {
			c.add(chk[i] = new JCheckBox(category[i + 1]));
			chk[i].addActionListener(a -> {
				txt.setText("");
				txt.setText(String.join(",",
						Stream.of(chk).filter(JCheckBox::isSelected).map(c -> c.getText()).toArray(String[]::new)));
			});
		}

		s.add(lbl("선택직종명", 0, 15));
		s.add(txt);
		s.add(btn("선택", a -> {
			if(txt.getText().isEmpty()) {
				eMsg("직종을 선택하세요.");
				return;
			}
			
			JobsTxt.setText(txt.getText());
			dispose();
		}));

		txt.setEnabled(false);		
		c.setBorder(
				new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder(new LineBorder(Color.black), "직종선택")));

		setVisible(true);
	}
}
