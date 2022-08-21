package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Store extends BaseFrame {
	public Store() {
		super("스토어", 900, 700);

		add(new JLabel(getIcon("./datafile/스토어/배경2.jpg", 900, 250)), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 3))));

		for (var rs : getRows("select s_no, s_name, s_explanation, format(s_price, '#,##0') from store")) {
			var tmp = event(new JPanel(new BorderLayout()), e -> {
				new Purchase(toInt(rs.get(0))).addWindowListener(new Before(this));
			});
			var tmp2 = new JPanel(new GridLayout(0, 1));
			var l = lbl(rs.get(2).toString(), 0);

			tmp2.add(lbl(rs.get(1).toString(), 0, 15));
			tmp2.add(l);
			tmp2.add(lbl(rs.get(3) + "원", 0, 15));

			l.setForeground(Color.LIGHT_GRAY);

			tmp.add(new JLabel(getIcon("./datafile/스토어/" + rs.get(1).toString() + ".jpg", 250, 200)));
			tmp.add(tmp2, "South");

			c.add(sz(tmp, 250, 300));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Store();
	}
}
