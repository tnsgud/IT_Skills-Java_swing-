package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	JComboBox com = new JComboBox(getRows("select m_name from movie where m_show=1").stream()
			.map(a -> a.get(0).toString()).collect(Collectors.joining(",")).split(","));
	int sum, arc;
	Color col[] = { new Color(70, 100, 155), new Color(215, 85, 135) };
	JPanel chart;

	public Chart() {
		super("통계", 700, 500);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(com);

		com.addActionListener(a -> {
			setChart();
		});
		
		setChart();

		setVisible(true);
	}

	private void setChart() {
		c.removeAll();

		c.add(chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				int height = 250;
				sum = toInt(getRows(
						"SELECT count(*) FROM movie.reservation r, schedule sc, movie m, user u where r.u_no = u.u_no and r.sc_no = sc.sc_no and sc.m_no = m.m_no and m.m_name = ?",
						com.getSelectedItem()).get(0).get(0));
				arc = 90;
				if (sum == 0) {
					eMsg("예매 내역이 없습니다.");
					com.setSelectedIndex(0);
					return;
				}
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				for (var r : getRows(
						"SELECT count(*), if(u.u_gender=0, '남자', '여자') FROM movie.reservation r, schedule sc, movie m, user u where r.u_no = u.u_no and r.sc_no = sc.sc_no and sc.m_no = m.m_no and m.m_name = ? group by u.u_gender order by u.u_gender",
						com.getSelectedItem().toString())) {
					var a = ((double) toInt(r.get(0)) / (double) sum * 360) * -1;
					var arc2d = new Arc2D.Float(Arc2D.PIE);

					arc2d.setFrame(150, 100, 300, 300);
					arc2d.setAngleStart(arc);
					arc2d.setAngleExtent(a);
					if (r.get(1).toString().equals("남자")) {
						g2d.setColor(col[0]);
					} else {
						g2d.setColor(col[1]);
					}
					g2d.draw(arc2d);
					g2d.fill(arc2d);
					g2d.fillRect(550, height - 15, 10, 10);
					g2d.setColor(Color.black);
					g2d.drawString(r.get(1).toString() + " : " + r.get(0) + "명", 565, height - 5);

					arc += a;
					height += 25;
				}
			}
		});

		chart.setOpaque(false);

		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new Chart();
	}
}
