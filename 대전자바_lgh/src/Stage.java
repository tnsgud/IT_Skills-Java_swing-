import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Stage extends BaseFrame {

	JPanel seat, item, peo;
	JLabel people[] = new JLabel[5], tot;
	JButton btn1, btn2;

	HashMap<String, Seat> seats = new HashMap<String, Stage.Seat>();
	HashMap<String, Item> items = new HashMap<String, Stage.Item>();

	String tno;
	int cnt = 0, sum = 0, pcnt = 1;

	public Stage() {
		super("좌석", 1300, 850);

		this.add(c = new JPanel(new BorderLayout()));
		this.add(e = new JPanel(new BorderLayout()), "East");

		var c_n = new JPanel();
		var c_c = new JPanel(new BorderLayout());

		var e_n = new JPanel(new FlowLayout(0));
		var e_c = new JPanel(new BorderLayout());
		var e_s = new JPanel(new GridLayout(1, 2, 5, 5));

		c.add(c_n, "North");
		c.add(c_c);

		e.add(e_n, "North");
		e.add(e_c);
		e.add(e_s, "South");

		c_n.add(lbl("STAGE", 0, 35));
		c_c.add(lbl("날짜 : " + getone("select p_date from perform where p_no =" + pno), 4, 15), "North");
		c_c.add(seat = new JPanel(new GridLayout(0, 1, 5, 5)));

		for (var s : "A,B,C,D,E,F".split(",")) {
			seats.put(s, new Seat(s));
			seat.add(seats.get(s));
		}

		e_n.add(lbl(getone("select p_name from perform where p_no = " + pno), 0, 25));
		e_c.add(peo = new JPanel(new FlowLayout(0)), "North");
		e_c.add(item = new JPanel(new FlowLayout(0)));
		e_c.add(tot = new JLabel("총금액 : 0", 2), "South");
		e_s.add(btn1 = btn("이전으로", e -> dispose()));
		e_s.add(btn2 = btn("다음으로", e -> {
			if (cnt == 0) {
				eMsg("좌석을 선택해주세요.");
				return;
			}

			if (cnt != pcnt) {
				eMsg("인원수에 맞게 좌석을 선택해주세요.");
				return;
			}

			if (e.getActionCommand().equals("다음으로")) {
				new Purchase(items, sum).addWindowListener(new Before(Stage.this));
			} else {
				String seats = "", dis = "";

				for (var k : items.keySet()) {
					if (seats.isEmpty()) {
						seats = k;
					} else {
						seats += "," + k;
					}

					if (dis.isEmpty()) {
						dis = items.get(k).dis + "";
					} else {
						dis += "," + items.get(k).dis + "";
					}
				}

				execute("update ticket set t_seat = '" + seats + "', t_discount = '" + dis + "' where t_no = " + tno);
				this.dispose();
			}
		}));

		peo.add(new JLabel("인원수 : "));
		for (int i = 0; i < people.length; i++) {
			peo.add(people[i] = new JLabel("○", 0));

			int idx = i;
			people[i].addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					for (int j = 0; j < people.length; j++) {
						people[j].setText("○");
					}

					pcnt = idx + 1;
					for (int k = 0; k < pcnt; k++) {
						people[k].setText("●");
					}
				};
			});
		}
		people[0].setText("●");

		item.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "선택한 좌석"));
		c_n.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
		e_n.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
		e.setBorder(new LineBorder(Color.BLACK));

		sz(c_n, 1, 100);
		sz(e, 350, 1);

		this.setVisible(true);
	}

	public Stage(String t_no) {
		this();

		this.tno = t_no;

		btn1.setText("취소하기");
		btn2.setText("수정하기");

		System.out.println(t_no);

		try {
			var rs = stmt.executeQuery("select * from ticket where t_no = " + t_no);
			rs.next();
			var tseat = rs.getString("t_seat").split(",");
			var tdis = rs.getString("t_discount").split(",");

			pcnt = cnt = tseat.length;
			Arrays.stream(people).forEach(a -> {
				a.setEnabled(false);
			});

			for (int i = 0; i < tseat.length; i++) {
				people[i].setText("●");

				var t = tseat[i].substring(0, 1);
				var d = tseat[i].substring(1);
				seats.get(t).lbl[toInt(d) - 1].setBackground(Color.ORANGE);

				var item = new Item(tseat[i], toInt(getone("select p_price from perform where p_no = " + pno)));
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class Seat extends JPanel {
		JLabel lbl[] = new JLabel[10];

		public Seat(String seat) {
			super(new GridLayout(1, 0, 5, 5));

			this.add(lbl(seat, 0, 15));

			for (int i = 0; i < lbl.length; i++) {
				var tseat = seat + String.format("%02d", i + 1);
				this.add(lbl[i] = new JLabel((i + 1) + "", 0));
				lbl[i].setName(tseat);
				lbl[i].setOpaque(true);
				lbl[i].setBorder(new LineBorder(Color.BLACK));

				if (!getone("select * from ticket where p_no = " + pno + " and t_seat like '%" + tseat + "%'")
						.isEmpty()) {
					lbl[i].setBackground(Color.LIGHT_GRAY);
				}

				lbl[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var lbl = (JLabel) e.getSource();

						if (lbl.getBackground().equals(Color.LIGHT_GRAY)) {
							eMsg("이미 예매된 좌석입니다.");
							return;
						} else if (lbl.getBackground().equals(Color.ORANGE)) {
							lbl.setBackground(null);
							cnt--;
							sum = 0;
							items.remove(tseat);
							item.removeAll();

							for (var k : items.keySet()) {
								item.add(items.get(k));
								sum += items.get(k).price;
							}

							tot.setText("총금액 : " + df.format(sum));

							item.repaint();
							item.revalidate();
						} else {
							cnt++;
							if (cnt > 5) {
								eMsg("더 이상 선택이 불가합니다.");
								cnt = 5;
								return;
							}
							lbl.setBackground(Color.ORANGE);

							var item = new Item(tseat,
									toInt(getone("select p_price from perform where p_no = " + pno)));
							items.put(tseat, item);
							Stage.this.item.add(item);
						}
					}
				});
			}
		}
	}

	class Item extends JPanel {
		JPanel n, c;
		JLabel lbl1, lbl2;
		JCheckBox chk[] = { new JCheckBox("청소년 할인 20%"), new JCheckBox("어린이 할인 40%"), new JCheckBox("장애인 할인 50%") };
		ButtonGroup bg = new ButtonGroup();

		String seat;
		int price, dis;

		public Item(String seat, int price) {
			super(new BorderLayout());
			this.seat = seat;
			this.price = price;

			sum += price;
			tot.setText("총금액 : " + df.format(sum));

			this.add(n = new JPanel(new BorderLayout()), "North");
			this.add(c = new JPanel(new FlowLayout(0)));

			n.add(lbl1 = new JLabel(seat + " : " + price, 0));
			n.add(lbl2 = new JLabel("▼", 0), "East");

			for (int i = 0; i < chk.length; i++) {
				c.add(sz(chk[i], 300, 30));
				bg.add(chk[i]);

				chk[i].addActionListener(e -> setPrice());
			}

			lbl2.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (lbl2.getText().equals("▼")) {
						lbl2.setText("▲");
						c.setVisible(true);
						sz(Item.this, 320, 120);
					} else {
						lbl2.setText("▼");
						c.setVisible(false);
						sz(Item.this, 320, 30);
					}
				}
			});

			this.setBorder(new LineBorder(Color.BLACK));
			sz(this, 320, 30);
		}

		void setPrice() {
			var p = toInt(getone("select p_price from perform where p_no = " + pno));

			p *= chk[0].isSelected() ? 0.8 : chk[1].isSelected() ? 0.6 : 0.5;
			dis = chk[0].isSelected() ? 1 : chk[1].isSelected() ? 2 : 3;
			price = p;
			sum = 0;

			for (var k : items.keySet()) {
				sum += items.get(k).price;
			}

			lbl1.setText(seat + " : " + df.format(price));
			tot.setText("총금액 : " + df.format(sum));
		}
	}
}
