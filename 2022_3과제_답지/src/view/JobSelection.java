package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class JobSelection extends BaseFrame {

	JCheckBox box[] = new JCheckBox[category.length - 1];
	JTextField txt;

	String cate = "";

	public JobSelection(Jobs d) {
		super("직종선택", 400, 400);
		add(c = new JPanel(new GridLayout(0, 2)));
		add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < box.length; i++) {
			box[i] = new JCheckBox(category[i + 1]);
			c.add(box[i]);
			box[i].addItemListener(evt -> {

				txt.setEnabled(true);
				for (int j = 0; j < box.length; j++) {
					if (box[j].isSelected()) {
						txt.setEnabled(false);
					}
				}
				
				cate = String.join(",",
						Arrays.stream(box).filter(a -> a.isSelected()).map(a -> a.getText()).toArray(String[]::new));
				txt.setText(cate);

			});
		}

		s.add(sc = new JPanel(new FlowLayout()));
		s.add(ss = new JPanel(new FlowLayout()), "South");

		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "직종선택", TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION, new Font("", Font.BOLD, 20)));

		sc.add(crt_lbl("선택직종명", JLabel.CENTER, "HY헤드라인M", Font.BOLD, 15));
		sc.add(txt = new JTextField(20));

		txt.setFocusable(false);

		ss.add(sz(crt_evt_btn("선택", a -> {
			d.txt[1].setText(cate);
			dispose();
		}), 100, 30));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	public static void main(String[] args) {
	}
}
