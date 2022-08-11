package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class Seat extends BaseFrame {
	HashMap<String, JLabel> seat = new HashMap<>();

	public Seat() {
		super("좌석배정", 1000, 600);

		BasePage.peoples.forEach(p -> p.seat = null);

		add(new JScrollPane(c));
		add(e = sz(new JPanel(), 200, 600), "East");

		c.add(cw = new JPanel(new FlowLayout(2, 5, 5)), "West");
		c.add(cc = new JPanel(new FlowLayout(1, 5, 5)));
		c.add(ce = new JPanel(new FlowLayout(0, 5, 5)), "East");

		var code = "A,B,C,D,E,F,G".split(",");
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 9; j++) {
				var tmp = j < 3 ? cw : j < 6 ? cc : ce;
				JLabel lbl = null;

				if (j == 0 || j == 8) {
					lbl = lbl(i == 0 ? "" : i + "", 0);
				} else if (i == 0) {
					lbl = lbl(code[j - 1], 0);
				} else {
					lbl = lbl(code[j - 1] + i, 0);

					lbl.setOpaque(true);
					lbl.setBackground(Color.white);
					lbl.setBorder(new LineBorder(Color.gray));

					lbl.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
						}
					});

					seat.put(code[j - 1] + i, lbl);
				}

				tmp.add(sz(lbl, j == 0 || j == 8 ? 35 : 80, i == 0 ? 20 : 80));
			}
		}

		setVisible(true);
	}
}
