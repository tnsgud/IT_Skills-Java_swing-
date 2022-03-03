package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Booking extends BaseDialog {

	String uno;
	DefaultTableModel m = BaseFrame.model("no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = BaseFrame.table(m);
	JPanel chart;
	JScrollPane jsp;
	ArrayList<ArrayList<String>> temp = new ArrayList<>();

	public Booking(String uno) {
		super("예매 정보", 800, 600);
		this.uno = uno;
		add(BaseFrame.lbl("사용자 예매 정보", JLabel.LEFT, 20), "North");
		add(chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(new Color(0, 125, 255));
				g2.drawLine(50, 30, 50, 251);
				g2.drawLine(50, 40, 700, 40);
				g2.drawLine(50, 145, 700, 145);
				g2.drawLine(50, 250, 700, 250);

				int h[] = { 40, 145, 240 };
				int num[] = { 210, 105, 10 };

				BaseFrame.Query(
						"select month(date), count(*), rank() over(order by count(*) desc) from reservation r, user u, schedule s where r.user_no = u.no and r.schedule_no = s.no and u.no = ? group by month(date) order by month(date) limit 3;",
						temp, uno);

				for (int i = 0; i < temp.size(); i++) {

					int row = BaseFrame.toInt((temp.get(i).get(2))) - 1;

					g2.setColor(new Color(0, 125, 255));
					g2.fillRect(165 + (i * 200), h[row], 80, num[row]);

					g2.setColor(Color.darkGray);
					g2.fillOval(205 + (i * 200), h[row] - 5, 10, 10);

					g2.drawString(temp.get(i).get(0) + "월", 200 + (i * 200), 270);
					g2.drawString(temp.get(i).get(1), 30, 40 + (row * 108));

				}

				for (int i = 0; i < temp.size() - 1; i++) {
					g2.drawLine(205 + (i * 200), h[BaseFrame.toInt(temp.get(i).get(2)) - 1], 205 + ((i + 1) * 200),
							h[BaseFrame.toInt(temp.get(i + 1).get(2)) - 1]);
				}
			}
		});

		add(jsp = new JScrollPane(t), "South");
		jsp.setPreferredSize(new Dimension(600, 200));

		try {
			BaseFrame.dataInit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		m.setRowCount(0);

		try {
			var rs = BaseFrame.stmt.executeQuery(
					"select s.no, s.departure_location2_no , s.arrival_location2_no, time(date_add(s.date, interval elapsed_time hour_second)), s.date from reservation r inner join schedule s on r.schedule_no = s.no and r.user_no = "
							+ uno + " order by s.date");
			int i = 1;
			while (rs.next()) {
				Object row[] = new Object[5];
				row[0] = i;
				row[1] = BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(2)];
				row[2] = BaseFrame.loc1[BaseFrame.locMap[rs.getInt(3)]] + " " + BaseFrame.loc2[rs.getInt(3)];
				row[3] = rs.getString(4);
				row[4] = rs.getString(5);
				m.addRow(row);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		((JPanel) (getContentPane())).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		new Booking("1").setVisible(true);
	}
}
