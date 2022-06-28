package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Chart extends BaseFrame {
	JLabel menBack, womenBack;
	BufferedImage men, women;
	double percents[];

	public Chart() {
		super("탑승자 분석", 1200, 600);

		setLayout(new GridBagLayout());

		try {
			men = ImageIO.read(new File("./datafiles/남자2.png"));
			women = ImageIO.read(new File("./datafiles/여자2.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		percents = getRows(
				"select count(*)/(select count(*) from companion) from companion c, reservation r where r.r_no = c.r_no group by c.c_division")
						.stream().flatMap(a -> a.stream()).mapToDouble(a -> Double.parseDouble(a.toString())).toArray();

		setLayout(new GridLayout(1, 0));

		add(c = new JPanel(new GridLayout(1, 0)));

		c.add(cw = new JPanel(new GridLayout(1, 0)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setColor(Color.gray);
				g2.drawLine(20, 430, 550, 430);
				
				g2.setColor(Color.black);
				g2.setFont(new Font("맑은 고딕", 1, 25)); 
				g2.drawString("남", 135, 460);
				g2.drawString("여", 430, 460);
			}
		});
		c.add(cc = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				var g2 = (Graphics2D) g;

				int w = 100;
				int baseX = 10, baseY = 130;

				for (int i = 0; i < 3; i++) {
					int totH = 300;
					int h = (int) (totH * percents[i]);

					g2.setColor(Color.LIGHT_GRAY);
					g2.fillRect(baseX, baseY, w, totH - h);

					g2.setColor(Color.gray);
					g2.fillOval(baseX, baseY - 15, w, 30);

					g2.setColor(new Color(0,0,200));
					g2.fillRect(baseX, baseY + totH - h, w, h);

					g2.setColor(new Color(0,0,200));
					g2.fillOval(baseX, baseY + totH - 15, w, 30);

					g2.setColor(new Color(0,0,150));
					g2.fillOval(baseX, baseY + totH - h - 15, w, 30);

					g2.setColor(Color.white);
					g2.setFont(new Font("맑은 고딕", 1, 30));
					g2.drawString(String.format("%.1f", percents[i] * 100) + "%", baseX + 10, 180);

					baseX += 150;
					baseY = 130;
				}
			}
		});

		cw.add(menBack = new JLabel(getIcon("./datafiles/남자1.png", men.getWidth(), men.getHeight())));
		cw.add(womenBack = new JLabel(getIcon("./datafiles/여자1.png", women.getWidth(), women.getHeight())));

		for (var rs : getRows(
				"select m_sex, round((count(*)/(select count(*) from companion)), 3)  from companion c, reservation r, member m where c.r_no = r.r_no and r.m_no = m.m_no group by m_sex")) {
			var percent = Double.parseDouble(rs.get(1).toString());

			if (toInt(rs.get(0)) == 0) {
				var h = (int) (men.getHeight() * percent);
				var l = new JLabel(new ImageIcon(men.getSubimage(0, 0, men.getWidth(), h))) {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);

						var g2 = (Graphics2D) g;

						g2.setFont(new Font("맑은 고딕", 1, 30));

						g2.drawString(String.format("%.1f", percent * 100) + "%", 55, 90);
					}
				};

				menBack.add(l).setBounds(62, 129, men.getWidth(), h);
			} else {
				var h = (int) (women.getHeight() * percent);
				var l = new JLabel(new ImageIcon(women.getSubimage(0, 0, women.getWidth(), h))) {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);

						var g2 = (Graphics2D) g;

						g2.setFont(new Font("맑은 고딕", 1, 30));

						g2.drawString(String.format("%.1f", percent * 100) + "%", 55, 90);
					}
				};

				womenBack.add(l).setBounds(61, 129, women.getWidth(), h);
			}
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Chart();
	}
}
