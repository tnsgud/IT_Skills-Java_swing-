package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Reserve extends BasePage {
	JTextField txt[] = new JTextField[2];
	JComboBox com[] = new JComboBox[2];
	JPanel m = new JPanel(new BorderLayout());

	public Reserve() {
		BaseFrame.peoples.clear();
		
		setLayout(new GridBagLayout());

		add(m);

		m.add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		m.add(s = new JPanel(), "South");

		var cap = "출발지,도착지,출발날짜,인원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 0, 0));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));

			if (i < 2) {
				tmp.add(com[i] = sz(new JComboBox(i == 0
						? getRows("select a_name from airport").stream().flatMap(a -> a.stream()).toArray(String[]::new)
						: "".split(",")), 100, 20));

				com[i].setSelectedIndex(-1);

				com[0].addActionListener(a -> {
					com[1].removeAllItems();

					for (var rs : getRows("select a_name from airport where not a_name = ?",
							com[0].getSelectedItem())) {
						com[1].addItem(rs.get(0));
					}
					com[1].setSelectedIndex(-1);
				});
			} else {
				var icon = new JLabel(getIcon("./datafiles/" + (i == 2 ? "달력" : "사람") + ".png", 20, 20));

				icon.setName(i + "");

				icon.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var idx = toInt(((JLabel) e.getSource()).getName()) - 2;

						if (idx == 0) {
							new Cal(txt[idx], txt[idx].getText().isEmpty() ? null : LocalDate.parse(txt[idx].getText()))
									.setVisible(true);
						} else {
							new PeopleSelect(txt[idx]).setVisible(true);
						}
					}
				});

				tmp.add(txt[i - 2] = new JTextField(10));
				txt[i-2].setEditable(false);
				tmp.add(icon);
			}

			c.add(tmp);
		}

		s.add(btn("확인", a -> {
			var rs = getRows("select * from schedule where s_depart=? and s_arrival = ?", com[0].getSelectedIndex() + 1,
					com[1].getSelectedIndex() + 1);

			if (rs.isEmpty()) {
				eMsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
				return;
			}

			var date = LocalDate.parse(txt[0].getText());

			main.swap(new AirlineTicket(date, rs.stream().map(e -> e.get(0)).toArray()));
		}));

		setVisible(true);
	}
}
