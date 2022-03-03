package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class QueuePage extends BasePage {
	public QueuePage() {
		ui();
	}
	void ui() {
		var n = new JPanel(new BorderLayout());
		var c = new JPanel(new BorderLayout());

		add(n, "North");
		add(c);

		n.add(lbl("지금 재생중", JLabel.LEFT, 15), "North");

		c.add(lbl("다음에 재생될 항목", JLabel.LEFT, 15), "North");
		n.add(cur_t);
		c.add(que_t);
		n.setOpaque(false);
		c.setOpaque(false);
	}

}
