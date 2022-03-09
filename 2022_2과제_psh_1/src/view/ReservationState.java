package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.xml.transform.Source;

public class ReservationState extends BaseFrame {
	int ano, arc = 90;
	int point[][] = { { 280, 50 }, { 211, 86 }, { 235, 165 }, { 215, 170 }, { 155, 249 }, { 267, 299 }, { 274, 331 },
			{ 280, 236 }, { 338, 260 }, { 391, 411 }, { 298, 417 }, { 458, 456 }, { 433, 503 }, { 175, 375 },
			{ 104, 470 }, { 194, 500 }, { 280, 630 } };
	ArrayList<BlackAndWhiteImage> imgs = new ArrayList<ReservationState.BlackAndWhiteImage>();
	ArrayList<BlackAndWhiteImage> makers = new ArrayList<ReservationState.BlackAndWhiteImage>();
	Color cols[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.pink,
			Color.magenta, Color.LIGHT_GRAY, Color.gray, Color.DARK_GRAY, Color.black, Color.white };
	ArrayList<Integer> list = new ArrayList<Integer>();

	public ReservationState() {
		super("지역별 예약 현황", 1200, 800);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(sz(w = new JPanel(null), 600, 600), "West");
		add(sz(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				arc = 90;
				var h = 250;
				var g2 = (Graphics2D) g;
				var sum = toInt(
						getOne("select count(*) from reservation r, cafe c where c.c_no=r.c_no and a_no=?", ano));
				var rs = rs(
						"select left(c.c_no, 1) as cno, count(*) as cnt, c_name from reservation r, cafe c where c.c_no=r.c_no and a_no = ? group by cno order by cnt desc",
						ano);
				try {
					while (rs.next()) {
						var a = (int) Math.round((double) rs.getInt(2) / (double) sum * 360) * -1;
						g2.setColor(cols[rs.getRow() - 1]);
						g2.fillArc(0, 250, 300, 300, arc, a);
						g2.fillRect(350, h - 20 + 5, 20, 20);
						g2.setColor(Color.black);
						g2.drawString(rs.getString(3).split(" ")[0], 375, h);
						arc += a;
						h += 25;
//						break;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println(arc);
			}
		}, 600, 600));

		n.add(lbl("지역별 예약 현황", 0, 35));
		n.add(lbl("C H A R T", 0, 20), "South");

		var rs = rs("SELECT a_name, p_x, p_y FROM area a, ping p where a.a_no = p.a_no");
		try {
			while (rs.next()) {
				var img = new BlackAndWhiteImage("./Datafiles/지도/" + rs.getString(1) + ".png");
				var maker = new BlackAndWhiteImage("./Datafiles/마커.png", 25, 25);
				imgs.add(img);
				makers.add(maker);
				img.setName(rs.getString(1));
				maker.setName(rs.getString(1));
				maker.setToolTipText(rs.getString(1));
				maker.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var source = (BlackAndWhiteImage) e.getSource();
						makers.forEach(m -> {
							m.isSelected = m.getName().equals(source.getName());
							repaint();
						});
						source.isSelected = true;
						imgs.forEach(img -> {
							img.isSelected = img.getName().equals(source.getName());
							if (img.isSelected) {
								ano = toInt(getOne("select a_no from area where a_name=?", source.getName()));
							}
							repaint();
						});
					}
				});
				
				
				w.add(maker).setBounds(rs.getInt(2), rs.getInt(3), 25, 25);
				w.add(img).setBounds(point[rs.getRow() - 1][0], point[rs.getRow() - 1][1], img.width, img.height);
				w.setComponentZOrder(maker, 0);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.repaint();
	}

	class BlackAndWhiteImage extends JLabel {
		boolean isSelected = false;
		BufferedImage master, gray;
		int width, height;

		public BlackAndWhiteImage(String path) {
			try {
				master = ImageIO.read(new File(path));
				gray = ImageIO.read(new File(path));
				var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
				op.filter(gray, gray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.width = master.getWidth();
			this.height = master.getHeight();
			repaint();
		}

		public BlackAndWhiteImage(String path, int w, int h) {
			this(path);
			this.width = w;
			this.height = h;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(isSelected ? master : gray, 0, 0, width, height, this);
		}
	}

	public static void main(String[] args) {
		new ReservationState();
	}
}
