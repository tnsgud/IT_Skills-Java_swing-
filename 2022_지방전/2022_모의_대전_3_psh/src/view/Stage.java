package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Stage extends BaseFrame {

	JPanel seat, peo, item;
	JLabel tot, people[] = new JLabel[5];
	JButton btn1, btn2;

	HashMap<String, Seat> seats = new HashMap<String, Stage.Seat>();
	HashMap<String, Item> items = new HashMap<String, Stage.Item>();

	String tno;

	int idx = 0, totPrice = 0;

	public Stage() {
		super("좌석", 1300, 850);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(sz(e = new JPanel(new BorderLayout(5, 5)), 350, 1), "East");

		var c_n = new JPanel(new BorderLayout());
		var c_c = new JPanel(new BorderLayout(5, 5));

		var e_n = new JPanel(new FlowLayout(0));
		var e_c = new JPanel(new BorderLayout());
		var e_s = new JPanel(new GridLayout(1, 2, 5, 5));

		c.add(sz(c_n, 1, 100), "North");
		c.add(c_c);

		e.add(e_n, "North");
		e.add(e_c);
		e.add(e_s, "South");

		c_n.add(lbl("STAGE", 0, 35));
		c_c.add(lbl("날짜 : " + getOne("select p_date from perform where p_no=?", pno), 4, 15), "North");
		c_c.add(seat = new JPanel(new GridLayout(0, 1, 5, 5)));

		e_n.add(lbl(getOne("select p_name from perform where p_no=?", pno), 0, 25));
		e_c.add(peo = new JPanel(new FlowLayout(0)), "North");
		e_c.add(item = new JPanel(new FlowLayout(0)));
		e_c.add(tot = lbl("총금액 : 0", 2), "South");
		e_s.add(btn1 = btn("이전으로", a -> dispose()));
		e_s.add(btn2 = btn("다음으로", a -> {
			if (items.isEmpty()) {
				eMsg("좌석을 선택해주세요.");
				return;
			}

			if (items.size() - 1 != idx) {
				eMsg("인원수에 맞게 좌석을 선택해주세요.");
				return;
			}

			if (a.getActionCommand().equals("다음으로")) {
				new Purchase().addWindowListener(new Before(Stage.this));
			} else {
				String seats = "", dis = "";

				for (var k : items.keySet()) {
					if (seats.isEmpty()) {
						seats = k;
						dis = items.get(k).dis + "";
					} else {
						seats += "," + k;
						dis += "," + items.get(k).dis;
					}
				}

				execute("update ticket set t_seat=?, t_discount=? where t_no=?", seats, dis, tno);
			}
		}));

		for (var s : "A,B,C,D,E,F".split(",")) {
			seats.put(s, new Seat(s));
			seat.add(seats.get(s));
		}
		peo.add(lbl("인원수 : ", 2, 15));
		for (int i = 0; i < people.length; i++) {
			peo.add(people[i] = lbl(i == 0 ? "●" : "○", 0));
			people[i].setName(i + "");
			people[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					idx = toInt(((JLabel) e.getSource()).getName());
					Stream.of(people).forEach(s -> s.setText("○"));
					Stream.of(people).filter(s -> toInt(s.getName()) <= idx).forEach(s -> s.setText("●"));
				}
			});
		}

		e.setBorder(new LineBorder(Color.black));
		c_n.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		e_n.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		item.setBorder(new TitledBorder(new LineBorder(Color.black), "선택한 좌석"));
	}

	public Stage(String tno) {
		this();
		this.tno = tno;

		btn1.setText("취소하기");
		btn2.setText("수정하기");

		var rs = rs("select * from ticket where t_no=?", tno);
		try {
			if (rs.next()) {
				var tseat = rs.getString("t_seat").split(",");
				var tdis = rs.getString("t_discount").split(",");

				Stream.of(people).forEach(s -> s.setEnabled(false));

				for (int i = 0; i < tdis.length; i++) {
					people[i].setText("●");

					var t = tseat[i].substring(0, 0);
					var d = tseat[i].substring(1);

					seats.get(t).lbl[toInt(d) - 1].setBackground(Color.orange);

					var item = new Item(tseat[i], toInt(getOne("select p_price from perform where p_no=?", pno)));
					this.item.add(item);
					items.put(tseat[i], item);

					try {
						if (!tdis[i].equals("0")) {
							item.chk[toInt(tdis[i]) - 1].setSelected(true);
							item.setPrice();
						}
					} catch (Exception e) {
						item.chk[i].setSelected(false);
						item.setPrice();
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class Seat extends JPanel {
		JLabel lbl[] = new JLabel[10];

		public Seat(String seat) {
			super(new GridLayout(1, 0, 5, 5));

			add(lbl(seat, 0, 15));

			for (int i = 0; i < lbl.length; i++) {
				add(lbl[i] = lbl(i + 1 + "", 0));
				lbl[i].setName(seat + String.format("%02d", i + 1));
				lbl[i].setOpaque(true);
				lbl[i].setBorder(new LineBorder(Color.black));

				if (!getOne("select * from ticket where  p_no=? and t_seat like ?", pno, "%" + lbl[i].getName() + "%")
						.isEmpty()) {
					lbl[i].setBackground(Color.LIGHT_GRAY);
				}

				lbl[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = (JLabel) e.getSource();

						if (l.getBackground().equals(Color.LIGHT_GRAY)) {
							eMsg("이미 예매된 좌석입니다.");
						} else if (l.getBackground().equals(Color.orange)) {
							l.setBackground(null);
							items.remove(l.getName());
							item.removeAll();

							for (var k : items.keySet()) {
								item.add(items.get(k));
								totPrice += items.get(k).price;
							}

							tot.setText("총 금액 : " + df.format(totPrice));

							item.repaint();
							item.revalidate();
						} else {
							if (items.size() == 5) {
								eMsg("더 이상 선택이 불가합니다.");
								return;
							}

							l.setBackground(Color.orange);

							var item = new Item(l.getName(),
									toInt(getOne("select p_price from perform where p_no=?", pno)));
							items.put(l.getName(), item);
							Stage.this.item.add(item);
						}
					}
				});
			}
		}
	}

	class Item extends JPanel {
		JPanel n, c;
		JLabel l1, l2;
		ButtonGroup bg = new ButtonGroup();
		JCheckBox[] chk = { new JCheckBox("청소년 할인 20%"), new JCheckBox("어린이 할인 40%"), new JCheckBox("장애인 할인 50%") };

		int price = 0, dis = 0;
		String seat;

		public Item(String seat, int price) {
			super(new BorderLayout());
			this.seat = seat;
			this.price = price;

			totPrice += price;
			tot.setText("총 금액 : " + df.format(totPrice));

			add(n = new JPanel(new BorderLayout()), "North");
			add(c = new JPanel(new FlowLayout(0)));

			n.add(l1 = lbl(seat + " : " + df.format(price), 0));
			n.add(l2 = lbl("▼", 0), "East");

			for (int i = 0; i < chk.length; i++) {
				c.add(sz(chk[i], 300, 30));
				bg.add(chk[i]);

				chk[i].setName(i + 1 + "");
				chk[i].addActionListener(e -> setPrice());
			}

			l2.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (l2.getText().equals("▼")) {
						c.setVisible(true);
						sz(Item.this, 320, 120);
						l2.setText("▲");
					} else {
						c.setVisible(false);
						sz(Item.this, 320, 30);
						l2.setText("▼");
					}
				};
			});

			setBorder(new LineBorder(Color.black));
			sz(this, 320, 30);
		}

		void setPrice() {
			Stream.of(chk).filter(JCheckBox::isSelected).forEach(s -> {
				var p = toInt(getOne("select p_price from perform where p_no=?", pno));
				dis = toInt(s.getName());
				price = (int) (p * (dis == 0 ? 1 : dis == 1 ? 0.8 : dis == 2 ? 0.6 : 0.5));
			});

			totPrice = 0;
			items.keySet().forEach(k -> totPrice += items.get(k).price);

			l1.setText(seat + " : " + df.format(price));
			tot.setText("총 금액 : " + df.format(totPrice));
		}
	}

	public static void main(String[] args) {
		uno = 1;
		isLogin = true;
		new Search();
	}
}