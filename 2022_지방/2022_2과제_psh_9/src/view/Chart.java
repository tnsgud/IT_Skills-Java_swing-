package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Chart extends BaseFrame {
	ArrayList<GrayImage> imgs = new ArrayList<>();
	ArrayList<GrayImage> markers = new ArrayList<>();

	public Chart() {
		super("지역별 예약 현황", 1200, 900);

		add(n = new JPanel(new BorderLayout()), "North");
		add(sz(w = new JPanel(null), 600, 600), "West");
		add(sz(c = new JPanel(null), 600, 600));

		n.add(lblH("지역별 예약 현황", 0, 0, 30));
		n.add(lblH("C H A R T", 0, 0, 20), "South");

		var rs = rs(
				"select a_name, m_x, m_y, p_x, p_y from area a, map m, ping p where a.a_no = m.a_no and a.a_no = p.a_no");
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/지도/" + r.get(0) + ".png");
			var marker = new GrayImage("./Datafiles/마커.png", 30, 30);

			w.add(img).setBounds(toInt(r.get(1)), toInt(r.get(2)), img.w, img.h);
			w.add(marker).setBounds(toInt(r.get(3)), toInt(r.get(4)), 30, 30);

			marker.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.selected = false);
					markers.forEach(i -> i.selected = false);

					marker.selected = true;
					img.selected = true;

					repaint();
				}
			});
			marker.setToolTipText(r.get(0) + "");

			w.setComponentZOrder(marker, 0);

			imgs.add(img);
			markers.add(marker);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
