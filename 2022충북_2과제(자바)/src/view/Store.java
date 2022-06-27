package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Store extends BaseFrame {
	JLabel img;

	public Store() {
		super("스토어", 900, 700);

		add(img = new JLabel(getIcon("./datafile/스토어/배경2.jpg", 900, 300)), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 3))));

		for (var rs : getRows("select s_no, s_name, s_explanation, format(s_price, '#,##0') from store")) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			var tmp_c = new JPanel(new GridLayout(0, 1));

			tmp.add(new JLabel(getIcon("./datafile/스토어/" + rs.get(1) + ".jpg", 100, 100)), "North");
			tmp.add(tmp_c);
			tmp.add(lbl(rs.get(3) + "원", 0, 15), "South");
			
			tmp_c.add(lbl(rs.get(1).toString(), 0, 15));
			tmp_c.add(lbl("<html><font color='gray'>"+rs.get(2), 0));
			
			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new BaseFrame("구매", 500, 500).setVisible(true);
				}
			});

			c.add(sz(tmp, 150, 200));
		}

		setVisible(true);
	}
}
