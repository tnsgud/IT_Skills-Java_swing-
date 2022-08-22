package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	ArrayList<ArrayList<Object>> rs;
	int redX = 0, redY = 0;

	public Chart(int bno) {
		super("차트", 600, 500);

		var data = getRows("select * from base where b_no = ?", bno).get(0);
		rs = getRows(
				"select f_no, concat(c_name, t_name), u_name, f_amount from farm f, user u, town t, city c where f.u_no = u.u_no and u.t_no = t.t_no and t.c_no = c.c_no and f.b_no = ? order by f_amount asc limit 5",
				bno);

		add(lbl(data.get(2) + " 가격 비교", 0, 0, 30), "North");
		add(event(new JLabel(getIcon(data.get(5), 600, 400)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
				for (int i = 1; i < x.length; i++) {
					g2.drawLine(x[i - 1], y[i - 1], x[i], y[i]);
				}

				for (int i = 0; i < x.length; i++) {
					g2.setColor(i == 0 ? Color.red : Color.blue);
					g2.fillOval(x[i] - 5, y[i] - 5, 10, 10);

					g2.setColor(Color.black);
					g2.drawString(rs.get(i).get(3).toString(), x[i] - 10, y[i] - 10);
				}

				redX = x[0];
				redY = y[0];
			}
		}, e -> {
			System.out.println(redX+","+redY);
			System.out.println(e.getX() +","+e.getY());
			if (redX-5 <= e.getX() && e.getX() <= redX + 10 && redY-5 <= e.getY() && e.getY() <= redY + 10) {
				new Purchase(toInt(rs.get(0).get(0))).addWindowListener(new Before(this));
			}
		}));
		add(s = new JPanel(new GridLayout(0, 5)), "South");

		for (var r : rs) {
			s.add(lbl("<html>" + r.get(1) + "<br>" + r.get(2), 2, 15));
		}

		setVisible(true);
	}
}
