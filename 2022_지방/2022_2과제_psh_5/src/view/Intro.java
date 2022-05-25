package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Intro extends BaseFrame {
	ArrayList<Object> cafe;
	ArrayList<GrayImage> imgs = new ArrayList<>();
	JScrollPane scr;

	public Intro() {
		super("지점소개", 900, 800);

		cafe = rs("select * from cafe where c_no=?", cno).get(0);

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		n.add(sz(scr = new JScrollPane(nw = new JPanel(new FlowLayout(0))), 150, 70), "West");
		n.add(ne = new JPanel(), "East");
		for (var r : cafe.get(2).toString().split(",")) {
			var img = new GrayImage("./Datafiles/테마/" + r + ".jpg", 40, 40);
			img.setToolTipText(rs("select t_name from theme where t_no=?", r).get(0).get(0) + "");
			img.setName(r);
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.isSelected = false);
					img.isSelected = true;

					tno = toInt(img.getName());
					load();

					repaint();
					revalidate();
				}
			});
			imgs.add(img);
			nw.add(sz(img, 40, 40));
		}
		scr.getHorizontalScrollBar().setVisible(imgs.size() > 3);
		imgs.get(0).isSelected = true;
		tno = toInt(imgs.get(0).getName());
		ne.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}));

		var l = lblH(cafe.get(1) + "", 2, 0, 25);
		l.setForeground(Color.orange);
		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		load();

		setVisible(true);
	}

	private void load() {
		cc.removeAll();

		var theme = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0') from theme t, cafe c, genre g where g.g_no=t.g_no and t.t_no=? and c.c_no=?",
				tno, cno).get(0);

		cc.add(new JLabel(img("테마/" + tno + ".jpg", 450, 600)));
		cc.add(ce = new JPanel(new BorderLayout()));

		var l1 = lblH(theme.get(0) + "", 2, 0, 20);
		var l2 = lblH("<html>" + theme.get(1), 2, 0, 20);
		var text = "<html>";
		var c = "장르,최대 인원,시간,가격".split(",");
		for (int i = 0; i < c.length; i++) {
			text += c[i] + " : " + theme.get(i + 2) + ",명,분,원".split(",")[i] + "<br/>";
		}
		var l3 = lbl(text, 2);

		ce.add(l1, "North");
		ce.add(l2);
		ce.add(l3, "South");

		Stream.of(l1, l2, l3).forEach(i -> i.setForeground(Color.white));

		this.c.setBackground(Color.black);
		cc.setBackground(Color.black);
		ce.setBackground(Color.black);

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		cno = "A-003";
		new Intro();
	}
}
