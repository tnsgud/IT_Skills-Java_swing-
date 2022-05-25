package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	JPanel chart;
	JComboBox<String> com;

	public Chart() {
		super("지원자 분석", 650, 400);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(chart = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				var rs = getResults(
						"select sum(if(year(now()) - year(u_birth) + 1 >= 10 and year(now()) - year(u_birth) + 1 < 20, 1, 0)), sum(if(year(now()) - year(u_birth) + 1 >= 20 and year(now()) - year(u_birth) + 1 < 30, 1, 0)), sum(if(year(now()) - year(u_birth) + 1 >= 30 and year(now()) - year(u_birth) + 1 < 40, 1, 0)), sum(if(year(now()) - year(u_birth) + 1 >= 40 and year(now()) - year(u_birth) + 1 < 50, 1, 0)), sum(if(year(now()) - year(u_birth) + 1 >= 50 and year(now()) - year(u_birth) + 1 < 60, 1, 0)) from applicant a, user u, employment e, company c where a.e_no=e.e_no and u.u_no=a.u_no and c.c_no=e.c_no and c_name = ? group by e.e_no",
						com.getSelectedItem());
				g2.setFont(new Font("HY헤드라인M", 0, 25));
				g2.drawString("회사별 지원자 (연령별)", 100, 30);

				if(rs.isEmpty()) {
					eMsg("지원자 또는 공고가 없습니다.");
					com.setSelectedIndex(0);
				}
				var r = rs.get(0);

				int max = 0;
				for (var i : r) {
					max = toInt(i) > max ? toInt(i) : max;
				}

				var cap = "10,20,30,40,50".split(",");
				var col = new Color[] { Color.black, Color.blue, Color.red, Color.green, Color.yellow };
				g2.setFont(new Font("", 0, 13));
				int width = 50, height = 250, baseY = 300;
				for (int i = 0; i < cap.length; i++) {
					var pr = (double) toInt(r.get(i)) / max;
					g2.drawString(cap[i] + "대", 50 + i * 100, 320);

					g2.setColor(col[i]);
					g2.fillRect(40 + 100 * i, baseY - (int) (height * pr), width, (int) (height * pr));
					g2.fillRect(500, 150 + i * 20, 15, 15);
					g2.setColor(Color.black);
					g2.drawRect(40 + 100 * i, baseY - (int) (height * pr), width, (int) (height * pr));
					g2.drawString(cap[i] + "대:" + r.get(i) + "명", 520, 160 + i * 20);
				}
			}
		});
		chart.add(n = new JPanel(new FlowLayout(2)), "North");

		var rs = getResults("select c_name from company c, employment e where e.c_no=c.c_no");
		n.add(com = new JComboBox<>());
		for (var r : rs) {
			com.addItem(r.get(0) + "");
		}

		com.addActionListener(a -> {
			chart.revalidate();
			chart.repaint();
		});

		n.setOpaque(false);

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Chart();
	}
}
