package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Status extends BasePage {

	Map map;
	Chart chart;

	public Status() {
		var n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var c = new JPanel(new BorderLayout());

		add(n, "North");
		add(c);

		for (var bcap : "앨범관리,로그아웃".split(",")) {
			n.add(btn(bcap, a -> {

			}));
		}

		c.add(chart = new Chart());
		c.add(map = new Map(), "West");
		c.setOpaque(false);
	}

	class Map extends JPanel {

		ArrayList<Integer> rank = new ArrayList<>();
		ArrayList<String> locName = new ArrayList<>();
		Queue<Point> que = new LinkedList<>();
		JLabel mapImg;

		Point[] localP = { new Point(157, 218), new Point(266, 192), new Point(440, 173), new Point(189, 454),
				new Point(468, 399), new Point(601, 465), new Point(384, 665), new Point(451, 732), new Point(305, 839),
				new Point(193, 1124) };

		int itb = 40;
		int visit[][];
		Color colour[] = { new Color(255, 0 * itb, 0 * itb), new Color(255, 1 * itb, 1 * itb),
				new Color(255, 2 * itb, 2 * itb), new Color(255, 3 * itb, 3 * itb), new Color(255, 4 * itb, 4 * itb),
				new Color(0 * itb, 0 * itb, 255), new Color(1 * itb, 1 * itb, 255), new Color(2 * itb, 2 * itb, 255),
				new Color(3 * itb, 3 * itb, 255), new Color(4 * itb, 4 * itb, 255) };

		BufferedImage img;

		public Map() {
			super(new BorderLayout());
			try {
				var rs = stmt.executeQuery(
						"select r.serial, count(h.serial) from user u, history h , region r where u.serial = h.user  and u.region = r.serial group by region order by count(h.serial) desc");
				while (rs.next()) {
					rank.add(rs.getInt(1) - 1);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				var rs = stmt.executeQuery("select * from region");
				while (rs.next()) {
					locName.add(rs.getString(2));
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				img = ImageIO.read(new File("./지급자료/images/지도.png"));
				visit = new int[img.getHeight()][img.getWidth()];
				rank.forEach(a -> {
					FloodFill(localP[a], colour[rank.indexOf(a)].getRGB());
				});
				mapImg = new JLabel(new ImageIcon(img.getScaledInstance(600, 721, Image.SCALE_DEFAULT)));
				add(mapImg);

				mapImg.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {

						Icon icon = mapImg.getIcon();
						BufferedImage bf = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
								BufferedImage.TYPE_INT_RGB);
						Graphics g = bf.createGraphics();
						icon.paintIcon(null, g, 0, 0);

						for (int i = 0; i < 5; i++) {
							int rgb = bf.getRGB(e.getX() + i, e.getY());
							for (int j = 0; j < 10; j++) {
								if (colour[j].getRGB() == rgb) {
									final int r_serial = rank.get(j) + 1;
									var c = chart.cp[0].c;
									chart.titlelbl.setText(locName.get(r_serial - 1) + "에 대한 상세 통계");
									c.removeAll();
									c.setLayout(new GridLayout(0, 1));
									chart.cp[1].setErrorlbl();

									try {
										var rs = stmt.executeQuery(
												"select c.serial, c.name ,count(h.serial) from category c, history h, user u, song s, album al where h.song = s.serial and u.serial = h.user and s.album = al.serial  and al.category = c.serial and u.region = '"
														+ r_serial
														+ "'  group by c.name order by count(h.serial) desc limit 0 ,5");
										int idx = 0, max = 0;
										while (rs.next()) {
											max = idx == 0 ? rs.getInt(3) : max;
											var line = chart.cp[0].line[idx];
											var width = (int) (((double) rs.getInt(3) / max) * 650);
											line = chart.new ChartLine(width,
													rs.getString(2) + "(" + rs.getInt(3) + "회)", chart.col[idx]);
											line.setName(rs.getString(1));
											line.addMouseListener(new MouseAdapter() {
												public void mousePressed(MouseEvent e) {
													var c = chart.cp[1].c;
													c.removeAll();
													c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
													var c_serial = ((Chart.ChartLine) e.getSource()).getName();
													try {
														var rs = stmt.executeQuery(
																"select ar.name ,count(h.serial) from category c, history h, user u, song s, album al, artist ar where h.song = s.serial and u.serial = h.user and s.album = al.serial  and al.category = c.serial and ar.serial = al.artist and u.region = '"
																		+ r_serial + "' and c.serial = '" + c_serial
																		+ "'  group by ar.serial order by count(h.serial) desc limit 0 ,5");
														int idx = 0, max = 0;
														while (rs.next()) {
															max = idx == 0 ? rs.getInt(2) : max;
															var line = chart.cp[1].line[idx];
															var width = (int) (((double) rs.getInt(2) / max) * 650);
															line = chart.new ChartLine(width,
																	rs.getString(1) + "(" + rs.getInt(2) + "회)",
																	chart.col[idx]);
															c.add(line);
															idx++;
														}
													} catch (SQLException e1) {
														// TODO Auto-generated catch block
														e1.printStackTrace();
													}

													c.revalidate();
													c.repaint();
												};
											});
											c.add(line);
											idx++;
										}
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									c.revalidate();
									c.repaint();
								}
							}
						}
					}

				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void FloodFill(Point pos, int col) {
			int sx = pos.x, sy = pos.y;
			que.add(new Point(sx, sy));
			visit[sy][sx] = 1;
			Point cPos;
			while (!que.isEmpty()) {
				cPos = que.poll();
				int x = cPos.x;
				int y = cPos.y;
				img.setRGB(x, y, col);
				for (int i = 1; i <= 5; i++) {
					if (img.getRGB(x + i, y) != new Color(0, 0, 0).getRGB() && visit[y][x + i] != 1) {
						que.add(new Point(x + i, y));
						visit[y][x + i] = 1;
					}
					if (img.getRGB(x, y + i) != new Color(0, 0, 0).getRGB() && visit[y + i][x] != 1) {
						que.add(new Point(x, y + i));
						visit[y + i][x] = 1;
					}
					if (img.getRGB(x - i, y) != new Color(0, 0, 0).getRGB() && visit[y][x - i] != 1) {
						que.add(new Point(x - i, y));
						visit[y][x - i] = 1;
					}
					if (img.getRGB(x, y - i) != new Color(0, 0, 0).getRGB() && visit[y - i][x] != 1) {
						que.add(new Point(x, y - i));
						visit[y - i][x] = 1;
					}

				}
			}
		}
	}

	class Chart extends JPanel {
		ChartP cp[] = new ChartP[2];
		Color col[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE };
		JLabel titlelbl;

		public Chart() {
			super(new BorderLayout());

			var c = new JPanel(new GridLayout(0, 1));

			add(titlelbl = lbl("지역을 선택해주세요.", JLabel.LEFT, Font.BOLD, 20), "North");
			add(c);

			for (int i = 0; i < cp.length; i++) {
				c.add(cp[i] = new ChartP("가장 많이 듣는 장르,가장 많이 재생된 아티스트".split(",")[i],
						"먼저 지역을 선택하세요,먼저 카테고리를 선택하세요".split(",")[i]));
			}
		}

		class ChartP extends JPanel {

			JPanel c;
			JLabel errlbl;
			ChartLine line[] = new ChartLine[5];

			public ChartP(String title, String errtxt) {
				setLayout(new BorderLayout());
				add(lbl(title, JLabel.LEFT, 0, 15), "North");
				add(c = new JPanel(new BorderLayout()));
				errlbl = lbl("<html><font color = \"red\">" + errtxt, JLabel.CENTER, 0, 15);
				setErrorlbl();
				setOpaque(false);
			}

			void setErrorlbl() {
				c.removeAll();
				c.setLayout(new BorderLayout());
				c.add(errlbl);
			}
		}

		class ChartLine extends JPanel {

			JPanel line;

			public ChartLine(int width, String title, Color col) {
				super(new BorderLayout());
				var c = new JPanel(new FlowLayout(FlowLayout.LEFT));
				add(lbl(title, JLabel.LEFT, 0, 10), "North");
				add(c);
				c.add(BasePage.size(line = new JPanel(), width, 30));
				line.setOpaque(true);
				line.setBackground(col);
			}
		}
	}

}
