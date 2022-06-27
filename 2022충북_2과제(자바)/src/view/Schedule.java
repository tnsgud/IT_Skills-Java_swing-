
package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Schedule extends BaseFrame {
	LocalDate date = now;

	public Schedule() {
		super("상영시간표", 500, 500);

		t_no = 1;
		
		add(n = new JPanel(new GridLayout(1, 0)), "North");
		add(c = new JPanel(new GridLayout(0, 1)));

		for (int i = 0; i < 7; i++) {
			var d = date.plusDays(i);
			var tmp = new JPanel(new GridLayout(1, 0));

			tmp.add(lbl("<html>" + String.format("%02d", d.getMonthValue()) + "월<br>"
					+ d.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()), 2, 12));
			tmp.add(lbl(String.format("%02d", d.getDayOfMonth()), 2, 15));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var tmp : n.getComponents()) {
						((JPanel) tmp).setBorder(null);
					}

					var me = (JPanel) e.getSource();

					me.setBorder(new MatteBorder(0, 2, 0, 2, Color.black));

					date = d;

					setMovie();
				}
			});

			n.add(tmp);
		}

		n.setBorder(new MatteBorder(2, 0, 2, 0, Color.black));

		((JPanel) n.getComponent(0)).setBorder(new MatteBorder(0, 2, 0, 2, Color.black));

		setMovie();

		setVisible(true);
	}

	private void setMovie() {
		c.removeAll();

		for (var m_no : getRows("select m_no from schedule where t_no = ? and sc_date = ? group by m_no", t_no,
				date.toString())) {
			var tmp = new JPanel(new FlowLayout(0, 5, 5));

			for (var rs : getRows("select sc_no, sc_time from schedule where t_no = ? and sc_date = ? and m_no = ?",
					t_no, date.toString(), m_no.get(0))) {
				var temp = new JPanel(new GridLayout(0, 1));

				temp.add(lbl(rs.get(1).toString(), 0, 15));
				temp.add(lbl("<html><font color='blue'>"
						+ getOne("select 60-ifnull(sum(r_people), 0) from reservation where sc_no=?", rs.get(0)) + "/60", 0));
				temp.setName(rs.get(0).toString());

				temp.setBorder(new LineBorder(Color.black));

				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						sc_no = toInt(((JPanel) e.getSource()).getName());
						new SheetSelect().addWindowListener(new Before(Schedule.this));
					}
				});

				tmp.add(sz(temp, 60, 30));
			}

			tmp.setBorder(new TitledBorder(new LineBorder(Color.black),
					getOne("select m_name from movie where m_no=?", m_no.get(0))));

			c.add(tmp);
		}

		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new Schedule();
	}
}
