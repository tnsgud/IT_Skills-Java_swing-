import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class Chart extends BaseFrame {

	int[] data = { 10, 20, 30, 40 };
	int[] arcAngle = new int[4];
	String cap[] = "미접종,1차 접종,2차 접종,확진자".split(",");
	Color color[] = new Color[4];
	int r = 255, g = 0, b = 0;

	public Chart() {
		for (int i = 0; i < color.length; i++, b += 30, g += 60) {
			color[i] = new Color(r, g, b);
		}

		var chart = new ChartPanel();
		setLayout(new GridLayout(0, 1));
		add(chart);

		var p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;

				for (int i = 0; i < color.length; i++) {
					g2.setColor(color[i]);
					g2.fillRect(50 + (i * 100), 100, 10, 10);

					g2.setColor(Color.black);
					g2.drawString(cap[i], 70 + (i * 100), 110);
				}
			}
		};

		add(p);

		p.repaint();

		int sum = 0;

		for (int i : data) {
			sum += i;
		}

		for (int i = 0; i < arcAngle.length; i++) {
			arcAngle[i] = (int) Math.round((double) data[i] / (double) sum * 360);
			chart.repaint();
		}

		setVisible(true);
	}

	class ChartPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;

			int startAngle = 0;

			for (int i = 0; i < data.length; i++) {
				g2.setColor(color[i]);
				g2.setStroke(new BasicStroke(10));
				g2.drawArc(150, 50, 200, 200, startAngle, -arcAngle[i]);
				startAngle = startAngle - arcAngle[i];
			}
		}
	}

	public static void main(String[] args) {
		new Chart();
	}
}
