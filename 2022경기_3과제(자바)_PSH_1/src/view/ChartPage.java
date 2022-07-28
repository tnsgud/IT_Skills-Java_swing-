package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChartPage extends BasePage {
	JLabel genrelbl, add;
	ArrayList<String> genre = new ArrayList<>();
	Color cols[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.blue, Color.magenta, Color.pink,
			Color.gray };
	int arc = 90, tot = 0;

	public ChartPage() {
		super("차트");

		add(c = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				var g2 = (Graphics2D) g;

				int h = 50;
				var rs = getRows(
						"select g_name, count(*) from library l, game g where l.g_no = g.g_no and g_genre regexp ? group by g.g_no order by count(*) desc, g.g_no limit 8",
						"(" + String.join("|", genre.toArray(String[]::new)) + ")");
				genrelbl.setText(genre.stream().map(gen -> g_genre[toInt(gen)]).collect(Collectors.joining(", ")));
				tot = rs.stream().mapToInt(r -> toInt(r.get(1))).sum();
				for (int i = 0; i < rs.size(); i++) {
					int a = (int) 360.0 * toInt(rs.get(i).get(1)) / tot;

					g2.setColor(cols[i]);
					g2.fillArc(100, 50, 300, 300, arc, -a);
					g2.fillRect(500, h, 20, 20);
					g2.setFont(new Font("맑은 고딕", 0, 12));
					g2.setColor(Color.white);
					g2.drawString(rs.get(i).get(0) + " : " + rs.get(i).get(1) + "개", 540, h + 10);
					arc -= a;
					h += 30;
					System.out.println(i + ":" + arc);
				}
			}
		});
		add(s = new JPanel(new FlowLayout(0)), "South");

		s.add(lbl("선택된 장르 : ", 2, 20));
		s.add(genrelbl = lbl("공포", 2));
		s.add(add = new JLabel(getIcon("./datafiles/기본사진/10.png", 50, 50)));

		genre.add("1");

		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new GenreSelect(genre).setVisible(true);
			}
		});

		mf.repaint();
	}
}
