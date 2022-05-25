package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class JobSelection extends BaseFrame {

	JCheckBox box[] = new JCheckBox[category.length - 1];
	JTextField txt, jobstxt;
	ArrayList<String> cate = new ArrayList<>();

	public JobSelection(JTextField jobstxt) {
		super("직종선택", 500, 500);
		this.jobstxt = jobstxt;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 2)));
		add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < box.length; i++) {
			c.add(box[i] = new JCheckBox(category[i+1]));
			box[i].addItemListener(e -> {
				cate.clear();
				for (var b : box) {
					if (b.isSelected()) {
						cate.add(b.getText());
					}
				}

				txt.setText(String.join(",", cate.toArray(String[]::new)));
			});
		}

		s.add(sc = new JPanel());
		s.add(ss = new JPanel(), "South");

		sc.add(lblH("선택직종명", 0, 1, 15));
		sc.add(txt = new JTextField(20));

		ss.add(btn("선택", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("직족을 선택하세요.");
				return;
			}
			jobstxt.setText(txt.getText());
			dispose();
		}));

		c.setBorder(new TitledBorder(new LineBorder(Color.black), "직종선택", TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION, new Font("", 1, 20)));
	}
}
