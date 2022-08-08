package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Puzzle extends BaseFrame {
	BufferedImage image;
	ArrayList<JLabel> ori = new ArrayList<>(), shuf = new ArrayList<>();
	JLabel select;
	int x, y;
	boolean flag = false;

	public Puzzle(Purchase purchase) {
		super("퍼즐", 900, 400);

		setResizable(true);

		add(w = new JPanel(new GridLayout(0, 3)) {
			@Override
			public void revalidate() {
				super.revalidate();

				for (var img : shuf) {
					w.add(img);
				}

				if (select == null)
					return;

				for (int k = 0; k < ori.size(); k++) {
					if (ori.get(k).getIcon() != shuf.get(k).getIcon()) {
						return;
					}
				}

				iMsg("퍼즐을 완성하였습니다. 축하합니다.");
				purchase.lblDC.setVisible(true);
				purchase.puz.setEnabled(false);
				purchase.setPrice();
				dispose();
			}
		}, "West");
		add(lbl("→", 0, 25));
		add(e = new JPanel(new BorderLayout()), "East");

		try {
			image = ImageIO.read(new File("./datafiles/비행기.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < 3; i++) {
			x = 0;
			for (int j = 0; j < 3; j++) {
				var sub = image.getSubimage(x, y, image.getWidth() / 3, image.getHeight() / 3);
				var lbl = new JLabel(new ImageIcon(sub));

				lbl.setBorder(new LineBorder(Color.black));

				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var me = (JLabel) e.getSource();
						var meIdx = shuf.indexOf(me);
						var meX = meIdx % 3;
						var meY = meIdx / 3;

						var selIdx = shuf.indexOf(
								shuf.stream().filter(img -> img.getIcon().equals(select.getIcon())).findFirst().get());
						var selX = selIdx % 3;
						var selY = selIdx / 3;

						if (((selX + 1 == meX || selX - 1 == meX) && selY == meY)
								|| ((selY + 1 == meY || selY - 1 == meY) && selX == meX)) {
							Collections.swap(shuf, meIdx, selIdx);

							select = shuf.get(meIdx);

							w.repaint();
							w.revalidate();
						}
					}
				});

				ori.add(lbl);
				x += image.getWidth() / 3;
			}
			y += image.getHeight() / 3;
		}

		shuf.addAll(ori);
		Collections.shuffle(shuf);

		for (var img : shuf) {
			w.add(img);
		}

		var lbl = new JLabel(getIcon("./datafiles/비행기.jpg", 300, 300));

		lbl.setBorder(new LineBorder(Color.black));

		e.add(en = new JPanel(new FlowLayout(2)), "North");
		e.add(lbl);

		en.add(lbl("<html>아이콘 이미지를 기준으로        =><br>서로 바꾸어 퍼즐을 완성하시오.", 0));
		en.add(select = new JLabel(shuf.get(0).getIcon()));

		setVisible(true);

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if(e.getOppositeWindow() instanceof JDialog) {
					return;
				}
				
				dispose();
			}
		});
	}

	public static void main(String[] args) {
		new Login();
	}
}
