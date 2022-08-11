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

public class Reserve extends BasePage {
	JComboBox com[] = {
			new JComboBox<>(getRows("select a_name from airport").stream().map(a -> a.get(0)).toArray(String[]::new)),
			new JComboBox<>() };
	JTextField txt[] = new JTextField[2];

	public Reserve() {
		setLayout(new GridBagLayout());

		add(c = new JPanel(new BorderLayout(50, 50)));

		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		c.add(cs = new JPanel(new FlowLayout(1)), "South");

		var cap = "출발지,도착지,출발날짜,인원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 0, 0));

			tmp.add(sz(lbl(cap[i], 2), 80, 20));

			if (i < 2) {
				tmp.add(sz(com[i], 150, 20));
			} else {
				var lbl = new JLabel(getIcon("./datafiles/" + (i == 2 ? "달력" : "사람") + ".png", 20, 20));
				lbl.setName(i + "");
				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						int idx = toInt(((JLabel) e.getSource()).getName()) - 2;

						if (idx == 0) {
							new Cal(txt[0], txt[0].getText().isEmpty() ? null : LocalDate.parse(txt[0].getText()));
						} else {
							new PeopleSelect(txt[idx]);
						}
					}
				});

				tmp.add(txt[i - 2] = new JTextField(15));
				tmp.add(lbl);
				txt[i - 2].setFocusable(false);

			}

			cc.add(tmp);
		}

		cs.add(btn("확인", a -> {
			for (int i = 0; i < 2; i++) {
				if (txt[i].getText().isEmpty() || com[i].getSelectedIndex() == -1) {
					eMsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
					return;
				}
			}

			var rs = getRows("select s_no from v1 where a1_name = ? and a2_name = ?", com[0].getSelectedItem(),
					com[1].getSelectedItem());
			if (rs.isEmpty()) {
				eMsg("선택하신 조건에 맞는 예약 가능 항공편이 없습니다.");
				return;
			}

			var date = LocalDate.parse(txt[0].getText());
			mf.swap(new AirlineTicket(date, rs.stream().mapToInt(e -> toInt(e.get(0))).toArray()));
		}));

		com[0].addActionListener(a -> {
			com[1].removeAllItems();

			for (var rs : getRows("select a_name from airport where a_name <> ?", com[0].getSelectedItem())) {
				com[1].addItem(rs.get(0));
			}
			com[1].setSelectedIndex(-1);
		});
		com[0].setSelectedIndex(-1);
	}

	public static void main(String[] args) {
		new Login();
	}
}
