package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class Intro extends BaseFrame {
	JScrollPane scr;
	ArrayList<GrayImage> imgs = new ArrayList<GrayImage>();

	public Intro() {
		super("지점소개", 800, 800);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(sz(scr = new JScrollPane(nw = new JPanel(new FlowLayout(0))), 150, 70), "West");
		n.add(ne = new JPanel(), "East");

		var rs = rs("select t_no from cafe where c_no=?", cno);
		for (var n : rs.get(0).get(0).toString().split(",")) {
			var img = new GrayImage("./Datafiles/테마/" + n + ".jpg", 40, 40);
			img.setToolTipText(rs("select t_name from theme where t_no=?", n).get(0).get(0) + "");
			img.setName(n);
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.selected = false);
					img.selected = true;

					tno = toInt(img.getName());

					load();

					repaint();
				}
			});

			nw.add(sz(img, 40, 40));
			imgs.add(img);
		}

		imgs.get(0).selected = true;
		tno = toInt(imgs.get(0).getName());

		ne.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}));

		var l = lblH(rs("select c_name from cafe where c_no=?", cno).get(0).get(0) + "", 2, 0, 30);
		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		load();

		l.setForeground(Color.orange);
		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		scr.getVerticalScrollBar().setVisible(imgs.size() > 3);

		setVisible(true);
	}

	private void load() {
		cc.removeAll();
		var info = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, c_price from theme t, cafe c, genre g where g.g_no = t.g_no and c_no= ? and  t.t_no=?",
				cno, tno).get(0);

		var img = new JLabel(img("테마/" + tno + ".jpg", 400, 300));

		cc.add(img);
		cc.add(ce = new JPanel(new BorderLayout()));

		var l1 = lblH(info.get(0) + "", 2, 0, 20);
		var l2 = lblH("<html>" + info.get(1), 2, 0, 20);
		var l3 = lbl("", 2, 15);
		var txt = "<html>";

		var cap = "장르,최대 인원,시간,가격".split(",");
		for (int i = 0; i < cap.length; i++) {
			txt += cap[i] + " : " + (i == 3 ? new DecimalFormat("#,##0").format(info.get(i + 2)) : info.get(i + 2))
					+ (i == 1 ? "명" : i == 2 ? "분" : "원") + "<br/>";
		}
		l3.setText(txt);

		ce.add(l1, "North");
		ce.add(l2);
		ce.add(l3, "South");

		Stream.of(l1, l2, l3).forEach(i -> i.setForeground(Color.white));
		Stream.of(c, ce, cc).forEach(i -> i.setBackground(Color.black));

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		cno = "A-001";
		new Intro();
	}
}
