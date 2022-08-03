package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import view.BaseFrame.Before;

public class Map extends BaseFrame {
	JLabel map = new JLabel(getIcon("./datafiles/지역/0.png", 350, 550)), line, myCity;
	int fromX, fromY, toX, toY;

	public Map() {
		super("지도", 600, 750);

		add(hylbl("지역별 농산물관리현황", 0, 25), "North");
		add(c = new JPanel(null));

		c.add(line = new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(2));

				if (fromX != toX && fromY != toX) {
					g2.drawLine(fromX, fromY, toX, toY);
				}
			}
		}).setBounds(0, 0, getWidth(), getHeight());

		for (var rs : getRows("select * from city")) {
			var img = new JLabel(
					new ImageIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/지역/" + rs.get(0) + ".png")));
			var marker = lbl("<html>📌<br>" + rs.get(3), 0);

			c.add(img).setBounds(toInt(rs.get(1)), toInt(rs.get(2)), img.getIcon().getIconWidth(),
					img.getIcon().getIconHeight());
			c.add(marker).setBounds(toInt(rs.get(1)) + (img.getIcon().getIconWidth() / 2) - 20,
					toInt(rs.get(2)) + (img.getIcon().getIconHeight() / 2) - 30, 50, 50);

			if (toInt(rs.get(0)) == 2)
				marker.setLocation(marker.getX() + 20, marker.getY());
			else if (toInt(rs.get(0)) == 8)
				marker.setLocation(marker.getX() - 30, marker.getY() - 20);

			marker.setEnabled(!getOne(
					"select ifnull(sum(f.f_amount), 0) from city c left outer join town t on c.c_no=t.c_no left outer join user u on t.t_no = u.t_no left outer join farm f on u.u_no = f.u_no where c.c_no = ? group by c.c_name",
					rs.get(0)).equals("0"));

			c.setComponentZOrder(marker, 0);

			if (getOne("select c_no from town t, user u where t.t_no = u.t_no and u.u_no = ?", user.get(0))
					.equals(rs.get(0).toString())) {
				marker.setForeground(Color.yellow);
				myCity = marker;
				fromX = marker.getX();
				fromY = marker.getY();
				toX = fromX;
				toY = fromY;
			}

			if (marker.isEnabled()) {
				marker.setToolTipText("<html>야채 : " + getOne(
						"select count(f.b_no) from farm f, user u, town t where t.t_no = u.t_no and f.u_no = u.u_no and u.division = 1 and t.c_no = ? group by t.c_no",
						rs.get(0)) + "종<br>과일 : "
						+ getOne(
								"select count(f.b_no) from farm f, user u, town t where t.t_no = u.t_no and f.u_no = u.u_no and u.division = 2 and t.c_no = ? group by t.c_no",
								rs.get(0))
						+ "종");
			}

			marker.addMouseListener(new MouseAdapter() {
				boolean flag = false;

				@Override
				public void mousePressed(MouseEvent e) {
					if (!marker.isEnabled()) {
						eMsg("상품이 없습니다.");
						return;
					}

					if (e.getButton() == 3) {
						var pop = new JPopupMenu();
						var i1 = new JMenuItem(marker.getForeground().equals(Color.GREEN) ? "구매하기" : "판매하기");

						i1.addActionListener(a -> new DetailMap(toInt(rs.get(0)), a.getActionCommand()).addWindowListener(new Before(Map.this)));

						pop.add(i1);

						pop.show(marker, e.getX(), e.getY());
					}

					if (e.getButton() == 1 && !marker.equals(myCity)) {
						flag = true;
						toX = marker.getX();
						toY = marker.getY();

						for (var com : c.getComponents()) {
							com.setForeground(com == myCity ? Color.yellow : Color.black);
						}
						
						marker.setForeground(Color.green);

						repaint();
						revalidate();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (marker.getForeground().equals(Color.yellow) || flag) {
						return;
					}

					marker.setForeground(Color.red);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (marker.getForeground().equals(Color.yellow) || flag) {
						return;
					}

					marker.setForeground(Color.black);
				}
			});
		}

		c.setComponentZOrder(line, 0);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
