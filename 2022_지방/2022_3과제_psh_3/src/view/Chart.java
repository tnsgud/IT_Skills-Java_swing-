package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	JPanel chart;
	JComboBox com;

	public Chart() {
		super("", 650, 400);

		add(chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2 = (Graphics2D) g;

				var rs = rs(
						"select c.c_no, sum(if(year(now()) - year(u_birth) +1 >= 10 and year(now()) - year(u_birth) < 20, 1, 0)) as one, sum(if(year(now()) - year(u_birth) +1 >= 20 and year(now()) - year(u_birth) < 30, 1, 0)) as two, sum(if(year(now()) - year(u_birth) +1 >= 30 and year(now()) - year(u_birth) < 40, 1, 0)) as three, sum(if(year(now()) - year(u_birth) +1 >= 40 and year(now()) - year(u_birth) < 50, 1, 0)) as four, sum(if(year(now()) - year(u_birth) +1 >= 50 and year(now()) - year(u_birth) < 60, 1, 0)) as five from user u left outer join applicant a on u.u_no=a.u_no inner join employment e on e.e_no=a.e_no right outer join company c on c.c_no=e.c_no where c_name =? group by c.c_no", com.getSelectedItem());
				g2.setFont(new Font("HY헤드라인M", 0, 25));
				g2.drawString("회사별 지원자 (연령별)", 100, 30);

				if (rs.isEmpty()) {
					eMsg("지원자 또는 공고가 없습니다."); 
					com.setSelectedIndex(0);
					return;
				}

				var r = rs.get(0);

				int max = 0;
				for (var i : r) {
					max = toInt(i) > max ? toInt(i) : max;
				}

				var cap = "10,20,30,40,50".split(",");
				var col = new Color[] { Color.black, Color.blue, Color.red, Color.green, Color.yellow };
				g2.setFont(new Font("", 0, 13));
				int w = 50, h = 250, baseY = 300;
				for (int i = 0; i < cap.length; i++) {
					var pr = (double) toInt(r.get(i)) / max;
					g2.drawString(cap[i] + "대", 50 + i * 100, 320);

					g2.setColor(col[i]);
					g2.fillRect(40 + 100 * i, baseY - (int) (h * pr), w, (int) (h * pr));
					g2.fillRect(500, 150 + i * 20, 15, 15);
					g2.setColor(Color.black);
					g2.drawRect(40 + i * 100, baseY - (int) (h * pr), w, (int) (h * pr));
					g2.drawString(cap[i]+"대:"+r.get(i)+"명", 520, 160+i*20);
				}
			}
		});

		chart.add(n = new JPanel(new FlowLayout(2)), "North");

		n.add(com = new JComboBox<>(rs("select c_name from company c, employment e where e.c_no=c.c_no").stream()
				.flatMap(a -> a.stream()).toArray(String[]::new)));

		com.addActionListener(a -> {
			chart.repaint();
			chart.revalidate();
		});

		n.setOpaque(false);

		repaint();
		revalidate();

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
