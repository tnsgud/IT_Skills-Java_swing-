package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PaintMap extends BaseFrame {
	int sx, sy, ex, ey, tmpx, tmpy;
	ArrayList<String> list = new ArrayList<>();
	ArrayList<Point> sp = new ArrayList<>(), ep = new ArrayList<>();

	public PaintMap() {
		super("", 1200, 800);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 'Z' || e.getKeyChar() == 'z') {
					sp.remove(sp.size() - 1);
					ep.remove(ep.size() - 1);

					list.remove(list.size() - 1);
					System.out.println(String.join(",", list));

					sx = sy = tmpx = tmpy = 0;
					repaint();
					revalidate();
				}
			}
		});
		setLayout(null);

		var rs1 = rs("select * from ping");
		try {
			while(rs1.next()) {
				add(img("마커.png", 50, 50)).setBounds(rs1.getInt(2), rs1.getInt(3), 50, 50);;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		add(new MyPanel()).setBounds(0, 0, this.getWidth(), this.getHeight());
		setVisible(true);
	}

	class MyPanel extends JPanel {

		public MyPanel() {
			setLayout(null);

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					sx = e.getX();
					sy = e.getY();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					ex = e.getX();
					ey = e.getY();

					list.add("{" + sx + "," + sy + "," + (ex - sx) + "," + (ey - sy) + "}");
					System.out.println(String.join(",", list));

					sp.add(new Point(sx, sy));
					ep.add(new Point(ex, ey));
				}
			});
			this.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					tmpx = e.getX();
					tmpy = e.getY();
					repaint();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			var g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.black);

			Point cs, ce;
			for (int i = 0; i < sp.size(); i++) {
				cs = sp.get(i);
				ce = ep.get(i);
				g.drawRect(cs.x, cs.y, ce.x - cs.x, ce.y - cs.y);
			}

			if (sx > 0 || sy > 0 || tmpx > 0 || tmpy > 0)
				g.drawRect(sx, sy, tmpx - sx, tmpy - sy);

		}
	}

	public static void main(String[] args) {
		new PaintMap();
	}
}
