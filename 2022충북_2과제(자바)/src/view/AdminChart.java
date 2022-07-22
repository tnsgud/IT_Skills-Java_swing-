package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class AdminChart extends BaseFrame {
	Color col[] = { Color.black, Color.blue, Color.red, Color.green, Color.yellow };

	public AdminChart() {
		super("관리자 통계", 800, 500);

		add(lbl("영화 예매율 TOP5", 0, 20), "North");
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				int h = 250, max = 0;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var rs = getRows(
						"SELECT count(r.r_no), m.m_name FROM movie.reservation r, schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by count(*) desc, m.m_name limit 5");
				max = toInt(rs.get(0).get(0));
				for (var r : rs) {
					var i = rs.indexOf(r);
					var pr = (double) (toInt(r.get(0))) / max;

					g2.setColor(col[i]);
					g2.fillRect(40 + 100 * i, (int) (350 - (h * pr)), 50, (int) (h * pr));
					g2.fillRect(530, 180 + i * 20, 10, 10);

					g2.setColor(Color.black);
					g2.drawRect(40 + 100 * i, (int) (350 - (h * pr)), 50, (int) (h * pr));
					g2.drawString(r.get(1).toString(), 40 + 100 * i, 370);
					g2.drawString(r.get(1).toString() + " " + r.get(0) + "명", 550, 190 + i * 20);
				}
			}
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminChart();
	}
}
