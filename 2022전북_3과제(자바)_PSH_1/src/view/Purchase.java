package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.People;

public class Purchase extends BasePage {
	JTextField num[] = new JTextField[4], date[] = new JTextField[2], pass = new JTextField(13),
			mil = new JTextField(13);
	JComboBox<String> com = new JComboBox<>("신한카드,현대카드,삼성카드,KB국민카드,롯데카드,하나카드,BC카드".split(","));
	ArrayList<Object> data;
	JLabel totPrice, lblDC;
	JButton puz;
	int idx = 0;

	public Purchase() {
		BaseFrame.user = getRows("select * from member where m_no = ?", 1).get(0);
		BaseFrame.s_no = 9;

		data();
		ui();
		evnet();
	}

	private void data() {
		r_date = LocalDate.of(2022, 9, 1);

		data = getRows(
				"select a1_code, a1_name, a2_code, a2_name, ?, time_format(s_time, '%h:%m') from v1 where s_no = ?",
				r_date, BaseFrame.s_no).get(0);
	}

	private void evnet() {
		mil.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (mil.getText().isEmpty())
					return;

				if (toInt(mil.getText()) < 1) {
					eMsg("마일리지 금액을 확인해주세요.");
					mil.setText("");
					return;
				}
				setPrice();
			}
		});

		for (var t : num) {
			t.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					var me = ((JTextField) e.getSource());

					if (me.getText().length() == 4) {
						num[++idx].requestFocus();
					} else {
						idx = Arrays.asList(num).indexOf(me);
					}
				}
			});
			t.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (t.getText().length() == 4) {
						idx = toInt(t.getName());

						if (idx == 3)
							return;

						num[++idx].requestFocus();
					}
				}
			});
		}
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(1, 0)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(lbl("결제하기", 2, 15), "West");
		n.add(puz = btn("퍼즐맞추기", a -> new Puzzle(this)), "East");

		c.add(cw = new JPanel(new GridLayout(0, 1, 5, 5)));
		c.add(new JScrollPane(cc = new JPanel(new BorderLayout(10, 10))));

		var cap = "카드,번호,유효기간,비밀번호,마일리지 사용".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl("<html>" + cap[i]
					+ (i == 4 ? "<br>총 " + getOne(
							"select format(sum(mi_income)- sum(mi_expense), '#,##0') from mileage where m_no=?",
							BaseFrame.user.get(0)) : ""),
					2, 12), 80, 40));

			if (i == 0) {
				tmp.add(sz(com, 140, 20));
				com.setSelectedIndex(-1);
			} else if (i == 1) {
				for (int j = 0; j < num.length; j++) {
					tmp.add(num[j] = new JTextField(6));
					num[j].setName(j + "");
				}
			} else if (i == 2) {
				for (int j = 0; j < date.length; j++) {
					tmp.add(date[j] = new JTextField(6));
				}
			} else {
				tmp.add(i == 3 ? pass : mil);
			}

			cw.add(tmp);
		}

		cc.add(lbl(String.format("<html>%s(%s) → %s(%s)<br>%s %s", data.get(0), data.get(1), data.get(2), data.get(3),
				data.get(4), data.get(5)), 2, 25), "North");

		{
			var tmp = new JPanel(new BorderLayout());
			var tmp_c = new JPanel(new GridLayout(0, 1));

			tmp.add(lbl("탑승객", 2, 20), "North");
			tmp.add(tmp_c);

			for (var peo : BaseFrame.peoples) {
				var temp = new JPanel(new BorderLayout());

				temp.add(lbl(String.format("%s %s(%s)", peo.getFname(), peo.getLname(), peo.getSeat()), 2), "West");
				temp.add(lbl(String.format("%s %s", c_div[peo.getDiv()], format(peo.getPrice())), 2), "East");

				tmp_c.add(temp);
			}

			cc.add(tmp);
		}

		{
			var tmp = new JPanel(new BorderLayout());
			var tmp_c = new JPanel(new GridLayout(0, 1));

			tmp.add(lbl("수하물", 2, 20), "North");
			tmp.add(tmp_c);

			for (var b : BaseFrame.bag) {
				var temp = new JPanel(new BorderLayout());
				var cap1 = Stream.of(b.chk).filter(JCheckBox::isSelected)
						.map(x -> "사이즈초과,무게초과".split(",")[Arrays.asList(b.chk).indexOf(x)])
						.collect(Collectors.joining(","));

				temp.add(lbl(String.format("%s%s", b.namelbl.getText(), cap1.isEmpty() ? "" : "(" + cap1 + ")"), 2, 15),
						"West");
				temp.add(lbl(String.format("%s", b.pricelbl.getText()), 4, 15), "East");
				tmp_c.add(temp);
			}

			cc.add(tmp, "South");
		}

		s.add(lblDC = lbl("(10%할인)", 0));
		s.add(totPrice = lbl("", 4, 25));
		s.add(btn("결제하기", a -> {
			for (var t : num) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}

				if (!t.getText().matches("^\\d{4}$")) {
					eMsg("카드 번호를 확인해주세요./");
					return;
				}
			}

			for (int i = 0; i < date.length; i++) {
				if (date[i].getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}

				if (date[i].getText().matches("^" + (i == 0 ? "\\d{2}" : "(0[1-9]|1[0-2])") + "$")) {
					eMsg("유효기간을 확인해주세요.");
					return;
				}
			}

			if (com.getSelectedIndex() == -1 || mil.getText().isEmpty() || pass.getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			if (pass.getText().length() != 4 || !pass.getText().matches("^\\d{4}$")) {
				eMsg("비밀번호를 확인해주세요.");
				return;
			}

			iMsg(totPrice.getText() + " 결제가 완료되었습니다.");

			execute("insert into reservation values(0,?,?,?,?)", BaseFrame.user.get(0), BaseFrame.s_no,
					r_date.toString(), toInt(totPrice.getText()));
			var r_no = getOne("select r_no from reservation order by r_no desc");
			for (var peo : BaseFrame.peoples) {
				execute("insert into companion values(0, ?, ?, ?, ?, ?, ?)", r_no, peo.getSex(),
						peo.getFname() + " " + peo.getLname(), peo.getBirth(), peo.getSeat(), peo.getDiv());
			}

			if (toInt(mil.getText()) == 0) {
				execute("insert into mileage values(0, ?, ?, 0)", BaseFrame.user.get(0),
						toInt(totPrice.getText()) * 0.01);
			} else {
				execute("insert into mileage values(0, ?, 0, ?)", BaseFrame.user.get(0), toInt(mil.getText()));
			}

			Login.icon.displayMessage("예약이 완료되었습니다.", "", MessageType.INFO);

			main.swap(new Reserve());
		}));

		lblDC.setVisible(false);

		setPrice();
	}

	void setPrice() {
		var peoP = BaseFrame.peoples.stream().mapToInt(People::getPrice).sum();
		var bagP = BaseFrame.bag.stream().mapToInt(b -> toInt(b.pricelbl.getText())).sum();
		var pr = peoP + bagP - (toInt(mil.getText()) == 0 ? 0 : toInt(mil.getText()));
		var tot = pr * (lblDC.isVisible() ? 0.9 : 1.0);

		totPrice.setText("총 " + format((int) tot) + "원");
	}
}
