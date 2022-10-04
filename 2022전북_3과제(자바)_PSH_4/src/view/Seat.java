package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import model.People;

public class Seat extends BaseFrame {
	ArrayList<Item> items = new ArrayList<>();
	HashMap<String, JLabel> seat = new HashMap<>();

	int idx = 0;

	public Seat() {
		super("좌석배정", 1000, 600);

		BasePage.peoples.forEach(p -> p.seat = null);

		add(new JScrollPane(c));
		add(e = sz(new JPanel(), 200, 600), "East");

		c.add(cw = new JPanel(new FlowLayout(2, 5, 5)), "West");
		c.add(cc = new JPanel(new FlowLayout(1, 5, 5)));
		c.add(ce = new JPanel(new FlowLayout(0, 5, 5)), "East");

		var code = "A,B,C,D,E,F,G".split(",");
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 9; j++) {
				var tmp = j < 3 ? cw : j < 6 ? cc : ce;
				JLabel lbl = null;

				if (j == 0 || j == 8) {
					lbl = lbl(i == 0 ? "" : i + "", 0);
				} else if (i == 0) {
					lbl = lbl(code[j - 1], 0);
				} else {
					lbl = lbl(code[j - 1] + i, 0);

					lbl.setOpaque(true);
					lbl.setBackground(Color.white);
					lbl.setBorder(new LineBorder(Color.gray));

					event(lbl, e -> {
						var me = (JLabel) e.getSource();

						if (me.getBackground().equals(Color.gray)) {
							eMsg(me.getText() + "좌석은 선택이 불가능합니다.");
							return;
						}

						var item = items.get(idx);
						var p = item.p;

						if (me.getBackground().equals(Color.gray)) {
							me.setBackground(Color.white);

							item.setText(p.fName + " " + p.lName + " - ");
							p.seat = null;
						} else {
							if (p.seat != null) {
								var ans = JOptionPane.showConfirmDialog(null, "이미 좌석을 선택하셨습니다. 해당 좌석을 선택하시겠습니까?", "경고",
										JOptionPane.YES_NO_OPTION);

								if (ans == JOptionPane.YES_OPTION) {
									seat.get(p.seat).setBackground(Color.white);
								} else {
									if (idx < items.size() - 1) {
										idx++;
									}
								}
							}

							me.setBackground(Color.red);

							p.seat = me.getText();
							item.setSelect();
						}
					});

					seat.put(code[j - 1] + i, lbl);
				}

				tmp.add(sz(lbl, j == 0 || j == 8 ? 35 : 80, i == 0 ? 20 : 80));
			}
		}

		for (var p : new JPanel[] { cw, cc, ce }) {
			sz(p, 230, 1750);
		}

		for (var rs : getRows(
				"select c_seat from reservaiton r, companion c where r.r_no = c.r_no and r_date = ? an s_no = ?",
				LocalDate.now().toString(), sNo)) {
			seat.get(rs.get(0).toString().toUpperCase()).setBackground(Color.gray);
		}

		e.add(lbl("총 " + BasePage.peoples.size() + "명", 2, 15), "North");
		e.add(ec = new JPanel());
		e.add(btn("선택완료", a -> {
			if (items.stream().filter(i -> i.p.seat == null).count() > 0) {
				eMsg("좌석을 모두 선택해주세요.");
				return;
			}

			dispose();
		}));

		ec.setLayout(new BoxLayout(ec, BoxLayout.PAGE_AXIS));

		for (var p : BasePage.peoples) {
			var item = new Item(p);

			ec.add(item);
			ec.add(Box.createVerticalStrut(20));

			items.add(item);
		}

		items.get(idx).setBackground(Color.blue);

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				if (e.getOppositeWindow() instanceof JDialog) {
					return;
				}

				dispose();
			}
		});

		setVisible(true);
	}

	class Item extends JLabel {
		People p;

		public Item(People p) {
			this.p = p;

			setMaximumSize(new Dimension(200, 40));
			setFont(new Font("맑은 고딕", 1, 15));
			setBorder(new LineBorder(Color.gray));

			event(this, e -> {
				setSelect();
			});
		}

		private void setSelect() {
			idx = items.indexOf(this);

			items.stream().forEach(i -> i.setBorder(new LineBorder(Color.gray)));

			setToolTipText(p.fName + " " + p.lName + " - " + (p.seat == null ? "" : p.seat));
			setBorder(new LineBorder(Color.blue));
		}
	}
	
	public static void main(String[] args) {
		new LoginFrame();
	}
}
