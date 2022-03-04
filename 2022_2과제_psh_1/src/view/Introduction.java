package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Introduction extends BaseFrame {
	JScrollPane scr;
	JPanel cc;
	JLabel cName, img, tName, tExplan, tAbout;
	ArrayList<BlackAndWhiteButton> btns = new ArrayList<>();

	public Introduction() {
		super("지점소개", 800, 700);

		ui();

		setVisible(true);
	}

	private void ui() {
		var nw = new JPanel(new FlowLayout());
		var ne = new JPanel(new FlowLayout());

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(ne, "East");

		var rs = rs("select * from cafe where c_no=?", cno);
		try {
			if (rs.next()) {
				for (var tno : rs.getString(3).split(",")) {
					var btn = new BlackAndWhiteButton(toInt(tno));
					btns.add(btn);
					nw.add(sz(btn, 45, 45));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (nw.getComponentCount() <= 3) {
			n.add(nw, "West");
		} else {
			n.add(sz(new JScrollPane(nw), 150, 75), "West");
		}

		ne.add(btn("예약하기", a -> {
			btns.forEach(btn->{
				if(btn.isSelected) {
					tno = btn.tno;
				}
			});
			
			new Reserve();
		}));

		c.add(cName = lbl(getOne("select c_name from cafe where c_no=?", cno), 2, 25), "North");
		c.add(img = new JLabel(), "West");
		c.add(cc = new JPanel(new BorderLayout()));

		cc.add(tName = lbl("", 2, 25), "North");
		cc.add(tExplan = lbl("", 2, 15));
		cc.add(tAbout = lbl("", 2, "", Font.BOLD, 15), "South");

		btns.get(0).doClick();

		c.setBorder(new EmptyBorder(10, 10, 10, 10));
		c.setBackground(Color.black);
		cName.setForeground(Color.orange);
		cc.setOpaque(false);

		cc.setBorder(new EmptyBorder(10, 10, 10, 10));
		tName.setForeground(Color.white);
		tExplan.setForeground(Color.white);
		tAbout.setForeground(Color.white);
	}

	class BlackAndWhiteButton extends JButton {
		int tno;
		boolean isSelected = false;
		BufferedImage master;
		BufferedImage grayScale;

		public BlackAndWhiteButton(int tno) {
			this.tno = tno;
			try {
				master = ImageIO.read(new File("./Datafiles/테마/" + tno + ".jpg"));
				grayScale = ImageIO.read(new File("./Datafiles/테마/" + tno + ".jpg"));
				var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
				op.filter(grayScale, grayScale);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setToolTipText(getOne("select t_name from theme where t_no=?", tno));

			setBorder(BorderFactory.createEmptyBorder());

			addActionListener(a -> {
				btns.forEach(btn -> {
					if (btn.isSelected) {
						btn.isSelected = false;
						btn.repaint();
						btn.revalidate();
					}
				});
				img.setIcon(img("/테마/" + tno + ".jpg").getIcon());
				var rs = rs(
						"select t_name, t_explan, g_name, t_personnel, t_time from theme t, genre g where g.g_no = t.g_no and t_no = ?",
						tno);
				try {
					if (rs.next()) {
						tName.setText(rs.getString(1));
						tExplan.setText("<html>" + rs.getString(2));
						var text = "<html>";
						var idx = 3;
						for (var cap : "장르,최대 인원,시간".split(",")) {
							text += cap + " : " + rs.getString(idx++).replaceAll("\r\n", "")
									+ (idx == 5 ? "명" : idx == 6 ? "분" : "") + "<br/>";
						}
						text += "가격 : " + format.format(toInt(getOne("select c_price from cafe where c_no=?", cno)))+"원"
								+ "</html>";
						tAbout.setText(text);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				isSelected = true;
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(isSelected ? master : grayScale, 0, 0, 45, 45, this);
		}
	}
}
