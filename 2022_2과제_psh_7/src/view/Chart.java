package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Chart extends BaseFrame {
	Color col[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.black, Color.pink,
			Color.magenta, Color.LIGHT_GRAY, Color.gray, Color.black };
	ArrayList<GrayImage> markers = new ArrayList<>(), imgs = new ArrayList<GrayImage>();

	public Chart() {
		super("지역별 예약 현황", 1500, 1000);

		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(sz(w = new JPanel(null), 600, 500), "West");
		add(sz(c = new JPanel(null) {
			@Override
			public void paint(Graphics g) {
				var g2 = (Graphics2D) g;
//				var rs = rs("select count(*) as cnt, c_name from reservation r, cafe c where c.c_no = r.c_no and a_no = ? group by c.c_no order by cnt desc", );
			}
		}, 500, 500));

		n.add(lblH("지역별 예약 현황", 0, 0, 30));
		n.add(lblH("C H A R T", 0, 0, 20), "South");

		var rs = rs(
				"select a_name, m_x,m_y,p_x,p_y, a.a_no from area a, map m, ping p where a.a_no = m.a_no and a.a_no = p.a_no");
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/지도/" + r.get(0) + ".png");
			var marker = new GrayImage("./Datafiles/마커.png", 30, 30);

			marker.setName(r.get(5) + "");
			marker.setToolTipText(r.get(0) + "");
			marker.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.selected = false);
					markers.forEach(i -> i.selected = false);

					img.selected = true;
					marker.selected = true;

					repaint();
				}
			});

			imgs.add(img);
			markers.add(marker);

			w.add(img).setBounds(toInt(r.get(1)), toInt(r.get(2)), img.w, img.h);
			w.add(marker).setBounds(toInt(r.get(3)), toInt(r.get(4)), marker.w, marker.h);

			w.setComponentZOrder(marker, 0);
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
