package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Schedule extends BaseFrame {
	JComboBox com[] = new JComboBox[2];
	JTextField txt[] = new JTextField[3];
	LocalTime stime, etime;
	int dep, arv, time;

	public Schedule() {
		super("항공일정등록", 500, 500);

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(btn("확인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			for (var c : com) {
				if (c.getSelectedIndex() == -1) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			try {
				var t = LocalTime.parse(txt[0].getText());
			} catch (Exception e) {
				eMsg("출발시간을 확인해주세요.");
				return;
			}

			if (toInt(txt[2].getText()) < 1) {
				eMsg("가격을 확인해주세요.");
				return;
			}

			for (var rs : getRows(
					"select time_format(s_time, '%h:%m') from schedule s, airport a1, airport a2 where s.s_depart =a1.a_no and s.s_arrival = a2.a_no and s_depart=? and s_arrival=?",
					dep, arv)) {
				var rs_stime = LocalTime.parse(rs.get(0).toString());
				var rs_etime = rs_stime.plusMinutes(time);

				if ((stime.isBefore(rs_stime) && etime.isBefore(rs_etime))
						|| (stime.isAfter(rs_stime) && etime.isAfter(rs_etime))
						|| (stime.equals(rs_stime) && etime.equals(rs_etime))) {
					eMsg("해당 출발시간에 동일한 항공일정이 존재합니다.");
					return;
				}
			}

			iMsg("항공일정등록이 완료되었습니다.");
			execute("insert into schedule values(0, ?, ?, ?, ?)", dep, arv, stime, txt[2].getText());

			dispose();
		}), "South");

		var cap = "출발지,도착지,출발시간,도착시간,가격".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(lbl(cap[i], 2, 15), "West");

			if (i < 2) {
				tmp.add(com[i] = new JComboBox<>(i == 0
						? getRows("select a_name from airport").stream().flatMap(a -> a.stream()).toArray(String[]::new)
						: "".split(",")));

				com[i].setSelectedIndex(-1);
			} else {
				tmp.add(txt[i - 2] = new JTextField());
			}

			c.add(tmp);
		}

		txt[0].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					stime = LocalTime.parse(txt[0].getText());
					etime = stime.plusMinutes(time);
					txt[1].setText(etime.toString());
				} catch (Exception e2) {
				}
			}
		});

		com[0].addItemListener(i -> {
			if (i.getStateChange() == ItemEvent.SELECTED) {
				com[1].removeAllItems();

				dep = com[0].getSelectedIndex() + 1;

				for (var rs : getRows("select a_name from airport where  a_name <> ?", com[0].getSelectedItem())) {
					com[1].addItem(rs.get(0));
				}
				com[1].setSelectedIndex(-1);
			}
		});
		com[1].addItemListener(i -> {
			var cnt = toInt(getOne("select count(*)-1 from airport"));
			if (com[1].getItemCount() < cnt) {
				return;
			}

			if (i.getStateChange() == ItemEvent.SELECTED) {
				arv = toInt(getOne("select a_no from airport where a_name = ?", com[1].getSelectedItem()));

				var spoint = getRows("select a_latitude, a_longitude from airport where a_no = ?", dep).get(0);
				var epoint = getRows("select a_latitude, a_longitude from airport where a_no = ?", arv).get(0);

				time = (int) distance((Double) spoint.get(0), (Double) spoint.get(1), (Double) epoint.get(0),
						(Double) epoint.get(1)) / 10;
			}
		});

		setVisible(true);
	}
}
