package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

	public SearchPage() {
		super();
		ui();
		events();
	}

	void notFound() {
		c.removeAll();
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		mf.swapPage(new SearchPage());
		mf.addNavigater();
		mf.setVisible(true);
	}

	void ui() {
		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		n.add(new JLabel(getIcon("./지급자료/Covid.png", 30, 30)), "West");
		n.add(searchField = new JTextField());
		add(c = new JPanel(new BorderLayout()));
		c.add(jsc = new JScrollPane(cw = new JPanel(new GridLayout(0, 1))), "West");
		c.add(cc = new JPanel(new BorderLayout()));
		cc.add(ccn = new JPanel(new FlowLayout(0)), "North");
		cc.add(ccc = new JPanel(new FlowLayout(0)));
		cc.add(cce = new JPanel(), "East");

		sz(jsc, 250, 1);

		search();

		cc.setBorder(new LineBorder(Color.LIGHT_GRAY));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		c.setBorder(new EmptyBorder(30, 0, 0, 0));
	}

	void search() {
		cw.removeAll();
		try {
			var rs = stmt.executeQuery(
					"select i.name, r.name, i.point, p.x, p.y, concat(date_format(open,'%H:%i'), '~', date_format(close,'%H:%i')) from institution i, region r, point p where i.region = r.no and i.point = p.no and i.name like '%"
							+ searchField.getText() + "%'");
			while (rs.next()) {
				var tmp = new JPanel(new FlowLayout(0)) {
					public String name, loc, pos, time;
				};
				var lbl = lbl(rs.getString(1), 2);
				tmp.setBorder(new LineBorder(Color.LIGHT_GRAY));
				tmp.add(lbl);
				tmp.name = rs.getString(1);
				tmp.loc = rs.getString(2);
				tmp.pos = rs.getInt(4) + ", " + rs.getInt(5);
				tmp.time = rs.getString(6);

				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						ccn.removeAll();
						ccc.removeAll();
						cce.removeAll();

						ccn.add(lbl("<html><font size='8'>" + lbl.getText() + "<br><font size='5'>인천광역시 " + tmp.loc
								+ tmp.pos + "<br>" + tmp.time, 2, 15));
						ccc.add(imgLbl = new JLabel(getIcon("지급자료/진료소사진/" + tmp.name + ".jpg", 450, 360)));
						cce.add(sz(btn("예약하기", event -> {
							
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
}
