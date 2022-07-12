package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SeatFrame extends BaseFrame {
	JLabel seats[][] = new JLabel[10][10];

	String t_no;
	ArrayList<Object> movie;
	LocalDateTime date;
	
	int cnt[][] = new int[3][1];

	public SeatFrame(String t_no, ArrayList<Object> movie, LocalDateTime date) {
		super("좌석 선택", 1300, 800);
		this.t_no = t_no;
		this.movie = movie;
		this.date = date;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new BorderLayout(20, 20)));
		add(s = new JPanel(new FlowLayout(0, 5, 5)));
		c.add(cc = new JPanel(new GridLayout(10, 10, 5, 5)));

		var rs = getRows("select * from reservation where r_time = ? and r_date = ? and m_no = ?",
				date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), date.toLocalDate(), movie.get(0));

		for (char i = 'A'; i <= 'J'; i++) {
			for (int j = 0; j < 10; j++) {
				var seat = i + String.format("%02d", j + 1);
				var flag = rs.stream().map(x -> x.get(6).toString())
						.filter(x -> Arrays.asList(x.split("\\.")).contains(seat)).findFirst().isPresent();
				seats[i - 'A'][j] = new JLabel(seat, 0) {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						var g2 = (Graphics2D) g;

						if (flag) {
							g2.setColor(Color.white);
							g2.drawLine(0, 0, getWidth(), getHeight());
							g2.drawLine(getWidth(), 0, 0, getHeight());
						}
					}
				};

				var lblSeat = seats[i - 'A'][j];

				if (flag) {
					lblSeat.setBackground(Color.white);
				} else {
					lblSeat.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							var selCount = Stream.of(seats).map(Arrays::asList).flatMap(x -> x.stream())
									.filter(x -> x.getName() != null).count();
							int maxCount = cnt[0][0] + cnt[1][0]+ cnt[2][0];
							
							if(lblSeat.getName() == null) {
								if(maxCount == 0) {
									eMsg("");
								}
							}else {
								
							}
						}
					});
				}
			}
		}
	}
}
