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
import javax.swing.border.EmptyBorder;

public class Intro extends BaseFrame {
	ArrayList<GrayImage> imgs = new ArrayList<>();
	JScrollPane scr;
	JPanel m = new JPanel(new BorderLayout());

	public Intro() {
		super("지점소게", 800, 700);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(scr = sz(new JScrollPane(nw = new JPanel(new FlowLayout(0))), 145, 70), "West");
		n.add(btn("예약하기", a -> {
			new Reserve();
		}), "East");

		var rs = rs(
				"select t.t_no, t_name from theme t, cafe c where concat(',', c.t_no, ',') like concat('%,', t.t_no, '%') and c_no=?",
				cno);
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/테마/" + r.get(0) + ".jpg", 40, 40);
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var s = (GrayImage) e.getSource();

					imgs.forEach(i -> i.isSelecetd = false);

					s.isSelecetd = true;

					tno = toInt(s.getName());
					load();

					repaint();
				}
			});
			img.setToolTipText(rs.get(1) + "");
			img.setName(r.get(0) + "");
			nw.add(sz(img, 40, 40));
			imgs.add(img);
		}

		scr.getHorizontalScrollBar().setVisible(imgs.size() != 3);

		var l = lbl(rs("select * from cafe where c_no=?", cno).get(0).get(1) + "", 2, 25);
		l.setForeground(Color.orange);
		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		c.setBackground(Color.black);
		m.setBackground(Color.black);
		cc.setBackground(Color.black);

		imgs.get(0).isSelecetd = true;
		tno = toInt(imgs.get(0).getName());

		load();

		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	private void load() {
		cc.removeAll();
		m.removeAll();
		var rs = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0') from theme t, genre g, cafe c where t.g_no=g.g_no and t.t_no = ? and c_no = ?",
				tno, cno).get(0);
		cc.add(new JLabel(img("테마/" + tno + ".jpg", 500, 500)));
		cc.add(m);
		JLabel l1 = lblH("<html>" + rs.get(0) + "", 2, 0, 25), l2 = lblH("<html>" + rs.get(1) + "", 2, 0, 25),
				l3 = lbl("<html>", 2, 15);
		m.add(l1, "North");
		m.add(l2);
		m.add(l3, "South");
		var cap = "장르,최대 인원,시간,가격".split(",");
		var txt ="<html>";
		for (int i = 0; i < cap.length; i++) {
			txt += cap[i] +" : "+rs.get(i+2)+"<br/>";
		}
		txt+="</html>";
		l3.setText(txt);
		l1.setForeground(Color.white);
		l2.setForeground(Color.white);
		l3.setForeground(Color.white);
	}
}
