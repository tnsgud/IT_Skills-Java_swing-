package view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import view.BaseFrame.Before;

public class DetailMap extends BaseFrame {
	JPanel img;

	public DetailMap(int cNo, String type) {
		super("", 1100, 800);

		var cName = getOne("select c_name from city where c_no = ?", cNo);
		setTitle(cName + "세부 지도");

		add(hylbl(cName, 0, 30), "North");
		add(img = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				try {
					g2.drawImage(ImageIO.read(new File("./datafiles/지역/" + cNo + ".png")), 0, 0, getWidth(),
							getHeight(), null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		if (type.equals("구매하기")) {
			for (var rs : getRows("select * from town where c_no = ?", cNo)) {
				var tmp1 = new JPanel(new BorderLayout());
				var tmp2 = new JPanel(new GridLayout(0, 3));

				for (var r : getRows(
						"select c_name, t_name, b_name, b_img, b.b_no, sum(f_quantity) qsum from base b, user u, farm f, city c, town t where u.u_no = f.u_no and u.t_no = t.t_no and b.b_no = f.b_no and c.c_no = t.c_no and b.division <> ? and t.t_no = ? group by c_name, t_name, b_name",
						user.get(5), rs.get(0))) {
					var subImg = new JLabel(getIcon(r.get(3), 45, 60));

					subImg.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							new Chart(toInt(r.get(4))).addWindowListener(new Before(DetailMap.this));
						}
					});

					tmp2.add(subImg);
				}

				if (tmp2.getComponentCount() == 0)
					img.remove(tmp1);

				tmp1.add(new JScrollPane(tmp2));
				tmp1.add(lbl(rs.get(4).toString(), 0), "South");

				img.add(tmp1).setBounds(toInt(rs.get(2)) - 80, toInt(rs.get(3)) - 70, 160, 80);
			}
		} else {
			for (var rs : getRows("select * from town where c_no = ?", cNo)) {
				var tmp1 = new JPanel(new BorderLayout());
				var tmp2 = new JPanel(new GridLayout(0, 3));

				for (var r : getRows(
						"select u.u_no, b.b_no, b_img, sum(f.f_quantity) qsum from base b, user u, farm f, city c, town t where u.u_no = f.u_no and u.t_no = t.t_no and b.b_no = f.b_no and c.c_no = t.c_no and b.division = ? and t.t_no = ? group by c_name, t_name, b_name",
						user.get(5), rs.get(0))) {
					var img = new JLabel(getIcon(r.get(2), 45, 60));

					tmp2.add(img);

					img.setEnabled(user.get(0).toString().equals(r.get(0).toString()));
					img.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							new BaseMange(toInt(r.get(1))).addWindowListener(new Before(DetailMap.this));
						}
					});
				}

				if (tmp2.getComponentCount() == 0) {
					img.remove(tmp1);
				}

				tmp1.add(new JScrollPane(tmp2));
				tmp1.add(lbl(rs.get(4).toString(), 0), "South");

				img.add(tmp1).setBounds(toInt(rs.get(2)) - 80, toInt(rs.get(3)) - 70, 160, 80); 
			}
		}

		setVisible(true);
	}
}
