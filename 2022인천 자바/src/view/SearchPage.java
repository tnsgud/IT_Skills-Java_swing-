package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SearchPage extends BasePage {
	JPanel ccn, ccc, cce;
	JTextField searchField;
	JScrollPane jsc;
	JLabel imgLbl;

	double distance = 0;

	public SearchPage() {
		super();
		ui();
		events();
	}

	void notFound() {
		ccn.add(lbl("검색결과가 없습니다.", 0, 20));
		revalidate();
		repaint();
	}

	void ui() {
		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(new JLabel(getIcon("./datafiles/Covid.png", 30, 30)), "West");
		n.add(searchField = new JTextField());

		c.add(jsc = new JScrollPane(cw = new JPanel(new GridLayout(0, 1))), "West");
		c.add(cc = new JPanel(new BorderLayout()));

		cc.add(ccn = new JPanel(new FlowLayout(0)), "North");
		cc.add(ccc = new JPanel(new FlowLayout(0)));
		cc.add(cce = new JPanel(new FlowLayout(1)), "South");

		search();

		sz(jsc, 250, 1);

		setBorder(new EmptyBorder(5, 5, 5, 5));
		c.setBorder(new EmptyBorder(30, 0, 0, 0));
		cc.setBorder(new LineBorder(Color.LIGHT_GRAY));
	}

	void distance() {
		try {
			double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

			var u_rs = stmt.executeQuery(
					"select x, y from point p, user u where p.no = u.point and u.point = '" + upoint + "'");
			if (u_rs.next()) {
				x1 = u_rs.getDouble(1);
				y1 = u_rs.getDouble(2);
			}
			var b_rs = stmt.executeQuery(
					"select x, y from point p, building b where p.no = b.point and b.point = '" + bpoint + "'");
			if (b_rs.next()) {
				x2 = b_rs.getDouble(1);
				y2 = b_rs.getDouble(2);
			}

			distance = Point.distance(x1, y1, x2, y2);
		} catch (SQLException e2) {
		}
	}

	void search() {
		cw.removeAll();

		try {
			var rs = stmt.executeQuery(
					"select b.name, p.x, p.y, concat(date_format(bi.open, '%H:%i'), '~', date_format(bi.close, '%H:%i')), b.point from building b, building_info bi, point p where b.point = p.no and bi.building = b.no and b.name like '%"
							+ searchField.getText() + "%'");
			while (rs.next()) {
				var tmp = new JPanel(new FlowLayout(0)) {
					String name, pos, time, point;
				};
				var lbl = lbl(rs.getString(1), 2);

				tmp.setBorder(new LineBorder(Color.LIGHT_GRAY));
				tmp.add(lbl);

				tmp.name = rs.getString(1);
				tmp.pos = rs.getInt(2) + ", " + rs.getInt(3);
				tmp.time = rs.getString(4);
				tmp.point = rs.getString(5);

				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						ccn.removeAll();
						ccc.removeAll();
						cce.removeAll();

						bpoint = getOne("select * from point p, building b where p.no = b.point and b.point = '"
								+ tmp.point + "'");
						distance();
						
						System.out.println(bpoint);

						ccn.add(lbl("<html><font size='8'>" + lbl.getText() + "<br><font size='5'>" + tmp.pos + "<br>"
								+ tmp.time + "<br>거리 : " + (int) distance + "m", 2, 15));
						ccc.add(imgLbl = new JLabel(getIcon("datafiles/진료소사진/" + tmp.name + ".jpg", 700, 300)));
						cce.add(sz(btn("길찾기", event -> {
							new MapDialog(toInt(upoint), toInt(bpoint)).setVisible(true);
						}), 150, 30));
						cce.add(sz(btn("예약하기", event -> {
							bpoint = tmp.point;
							mf.swapPage(new ReservePage(tmp.name));
						}), 150, 30));

						imgLbl.setBorder(new LineBorder(Color.BLACK));

						repaint();
						revalidate();
					}
				});

				cw.add(sz(tmp, 150, 30));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cw.getComponents().length <= 10)
			while (cw.getComponents().length <= 10)
				cw.add(new JPanel());

		repaint();
		revalidate();
	}

	void events() {
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				search();
			}
		});
	}

	public static void main(String[] args) {
		upoint = "304";
		mf.swapPage(new SearchPage());
		mf.setVisible(true);
	}
}
