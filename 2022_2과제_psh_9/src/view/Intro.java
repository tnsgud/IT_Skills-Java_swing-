package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class Intro extends BaseFrame {
	JScrollPane scr;
	ArrayList<GrayImage> imgs = new ArrayList<>();

	public Intro() {
		super("지점소개", 800, 800);

		var info = rs("select t_no, c_name from cafe where c_no=?", cno).get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(sz(scr = new JScrollPane(nw = new JPanel()), 150, 70), "West");
		n.add(ne = new JPanel(), "East");
		ne.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}));

		for (var r : info.get(0).toString().split(",")) {
			var l = new GrayImage("./Datafiles/테마/" + r + ".jpg", 40, 40);
			l.setName(r);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.selected = false);
					l.selected = true;
					tno = toInt(l.getName());
					load();

					repaint();
				}
			});
			nw.add(sz(l, 40, 40));
			imgs.add(l);
		}

		scr.getHorizontalScrollBar().setVisible(imgs.size() > 3);

		imgs.get(0).selected = true;
		tno = toInt(imgs.get(0).getName());

		var l = lbl(info.get(1) + "", 2, 20);
		l.setForeground(Color.orange);

		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		load();

		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void load() {
		cc.removeAll();

		var info = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0') from theme t, genre g, cafe c where c_no=? and t.t_no=?",
				cno, tno).get(0);

		var img = new JLabel(img("테마/" + tno + ".jpg"));
		var m = new JPanel(new BorderLayout());
		cc.add(img);
		cc.add(m);

		var l1 = lblH(info.get(0) + "", 2, 0, 30);
		var l2 = lblH("<html>" + info.get(1), 2, 0, 20);
		var txt = "<html>";
		var cap = "장르,최대 인원,시간,가격".split(",");
		for (int j = 0; j < cap.length; j++) {
			txt += cap[j] + " : " + info.get(j + 2) + (j == 0 ? "" : j == 1 ? "명" : j == 2 ? "분" : "원") + "<br/>";
		}
		var l3 = lbl(txt, 2, 12);

		m.add(l1, "North");
		m.add(l2);
		m.add(l3, "South");

		Stream.of(l1, l2, l3).forEach(i -> i.setForeground(Color.white));

		Stream.of(c, cc, m).forEach(i -> i.setBackground(Color.black));
	}
}
