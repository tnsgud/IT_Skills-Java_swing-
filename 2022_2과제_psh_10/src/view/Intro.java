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
	ArrayList<GrayImage> imgs = new ArrayList<GrayImage>();
	JScrollPane scr;

	public Intro() {
		super("지점소개", 800, 800);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(sz(scr = new JScrollPane(nw = new JPanel()), 150, 70), "West");
		n.add(ne = new JPanel(), "East");
		var rs = rs("select c_name,t_no from cafe where c_no = ?", cno).get(0);
		for (var n : rs.get(1).toString().split(",")) {
			var img = new GrayImage("./Datafiles/테마/" + n + ".jpg", 40, 40);
			img.setName(n);
			img.setToolTipText(rs("select t_name from theme t where t_no=?", n).get(0).get(0) + "");
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.selected = false);
					img.selected = true;
					tno = toInt(img.getName());
					load();
				}
			});
			nw.add(sz(img, 40, 40));
			imgs.add(img);
		}
		scr.getHorizontalScrollBar().setVisible(imgs.size() > 3);
		imgs.get(0).selected = true;
		tno = toInt(imgs.get(0).getName());
		ne.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}));

		var l = lblH(rs.get(0) + "", 2, 0, 30);
		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		load();

		l.setForeground(Color.orange);
		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void load() {
		cc.removeAll();

		var info = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0') from cafe c, theme t, genre g where g.g_no = t.g_no and t.t_no = ? and c.c_no=?",
				tno, cno).get(0);

		cc.add(new JLabel(img("테마/" + tno + ".jpg", 400, 600)));
		var p = new JPanel(new BorderLayout());
		var l1 = lblH(info.get(0) + "", 2, 0, 20);
		var l2 = lblH("<html>" + info.get(1), 2, 0, 15);
		var txt = "<html>";
		var cap = "장르,최대 인원,시간,가격".split(",");
		var t = " ,명,분,원".split(",");
		for (int i = 0; i < cap.length; i++) {
			txt += cap[i] + " : " + info.get(i + 2) + t[i] + "<br/>";
		}
		var l3 = lbl(txt, 2, 15);
		p.add(l1, "North");
		p.add(l2);
		p.add(l3, "South");

		cc.add(p);

		Stream.of(c, cc, p).forEach(a -> a.setBackground(Color.black));
		Stream.of(l1, l2, l3).forEach(a -> a.setForeground(Color.white));

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		cno = "A-001";
		new Intro();
	}
}
