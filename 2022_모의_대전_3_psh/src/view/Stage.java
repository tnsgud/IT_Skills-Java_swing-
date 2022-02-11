package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class Stage extends BaseFrame {
	JPanel seat[][] = new JPanel[6][10], e_c_c;
	JLabel peo[] = new JLabel[5], price;
	ArrayList<Item> selected = new ArrayList<Item>();
	int cnt = 1;

	public Stage() {
		super("좌서", 950, 500);

		ui();
		data();

		setVisible(true);
	}

	private void data() {

	}

	private void ui() {
		var c_n = new JPanel();
		var c_c = new JPanel(new BorderLayout(10, 10));
		var c_c_c = new JPanel(new GridLayout(0, 11, 10, 10));

		var e_n = new JPanel(new FlowLayout(0));
		var e_c = new JPanel(new BorderLayout(20, 20));
		var e_s = new JPanel(new GridLayout(1, 0, 5, 5));
		var e_c_n = new JPanel(new FlowLayout(0));

		add(c = new JPanel(new BorderLayout(10, 10)));
		add(sz(e = new JPanel(new BorderLayout(10, 10)), 250, 0), "East");

		c.add(c_n, "North");
		c.add(c_c);

		e.add(e_n, "North");
		e.add(e_c);
		e.add(e_s, "South");

		c_n.add(lbl("STAGE", 0, 35));

		c_c.add(lbl("날짜 :" + getOne("select p_date from perform where p_no=?", pno), 4, 15), "North");
		c_c.add(c_c_c);

		var j = 0;
		for (var cap : "A,B,C,D,E,F".split(",")) {
			c_c_c.add(lbl(cap, 0, 15));
			for (int i = 0; i < 10; i++) {
				seat[j][i] = new JPanel(new BorderLayout());
				seat[j][i].add(lbl(i + 1 + "", 0, 15));
				seat[j][i].setBorder(new LineBorder(Color.black));
				seat[j][i].setName(j + "," + i);
				c_c_c.add(seat[j][i]);
			}
		}

		e_n.add(lbl(getOne("select p_name from perform where p_no=?", pno), 2, 25));

		e_c.add(e_c_n, "North");
		e_c.add(e_c_c = new JPanel(new FlowLayout()));
		e_c.add(price = lbl("총금액 : 0", 2, 15), "South");
		e_c_n.add(lbl("인원수 :", 2, 15));
		for (int i = 0; i < 5; i++) {
			peo[i] = lbl((i == 0 ? "●" : "○"), 2, 15);
			peo[i].setName(i + "");
			peo[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					cnt = toInt(((JLabel) e.getSource()).getName()) + 1;
					Stream.of(peo).forEach(l -> l.setText("○"));
					Stream.of(peo).filter(l -> toInt(l.getName()) < cnt).forEach(l -> l.setText("●"));
				}
			});
			e_c_n.add(peo[i]);
		}

		
		for (var cap : "이전으로,다음으로".split(",")) {
			e_s.add(btn(cap, a -> {
			}));
		}

		e.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		c_n.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		c_c.setBorder(new EmptyBorder(5, 5, 5, 5));
		e_n.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		e_c_c.setBorder(new TitledBorder(new LineBorder(Color.black), "선택한 좌석"));
	}

	class Item extends JPanel {
		JCheckBox chk[] = new JCheckBox[3];
		JLabel more;
		int price;
		
		public Item() {
			price = toInt(getOne("select p_price from perform where p_no=?", pno));
		}
	}
}