package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class Chart extends BaseFrame {
	int fNo, redX, redY;

	public Chart(int bNo) {
		super("차트", 600, 400);

		add(lbl(getOne("select b_name from base where b_no = ?", bNo), 0, 0, 20), "North");
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var rs = getRows(
						"select * from farm f, base b where b.b_no = f.b_no and f.b_no = ? order by f_amount limit 5",
						bNo);
				int max = rs.stream().mapToInt(a -> toInt(a.get(3))).max().getAsInt(), h = 200;
				int[] x = new int[5], y = new int[5];

				for (var r : rs) {
					int i = rs.indexOf(r);
					var pr = toInt(r.get(3)) / (double) max;

					x[i] = i * 120 + 50;
					y[i] = (h - (int) (pr * h)) + 50;
				}

				g2.setColor(Color.blue);
				g2.setStroke(new BasicStroke(3));
				g2.drawPolygon(x, y, x.length);

				for (int i = 0; i < x.length; i++) {
					g2.setColor(i == 0 ? Color.red : Color.blue);
					g2.fillOval(x[i] - 5, y[i] - 5, 10, 10);

					g2.setColor(Color.black);
					g2.drawString(rs.get(i).get(3).toString(), x[i] - 10, y[i] - 10);
				}

				redX = x[0] - 5;
				redY = y[0] - 5;
			}
		});
		add(s = new JPanel(new GridLayout(0, 5)), "South");
		
		

		setVisible(true);
	}
}
