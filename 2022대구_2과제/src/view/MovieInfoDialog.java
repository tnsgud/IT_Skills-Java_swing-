package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MovieInfoDialog extends BaseDialog {
	JScrollPane scr;
	ArrayList<Object> movie;
	JPanel content, main;

	public static void main(String[] args) {
		new MainFrame();
	}

	public MovieInfoDialog(ArrayList<Object> movie) {
		super(movie.get(1).toString(), 500, 400);
		this.movie = movie;

		add(scr = scroll(content = new JPanel()));
		content.add(main = new JPanel());

		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		main.setBackground(Color.white);
		content.setBackground(Color.white);

		movieInfo();
		synopsis();
		audience();
		review();

		opaque(main);
	}

	private void review() {
		var card = new CardLayout();
		var c = new JPanel(new BorderLayout(5, 5));
		var cc = new JPanel(card);
		var cs = new JPanel();
		var reviews = new JPanel(new GridLayout(0, 1));
		var lblTitle = lbl("리뷰", 2, 15);

		var rs = getRows(
				"select u.u_no, u_name, c_rate, c_text from comment c, user u where c.u_no = u.u_no and m_no=?",
				movie.get(0));

		c.add(lblTitle, "North");
		c.add(cc);
		c.add(cs, "South");

		int idx = 1;
		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			var tmpN = new JPanel(new FlowLayout(0));

			tmp.add(tmpN, "North");
			tmp.add(lbl(r.get(3).toString(), 2, 13));

			if (reviews.getComponentCount() == 5) {
				cc.add(reviews, idx + "");
				reviews = new JPanel(new GridLayout(0, 1));
				cs.add(lbl(idx + "", 0, 13, e -> {
					if (e.getButton() == 1) {
						for (var com : cs.getComponents()) {
							com.setForeground(Color.black);
						}

						var me = (JLabel) e.getSource();

						me.setForeground(Color.red);
						card.show(cc, me.getText());
					}
				}));
				idx++;
			}

			tmpN.add(lblRoundImg(getIcon("./지급자료/image/user/" + r.get(0) + ".jpg", 30, 30), 30, 30));
			tmpN.add(lbl(r.get(1).toString(), 2, 13));
			tmpN.add(lbl("<html><font color='yellow'>" + "★".repeat(toInt(r.get(2))) + "☆".repeat(5 - toInt(r.get(2))),
					2, 13));

			tmp.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

			reviews.add(tmp);
		}

		if (reviews.getComponentCount() > 0) {
			while (reviews.getComponentCount() < 5) {
				reviews.add(new JLabel());
			}

			cc.add(reviews, idx + "");
			cs.add(lbl(idx + "", 0, 13, e -> {
				if (e.getButton() == 1) {
					for (var com : cs.getComponents()) {
						com.setForeground(Color.black);
					}

					var me = (JLabel) e.getSource();

					me.setForeground(Color.red);
					System.out.println(me.getText());
					card.show(cc, me.getText());
				}
			}));
		}

		cs.getComponent(0).setForeground(Color.red);
		card.show(cc, "1");
		c.setAlignmentX(LEFT_ALIGNMENT);

		lblTitle.setBorder(new MatteBorder(0, 2, 1, 0, Color.black));

		main.add(sz(c, 400, 400));
	}

	private void audience() {
		var c = new JPanel(new BorderLayout(5, 5));
		var chart = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				float male = toInt(getOne(
						"select count(*) from reservation r, user u where r.u_no = u.u_no and u_gender=1 and m_no=?",
						movie.get(0)));
				float female = toInt(getOne(
						"select count(*) from reservation r, user u where r.u_no = u.u_no and u_gender=2 and m_no=?",
						movie.get(0)));
				var values = new float[] { male, female };
				int sum = (int) (male + female);
				int arc = 90;
				var col = new Color[] { Color.blue, Color.red };

				for (int i = 0; i < values.length; i++) {
					float a = (values[i] / sum) * 360 * -1;
					float per = (values[i] / sum) * 100;

					g2.setColor(col[i]);
					g2.fillRect(i * 100 + 5, 160, 10, 10);
					g2.fill(new Arc2D.Float(5, 5, 150, 150, arc, a, Arc2D.PIE));
					g2.setColor(Color.BLACK);
					g2.drawString("남,여".split(",")[i] + " : " + String.format("%.2f", per) + "%", i * 100 + 20, 170);

					arc += a;
				}

				g2.setColor(Color.white);
				g2.fill(new Arc2D.Float(25, 25, 150 - 40, 150 - 40, 0, 360, Arc2D.PIE));

				var rs = getRows(
						"select sum(if(year(now()) - year(u_birth)+1 >= 10 and year(now())- year(u_birth) + 1 < 20, 1, 0)) '10', sum(if(year(now()) - year(u_birth)+1 >= 20 and year(now())- year(u_birth) + 1 < 30, 1, 0)) '20',sum(if(year(now()) - year(u_birth)+1 >= 30 and year(now())- year(u_birth) + 1 < 40, 1, 0)) '30',sum(if(year(now()) - year(u_birth)+1 >= 40 and year(now())- year(u_birth) + 1 < 50, 1, 0)) '40',sum(if(year(now()) - year(u_birth)+1 >= 50 and year(now())- year(u_birth) + 1 < 60, 1, 0)) '50' from reservation r, user u where r.u_no = u.u_no and r.m_no = ?",
						movie.get(0)).get(0);
				sum = rs.stream().mapToInt(x -> toInt(x)).sum();
				int max = rs.stream().mapToInt(x -> toInt(x)).max().getAsInt();
				int he = 130;

				for (int i = 0; i < rs.size(); i++) {
					var per = (toInt(rs.get(i)) / (float) sum) * 100;
					var h = (toInt(rs.get(i)) / (float) max) * he;

					g2.setColor(toInt(rs.get(i)) == max ? Color.red : Color.DARK_GRAY);
					g2.fillRect(50 * i + 200, (int) (he - h + 20), 30, (int) h + 20);

					g2.setColor(Color.black);
					g2.drawString(((i + 1) * 10) + "대", 50 * i + 200, he + 50);
					g2.drawString(String.format("%.1f", per) + "%", 50 * i + 200, he - h + 10);
				}
			}
		};
		var lblTitle = lbl("주시청층", 2, 15);

		c.setAlignmentX(LEFT_ALIGNMENT);
		lblTitle.setBorder(new MatteBorder(0, 2, 1, 0, Color.black));

		c.add(lblTitle, "North");
		c.add(sz(chart, 450, 180));

		main.add(c);
	}

	private void synopsis() {
		var c = new JPanel(new BorderLayout(5, 5));
		var lblTitle = lbl("시놉시스", 2, 15);
		var area = new JTextArea();

		area.setText(movie.get(2).toString());
		area.setLineWrap(true);
		area.setEditable(false);
		area.setBackground(Color.white);

		c.setAlignmentX(LEFT_ALIGNMENT);

		lblTitle.setBorder(new MatteBorder(0, 2, 1, 0, Color.black));
		

		c.add(lblTitle, "North");
		c.add(area);

		main.add(c);
	}

	private void movieInfo() {
		var c = sz(new JPanel(new BorderLayout(20, 5)), 450, 200);
		var cc = new JPanel();
		var lblImg = new JLabel(getIcon("./지급자료/image/movie/" + movie.get(0) + ".jpg", 150, 200));
		int avg = (int) Math.round(Double.parseDouble(movie.get(8).toString()));
		var txt = String.format("<html>감독 : %s<br>장르 : %s / 기본 : %s, %s<br>별점 : <font color='yellow'>%s</font> %s",
				movie.get(6).toString(), mapToGenre(movie.get(3).toString()), m_age[toInt(movie.get(5))],
				movie.get(4) + "분", "★".repeat(avg) + "☆".repeat(5 - avg), "(" + movie.get(8).toString() + ")");
		var lblInfo = lbl(txt, 2);

		c.add(lblImg, "West");
		c.add(cc);

		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cc.add(lbl(movie.get(1).toString(), 2, 20), "North");
		cc.add(lblInfo);

		c.setAlignmentX(LEFT_ALIGNMENT);
		lblInfo.setBorder(
				new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), new EmptyBorder(10, 0, 0, 0)));

		main.add(c);
		main.add(Box.createVerticalStrut(20));
	}
}
