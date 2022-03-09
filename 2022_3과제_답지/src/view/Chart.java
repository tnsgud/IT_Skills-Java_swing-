package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Chart extends BaseFrame {

	JPanel chart;
	JComboBox<String> box;

	public Chart() {
		super("차트", 650, 400);
		add(chart = new JPanel(new BorderLayout()) {

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;
				System.out.println("called");
				var rs = getResults("SELECT \r\n"
						+ "   sum(if(year(now()) - year(u_birth) + 1 >= 10 and year(now()) - year(u_birth) + 1 < 20,1,0)) one, \r\n"
						+ "   sum(if(year(now()) - year(u_birth) + 1 >= 20 and year(now()) - year(u_birth) + 1 < 30,1,0)) two,\r\n"
						+ "   sum(if(year(now()) - year(u_birth) + 1 >= 30 and year(now()) - year(u_birth) + 1 < 40,1,0)) three,\r\n"
						+ "   sum(if(year(now()) - year(u_birth) + 1 >= 40 and year(now()) - year(u_birth) + 1 < 50,1,0)) four,\r\n"
						+ "   sum(if(year(now()) - year(u_birth) + 1 >= 50 and year(now()) - year(u_birth) + 1 < 60,1,0)) five \r\n"
						+ "FROM\r\n" + "    applicant a,\r\n" + "    user u,\r\n" + "    employment e,\r\n"
						+ "    company c\r\n" + "WHERE\r\n"
						+ "    a.e_no = e.e_no AND a.u_no = u.u_no AND c.c_no = e.c_no and c.c_name = ? group by e.e_no",
						box.getSelectedItem() + "");
				g2.setFont(new Font("HY헤드라인M", 0, 25));
				g2.drawString("회사별 지원자(연령별)", 100, 30);

				var r = rs.get(0);

				int max = 0;
				for (int i = 0; i < r.size(); i++) {
					if (max < toInt(r.get(i))) {
						max = toInt(r.get(i));
					}
				}

				String cap[] = "10,20,30,40,50".split(",");
				Color colors[] = { Color.black, Color.blue, Color.red, Color.green, Color.yellow };
				g2.setFont(new Font("", 0, 13));
				int width = 50, height = 250, baseY = 300;
				for (int i = 0; i < cap.length; i++) {
					var pr = (double) toInt(r.get(i)) / max;
					System.out.println(pr);
					g2.setColor(Color.BLACK);
					g2.drawString(cap[i] + "대", 50 + i * 100, 320);
					// chart
					g2.setColor(colors[i]);
					g2.fillRect(40 + 100 * i, baseY - (int) (height * pr), width, (int) (height * pr));
					g2.fillRect(500, 150 + i * 20, 15, 15);
					g2.setColor(Color.BLACK);
					g2.drawRect(40 + 100 * i, baseY - (int) (height * pr), width, (int) (height * pr));
					g2.drawString(cap[i] + "대:" + r.get(i) + "명", 520, 160 + i * 20);

				}

			}

		});
		chart.add(n = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "North");

		var rs = getResults("select c_name from company c, employment e where c.c_no = e.c_no");
		n.add(box = new JComboBox<String>());
		for (var r : rs) {
			box.addItem(r.get(0) + "");
		}

		box.addItemListener(i -> {
			chart.revalidate();
			chart.repaint();
		});

		n.setOpaque(false);

		revalidate();
		repaint();

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
