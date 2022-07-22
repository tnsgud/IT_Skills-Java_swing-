package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class SheatSelect extends BaseFrame {
	HashMap<String, JLabel> seat = new HashMap<>();
	ArrayList<Object> movie = getRows("select * from movie where m_no = ?", m_no).get(0);
	ArrayList<Item> items = new ArrayList<>();
	ArrayList<String> select = new ArrayList<>();
	int price = 0;
	JLabel priceLbl = lbl("총 합계 : 0원", 2);

	public SheatSelect() {
		super("좌석", 1000, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridBagLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(nw = new JPanel(new BorderLayout(5, 5)), "West");
		n.add(ne = new JPanel(new FlowLayout(1)), "East");

		nw.add(new JLabel(
				getIcon("./datafile/영화/" + getOne("select m_name from movie where m_no=?", m_no) + ".jpg", 70, 100)),
				"West");
		{
			var tmp = new JPanel(new GridLayout(0, 1));
			var data = getRows(
					"select m_rating, m_name, p_name, date_format(sc_date, '%y.%m.%d'), weekday(sc_date), sc_time, t_name, sc_theater from movie m, pomaes p, schedule sc, theater t where m.m_no = sc.m_no and sc.p_no = p.p_no and sc.t_no = t.t_no and sc_no = ?",
					sc_no).get(0);

			var tmp1 = new JPanel(new FlowLayout(0));
			tmp1.add(new JLabel(getIcon("./datafile/아이콘/" + data.get(0) + ".png", 20, 20)));
			tmp1.add(lbl(data.get(1) + "(" + data.get(2) + ")", 2));

			var tmp2 = new JPanel(new FlowLayout(0));
			var l = lbl(data.get(3) + "(" + "월,화,수,목,금,토,일".split(",")[toInt(data.get(4))] + ")", 2);

			l.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
			tmp2.add(sz(l, 80, 20));
			tmp2.add(lbl(data.get(5).toString(), 2));

			tmp.add(tmp1);
			tmp.add(tmp2);
			tmp.add(lbl(data.get(6) + "·" + data.get(7), 2, 12));

			nw.add(tmp);
		}

		var cap = "성인,청소년,시니어,장애인".split(",");
		var pr = new int[] { 15000, 13000, 10000, 7000 };
		for (int i = 0; i < cap.length; i++) {
			var item = new Item(cap[i], pr[i], i + 1);

			items.add(item);
			ne.add(item);
		}

		c.add(cc = new JPanel(new GridLayout(0, 11, 5, 5)));

		cap = "A,B,C,D,E,F".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(sz(lbl(cap[i], 0), 50, 50));
			for (int j = 0; j < 10; j++) {
				var key = cap[i] + "-" + (String.format("%02d", j + 1));
				seat.put(key, lbl(j + 1 + "", 0));
				seat.get(key).setBackground(Color.white);
				seat.get(key).setOpaque(true);

				seat.get(key).addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var me = (JLabel) e.getSource();

						if (me.getBackground().equals(Color.LIGHT_GRAY)) {
							return;
						}

						if (me.getBackground().equals(Color.red)) {
							select.remove(key);
							me.setBackground(Color.white);
						} else {
							select.add(key);
							me.setBackground(Color.red);
						}
					}
				});

				cc.add(seat.get(key));
			}
		}

		for (var rs : getRows("select r_seat from reservation where sc_no = ?", sc_no)) {
			for (var key : rs.get(0).toString().split(",")) {
				key = key.trim();
				seat.get(key).setBackground(Color.lightGray);
			}
		}

		s.add(priceLbl);
		s.add(btn("결제하기", a -> {
			if (items.stream().filter(e -> toInt(e.cnt.getText()) != 0).count() == 0) {
				eMsg("인원을 선택해주세요.");
				return;
			}

			if (select.size() == 0) {
				eMsg("좌석을 선택해주세요.");
				return;
			}

			iMsg("예매가 완료되었습니다.");

			var list = items.stream().filter(e -> !e.cnt.getText().equals("0")).toArray(Item[]::new);
			execute("insert into reservation values(0, ?, ?, ?, ?, ?, ?, ?)", user.get(0),
					String.join(",", Stream.of(list).map(e -> e.div + "").toArray(String[]::new)), sc_no, select.size(),
					String.join(",", select.toArray(String[]::new)), LocalDate.now().toString(),
					LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm")));
		}), "East");

		setVisible(true);
	}

	class Item extends JPanel {
		String cap;   
		JPanel c;
		JLabel cnt, mi, pl;
		int p, div;

		public Item(String cap, int p, int div) {
			super(new FlowLayout(2));

			this.cap = cap;
			this.p = p;
			this.div = div;

			add(lbl(cap, 0));
			add(c = new JPanel(new FlowLayout(1)));

			c.add(mi = lbl("-", 0));
			c.add(cnt = lbl("0", 0));
			c.add(pl = lbl("+", 0));

			c.setBorder(new LineBorder(Color.LIGHT_GRAY));

			mi.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (cap.equals("청소년") && movie.get(2).toString().equals("청불")) {
						return;
					}

					if (toInt(cnt.getText()) == 0) {
						return;
					}

					cnt.setText(toInt(cnt.getText()) - 1 + "");

					price -= p;

					priceLbl.setText("총 합계 : " + new DecimalFormat("#,##0").format(price) + "원");
				}
			});

			pl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (cap.equals("청소년") && movie.get(2).toString().equals("청불")) {
						return;
					}

					if (toInt(cnt.getText()) == 8) {
						return;
					}

					cnt.setText(toInt(cnt.getText()) + 1 + "");

					price += p;

					priceLbl.setText("총 합계 : " + new DecimalFormat("#,##0").format(price) + "원");
				}
			});
		}
	}

	public static void main(String[] args) {
		new Reserve();
	}
}
