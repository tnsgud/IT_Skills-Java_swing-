package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.text.html.InlineView;

public class Seat extends BaseFrame {
	int idx = 0;
	ArrayList<Item> items = new ArrayList<>();
	HashMap<String, JLabel> seat = new HashMap<>();

	public Seat() {
		super("좌석배정", 1000, 600);

		this.setResizable(true);

		add(new JScrollPane(c = new JPanel(new BorderLayout(25, 25))));
		add(e = sz(new JPanel(new BorderLayout()), 200, 600), "East");

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

					lbl.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							var me = (JLabel) e.getSource();

							if (me.getBackground().equals(Color.gray)) {
								eMsg(me.getText() + "좌석은 선택이 불가능합니다.");
								return;
							}
							var peo = items.get(idx).p;

							if (me.getBackground().equals(Color.red)) {
								me.setBackground(Color.white);

								items.get(idx).setText(peo.fname + " " + peo.lname + " - ");
								items.get(idx).p.seat = null;
							} else {
								if (items.get(idx).p.seat != null) {
									var ans = JOptionPane.showConfirmDialog(null, "이미 좌석을 선택하셨습니다. 해당 좌석을 선택하시겠습니까?",
											"경고", JOptionPane.YES_NO_OPTION);
									if (ans == JOptionPane.YES_OPTION) {
										seat.get(items.get(idx).p.seat).setBackground(Color.white);
									} else {
										if (idx < items.size() - 1) {
											idx++;
										}else {
											return;
										}
									}
								}

								me.setBackground(Color.red);

								items.get(idx).p.seat = me.getText();
								items.get(idx).setSelect();
							}
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
				"select c_seat from reservation r, companion c where r.r_no = c.r_no and r_date = ? and s_no = ?",
				now.toString(), s_no)) {
			seat.get(rs.get(0).toString().toUpperCase()).setBackground(Color.gray);
		}

		for (var cap : "dong,song,hun".split(",")) {
			peoples.add(new People(cap.equals("dong") ? 1 : 2, "Hong", "gil" + cap));
		}

		e.add(lbl("총 " + peoples.size() + "명", 2, 15), "North");
		e.add(ec = new JPanel());
		e.add(btn("선택완료", a -> {
			if(items.stream().filter(i->i.p.seat==null).count() > 0) {
				eMsg("좌석을 모두 선택해주세요.");
				return;
			}
			
			dispose();
		}), "South");

		ec.setLayout(new BoxLayout(ec, BoxLayout.PAGE_AXIS));

		for (var p : peoples) {
			var item = new Item(p, peoples.indexOf(p));

			ec.add(item);
			ec.add(Box.createVerticalStrut(20));

			items.add(item);
		}

		items.get(idx).setBackground(Color.blue);

		setVisible(true);
	}

	class Item extends JLabel {
		People p;
		int i;

		public Item(People p, int i) {
			super(p.fname + " " + p.lname + " - ", 2);
			this.p = p;
			this.i = i;

			setMaximumSize(new Dimension(200, 35));

			setFont(new Font("맑은 고딕", 1, 15));

			setBorder(new LineBorder(Color.gray));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					setSelect();
				}
			});
		}

		public void setSelect() {
			idx = i;

			for (var it : items) {
				it.setBorder(new LineBorder(Color.gray));
			}

			setText(p.fname + " " + p.lname + " - " + (p.seat == null ? "" : p.seat));

			setBorder(new LineBorder(Color.blue));
		}
	}

	public static void main(String[] args) {
		new Seat();
	}
}
