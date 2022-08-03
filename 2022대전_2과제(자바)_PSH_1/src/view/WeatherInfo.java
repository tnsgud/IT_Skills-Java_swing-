package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class WeatherInfo extends BaseFrame {
	JLabel prev, lblDate, next;
	LocalDate today = LocalDate.now();
	Day days[] = new Day[42];

	public static void main(String[] args) {
		new Main();
	}

	public WeatherInfo() {
		super("날씨정보", 1100, 800);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.add(prev = lbl("◀", 0, 30));
		n.add(lblDate = lbl("2022년 8월", 0, 30));
		n.add(next = lbl("▶", 0, 30));

		var cap = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < cap.length; i++) {
			var lbl = lbl(cap[i], 0);
			lbl.setForeground(i % 7 == 6 ? Color.blue : (i % 7 == 0 ? Color.RED : Color.black));
			c.add(lbl);
		}

		for (int i = 0; i < days.length; i++) {
			c.add(days[i] = new Day());
			days[i].setBorder(new LineBorder(Color.black));
		}

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (today.getMonthValue() == 1) {
					eMsg("올해부터 정보가 제공됩니다.");
					return;
				}

				today = today.minusMonths(1);
				setCal();
			}
		});

		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (today.getMonthValue() == 12) {
					eMsg("올해부터 정보가 제공됩니다.");
					return;
				}

				today = today.plusMonths(1);
				setCal();
			}
		});

		setCal();

		setVisible(true);
	}

	private void setCal() {
		today = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
		lblDate.setText(today.format(DateTimeFormatter.ofPattern("yyyy년 MM월")));
		int sday = today.getDayOfWeek().getValue() % 7;
		for (int i = 0; i < 42; i++) {
			var ld = today.plusDays(i - sday);
			var col = i % 7 == 6 ? Color.blue : (i % 7 == 0 ? Color.RED : Color.black);

			days[i].setVisible(ld.getMonthValue() == today.getMonthValue());

			if (days[i].isVisible()) {
				int hum = 0, state = 0, temp = 0;
				var rs = getRows("select * from weather where w_day = ?", ld.toString());
				if (!rs.isEmpty()) {
					temp = toInt(rs.get(0).get(1));
					hum = toInt(rs.get(0).get(2));
					state = toInt(rs.get(0).get(3));
				}

				var bName = getRows("select b_name from base where b_temperature between ? and ?", temp - 1, temp + 1)
						.stream().map(a -> a.get(0).toString()).collect(Collectors.joining(","));
				days[i].setData(ld, col, bName, state, temp, hum);
			}
		}
	}

	class Day extends JPanel {
		JPanel c, s;
		JProgressBar bar = new JProgressBar(0, 100);
		JLabel lblDay, img, lblWeather;

		public Day() {
			super(new BorderLayout());

			add(lblDay = lbl("", 4), "North");
			add(new JScrollPane(c = new JPanel()));
			add(s = new JPanel(new FlowLayout(0)), "South");
			c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

			s.add(sz(bar, 100, 20));
			s.add(img = new JLabel());
			s.add(lblWeather = lbl("", 0));

			bar.setForeground(Color.blue);
			bar.setStringPainted(true);
			bar.setBorder(new LineBorder(Color.blue));
			bar.setUI(new BasicProgressBarUI() {
				@Override
				protected Color getSelectionBackground() {
					return Color.black;
				}

				@Override
				protected Color getSelectionForeground() {
					return Color.black;
				}
			});
		}

		void setData(LocalDate ld, Color col, String bName, int weather, int temp, int hum) {
			c.removeAll();

			lblDay.setText(ld.getDayOfMonth() + "");
			lblDay.setForeground(col);

			for (var b : bName.split(",")) {
				var l = lbl(b, 2);
				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var edit = new AdminEditApply();
						edit.txt[0].setText(b);
						edit.btnSearch.setText("재검색");
						edit.rad[toInt(getOne("select division from base where b_name = ?", b)) - 1].setSelected(true);
						edit.setData(getRows("select b_temperature, b_note, b_img, b_no from base where b_name = ?", b)
								.get(0));
						edit.addWindowListener(new Before(WeatherInfo.this));
					}
				});
				c.add(l);
			}

			img.setIcon(getIcon("./datafiles/날씨/" + weather + ".jpg", 10, 10));
			lblWeather.setText(temp + "℃");

			lblWeather.setForeground(temp < 0 ? Color.blue : temp > 30 ? Color.red : Color.black);

			bar.setValue(hum);

			c.repaint();
			c.revalidate();
		}
	}
}
