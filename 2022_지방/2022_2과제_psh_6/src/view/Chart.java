package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Chart extends BaseFrame {
	ArrayList<GrayImage> imgs = new ArrayList<>(), markers = new ArrayList<>();

	public Chart() {
		super("지역별 예약현황", 1200, 800);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(1, 0)));

		n.add(lblH("지역별 예약 현황", 0, 0, 30));
		n.add(lblH("C H A R T", 0, 0, 10), "South");

		c.add(cw = new JPanel(null));
		c.add(cc = new JPanel());

		var rs = rs(
				"select a_name, m_x,m_y,p_x,p_y from map m, ping p, area a where p.a_no = m.a_no and a.a_no = m.a_no");
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/지도/" + r.get(0) + ".png");
			var marker = new GrayImage("./Datafiles/마커.png", 30, 30);
			marker.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i->i.isSelected=false);
					markers.forEach(i->i.isSelected=false);
					
					marker.isSelected = true;
					img.isSelected = true;

					repaint();
				}
			});

			imgs.add(img);
			markers.add(marker);
			
			cw.add(img).setBounds(toInt(r.get(1)), toInt(r.get(2)), img.w, img.h);
			cw.add(marker).setBounds(toInt(r.get(3)), toInt(r.get(4)), 30, 30);
			cw.setComponentZOrder(marker, 0);
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
