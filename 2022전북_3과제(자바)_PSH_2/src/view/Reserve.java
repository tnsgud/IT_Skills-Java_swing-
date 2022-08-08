package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.People;

public class Reserve extends BasePage {
	JComboBox com[] = {
			new JComboBox<>(getRows("select a_name from airport").stream().map(a -> a.get(0).toString()).toArray()),
			new JComboBox<>() };
	JTextField txt[] = new JTextField[2];

	public Reserve() {
		peoples.clear();

		setLayout(new GridBagLayout());

		add(c = new JPanel(new BorderLayout()));

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(cs = new JPanel(), "South");

		var cap = "출발지,도착지,출발날짜,인원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 0, 0));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));

			if (i < 2) {
				tmp.add(com[i]);

				com[i].setSelectedIndex(-1);
			} else {
				var icon = new JLabel(getIcon("./datafiles/" + (i == 2 ? "달력" : "사람") + ".png", 20, 20));

				icon.setName(i + "");

				icon.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var idx = toInt(((JLabel) e.getSource()).getName()) - 2;

						if (idx == 0) {
							new Cal(txt[idx],
									txt[idx].getText().isEmpty() ? null : LocalDate.parse(txt[idx].getText()));
						} else {
							new PeopleSelect(txt[idx]);
						}
					}
				});

				tmp.add(txt[i - 2] = new JTextField(20));
				txt[i - 2].setEditable(false);
				tmp.add(icon);
			}

			cc.add(tmp);
		}

		s.add(btn("확인", a -> {
			var rs = getRows("select * from schedule where s_depart=? and s_arrival = ?", com[0].getSelectedIndex() + 1,
					com[1].getSelectedIndex() + 1);
			if (rs.isEmpty()) {
				eMsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
				return;
			}

			var date = LocalDate.parse(txt[0].getText());
			mf.swap(new AirlineTicket(date, rs.stream().map(e -> e.get(0)).toArray()));
		}));

		com[0].addActionListener(a -> {
			com[1].removeAll();

			for (var rs : getRows("select a_name from airport where a_name <> ?", com[0].getSelectedItem())) {
				com[1].addItem(rs.get(0).toString());
			}

			com[1].setSelectedIndex(-1);
		});
	}

	public static void main(String[] args) {
		BasePage.mf = new MainFrame();
		BasePage.mf.swap(new Reserve());
	}
}
