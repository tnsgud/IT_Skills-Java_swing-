package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import tool.Tool;

public class Booking extends BaseDialog implements Tool {
	int uno;
	JPanel chart;
	DefaultTableModel m = model("no,출발지,도착지,도착시작,출발날짜".split(","));
	JTable t = table(m);

	public Booking(int uno) {
		super("", 100, 100);
		this.uno = uno;

		ui();
		data();

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));
		add(lbl("사용자 예매 정보", 2, 35), "North");
		add(chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(Color.black);
				g2.drawLine(50, 30, 50, 251);
				g2.drawLine(50, 40, 700, 40);
				g2.drawLine(50, 145, 700, 145);
				g2.drawLine(50, 250, 700, 250);

				var h = new int[] { 40, 145, 240 };
				var num = new int[] { 210, 105, 10 };

				var data = DB.toArrayList(
						"select month(date), count(*), rank() over(order by count(*) desc) from reservation r, user u, schedule s where r.user_no=u.no and r.schedule_no=s.no and u.no=? group by month(date) order by month(date) limit 3",
						uno);

				for (var list : data) {
					var row = toInt(list.get(2)) - 1;
					var i = data.indexOf(list) * 200;

					g2.setColor(new Color(0, 125, 255));
					g2.fillRect(165 + i, h[row], 80, num[row]);

					g2.setColor(Color.DARK_GRAY);
					g2.fillOval(205 + i, h[row] - 5, 10, 10);

					g2.drawString(list.get(0) + "월", 200 * i, 270);
					g2.drawString(list.get(1), 30, 40 + (row * 108));
				}

				for (var list : data) {
					var i = data.indexOf(list) * 200;

					if (data.indexOf(list) < data.size() - 1) {
						g2.drawLine(205 + i, h[toInt(list.get(2)) - 1], 205 + (i + 200),
								h[toInt(data.get(data.indexOf(list) + 1).get(2)) - 1]);
					}
				}
			}
		});

		add(sz(new JScrollPane(t), 800, 200), "South");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
