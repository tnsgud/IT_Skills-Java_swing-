package view;

import java.awt.BorderLayout;
import java.awt.Color;
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
	ArrayList<GrayImage> imgs = new ArrayList<GrayImage>();

	public Intro() {
		super("지점 소개", 800, 700);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(sz(scr = new JScrollPane(nw = new JPanel()), 150, 70), "West");
		n.add(ne = new JPanel(), "East");
		ne.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}));

		var rs = rs(
				"select t.t_no, t_name from cafe c, theme t where concat(',', c.t_no, ',') like concat('%,', t.t_no, ',%') and c_no=?",
				cno);
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/테마/" + r.get(0) + ".jpg", 40, 40);
			img.setToolTipText(r.get(1) + "");
			img.setName(r.get(0) + "");
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var s = (GrayImage) e.getSource();
					imgs.forEach(l -> l.isSelect = false);
					s.isSelect = true;
					tno = toInt(s.getName());
					load();

				}
			});
			imgs.add(img);
			nw.add(sz(img, 40, 40));
		}

		imgs.get(0).isSelect = true;
		tno = toInt(imgs.get(0).getName());

		load();

		c.setBackground(Color.black);
		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		scr.getHorizontalScrollBar().setVisible(nw.getComponentCount() > 3);

		setVisible(true);
	}

	void load() {
		c.removeAll();
		var l = lblH(rs("select c_name from cafe where c_no=?", cno).get(0).get(0) + "", 2, 0, 25);
		l.setForeground(Color.orange);
		c.add(l, "North");
		var rs = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0')  from cafe c, theme t, genre g where t.g_no=g.g_no and t.t_no=? and c_no=?",
				tno, cno).get(0);
		c.add(new JLabel(img("테마/" + tno + ".jpg", 500, 500)), "West");
		c.add(cc = new JPanel(new BorderLayout()));

		var nl = lblH(rs.get(0) + "", 2, 0, 15);
		var cl = lblH("<html>" + rs.get(1) + "</html>", 2, 0, 15);
		var txt = "<html>";
		var cap = "장르,최대 인원,시간,가격".split(",");
		for (int i = 0; i < cap.length; i++) {
			txt += cap[i] + " : " + rs.get(i + 2) + (i == 1 ? "명" : i == 2 ? "분" : "원") + "<br/>";
		}
		var sl = lblH(txt, 2, 0, 15);
		cc.add(nl, "North");
		cc.add(cl);
		cc.add(sl, "South");
		
		Stream.of(nl, cl, sl).forEach(a->a.setForeground(Color.white));

		cc.setBackground(Color.black);

		repaint();
		revalidate();
	}
}
