package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Chart extends BaseFrame {
	ArrayList<GrayImage> imgs = new ArrayList<>();
	ArrayList<GrayImage> markers = new ArrayList<>();
	int ano, arc = 90, idx = -1;
	Color cols[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.pink,
			Color.magenta, Color.LIGHT_GRAY, Color.gray, Color.DARK_GRAY, Color.black, Color.white };

	public Chart() {
		super("지역별 예약 현황", 1200, 800);

		add(n = new JPanel(new BorderLayout()), "North");
		add(sz(w = new JPanel(null), 600, 600), "West");
		add(sz(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				arc = 90;
				int h = 250;
				var g2 = (Graphics2D) g;

				int sum = toInt(
						rs("select count(*) from reservation r, cafe c where c.c_no=r.c_no and c.a_no=?", ano)
								.get(0).get(0));
				var rs = rs(
						"select left(c.c_no, 1) as cno, count(*) as cnt, c_name from reservation r, cafe c where c.c_no=r.c_no and a_no=? group by cno order by cnt desc",
						ano);
				for (var r : rs) {
					var a = (int) Math.round(((double) toInt(r.get(1)) / (double) sum * 360) * -1);

					g2.setColor(cols[rs.indexOf(r)]);
					g2.fillArc(0, 250, 300, 300, arc, a);
					g2.fillRect(350, h - 20 + 5, 20, 20);
					g2.setColor(Color.black);
					g2.drawString(r.get(2).toString().split(" ")[0], 375, h);

					arc += a;
					h += 25;
				}
			}
		}, 600, 600));

		n.add(lbl("지역별 예약 현황", 0, 25));
		n.add(lbl("C H A R T", 0, 20), "South");

		var rs = rs(
				"select a_name, m_x, m_y, p_x, p_y from area a, ping p, map m where a.a_no=p.a_no and m.a_no = a.a_no");
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/지도/" + r.get(0) + ".png");
			var marker = new GrayImage("./Datafiles/마커.png", 25, 25);

			w.add(marker).setBounds(toInt(r.get(3)), toInt(r.get(4)), 25, 25);
			w.add(img).setBounds(toInt(r.get(1)), toInt(r.get(2)), img.w, img.h);

			w.setComponentZOrder(marker, 0);

			img.setName(r.get(0).toString());
			marker.setName(r.get(0).toString());
			marker.setToolTipText(r.get(0).toString());

			imgs.add(img);
			markers.add(marker);

			marker.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					markers.forEach(l -> l.isSelect = false);
					imgs.forEach(l -> l.isSelect = false);

					var s = (GrayImage) e.getSource();
					s.isSelect = true;

					imgs.stream().filter(l -> l.getName().equals(s.getName())).forEach(l -> {
						l.isSelect = true;
						ano = toInt(rs("select a_no from area where a_name=?", l.getName()));
					});

					repaint();
					revalidate();
				}
			});
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
