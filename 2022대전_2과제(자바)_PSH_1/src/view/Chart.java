package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	int fNo, redX, redY;

	public Chart(int bNo) {
		super("차트", 600, 400);

		add(lbl(getOne("select b_name from base where b_no = ?", bNo), 0, 0, 20), "North");
		add(c = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				var img = new ImageIcon((byte[]) getRows("select b_img from base where b_no = ?", bNo).get(0).get(0));
				g2.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);

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
				for (int i = 1; i < x.length; i++) {
					g2.setColor(Color.blue);
					g2.drawLine(x[i - 1], y[i - 1], x[i], y[i]);
				}

				for (int i = 0; i < x.length; i++) {
					g2.setColor(i == 0 ? Color.red : Color.blue);
					g2.fillOval(x[i] - 5, y[i] - 5, 10, 10);

					g2.setColor(Color.black);
					g2.drawString(rs.get(i).get(3).toString(), x[i] - 10, y[i] - 10);

				}

				fNo = toInt(rs.get(0).get(0));
				redX = x[0] - 5;
				redY = y[0] - 5;
			}
		});
		add(s = new JPanel(new GridLayout(0, 5)), "South");

		for (var rs : getRows(
				"select concat(c.c_name, '', t.t_name), u.u_name from farm f, base b, user u, town t, city c where b.b_no = f.b_no and f.u_no = u.u_no and u.t_no = t.t_no and t.c_no = c.c_no and f.b_no = ? order by f_amount limit 5",
				bNo)) {
			s.add(lbl("<html>" + rs.get(0) + "<br>" + rs.get(1), 2, 13));
		}

		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (redX <= e.getX() && e.getX() <= redX + 10 && redY <= e.getY() && e.getY() <= redY + 10) {
					new Purchase(fNo).addWindowListener(new Before(Chart.this));
				}
			}
		});

		setVisible(true);
	}
}
