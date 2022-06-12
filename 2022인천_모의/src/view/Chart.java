package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Chart extends BasePage{
	JPanel n, c;
	JPanel chart;
	JLabel title;
	JComboBox<String> com;
	Color col[] = { Color.red, Color.ORANGE, Color.yellow, Color.green, Color.BLUE };

	double arc = 90;

	public Chart() {
		setLayout(new BorderLayout());
		ui();
	}

	public void ui() {
		add(n = new JPanel(new BorderLayout()), "North");
		c = new JPanel();
		setChart("select v.name, count(*) from purchase p, vaccine v where p.vaccine=v.no group by v.no",
				toInt(getOne("select count(*) from purchase p, vaccine v where p.vaccine=v.no")));

		n.add(title = new JLabel());
		n.add(com = new JComboBox<String>("상위 백신,상위 병원,상위 진료소".split(",")), "East");

		com.addActionListener(e -> {
			remove(c);

			if (com.getSelectedIndex() == 0) {
				setChart("select v.name, count(*) from purchase p, vaccine v where p.vaccine=v.no group by v.no",
						toInt(getOne("select count(*) from purchase p, vaccine v where p.vaccine=v.no")));
			} else if (com.getSelectedIndex() == 1) {
				int sum = 0;
				String sql = "select b.name, count(*) from building b, purchase p where p.building = b.no and type=1 group by b.no order by count(*) desc limit 5";
				for (var r : getRows(sql)) {
					sum += toInt(r.get(1));
				}
				setChart(sql, sum);
			} else if (com.getSelectedIndex() == 2) {
				int sum = 0;
				String sql = "select b.name, count(*) from building b, purchase p where p.building = b.no and type=0 group by b.no order by count(*) desc limit 5";
				for (var r : getRows(sql)) {
					sum += toInt(r.get(1));
				}
				setChart(sql, sum);
			}
		});

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public void setChart(String sql, double sum) {
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int height = 250;
				arc = 90;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var rs = getRows(sql);
				for (var r : rs) {
					var a = ((double) toInt(r.get(1)) / (double) sum * 360) * -1;

					g2d.setColor(col[rs.indexOf(r)]);
					var arc2d = new Arc2D.Float(Arc2D.PIE);
					arc2d.setFrame(300, 100, 300, 300);
					arc2d.setAngleStart(arc);
					arc2d.setAngleExtent(a);
					int midx = (int) (arc2d.getEndPoint().getX() + arc2d.getStartPoint().getX()) / 2;
					int midy = (int) (arc2d.getEndPoint().getY() + arc2d.getStartPoint().getY()) / 2;
					g2d.draw(arc2d);
					g2d.fill(arc2d);
					g2d.fillOval(800, height - 15, 20, 20);
					g2d.setColor(Color.BLACK);
					g2d.drawString(r.get(0).toString(), 825, height);
					g2d.drawString(String.format("%.1f", ((double) toInt(r.get(1)) / (double) sum) * 100) + "%", midx,
							midy);
					arc += a;
					height += 25;
				}
			}
		});

		repaint();
		revalidate();
	}
}