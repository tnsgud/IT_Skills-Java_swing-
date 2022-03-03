package View;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Category extends JPanel {
	String cate = "가요";
	ImageIcon img;
	JLabel lbl;
	float opacity = 1f;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		img = new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/images/category/" + cate + ".jpg")
				.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));
		g.drawImage(img.getImage(), 0, 0, null);
	}

	public Category() {

	}

	public Category(String cate) {
		setOpaque(false);

		this.cate = cate.replace("\n", "").replace("\r", "");

		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(250, 150));
		lbl = BasePage.lbl(cate, JLabel.CENTER, Font.BOLD, 20);
		add(lbl);

		setName(cate);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				opacity = 0.5f;
				lbl.setForeground(Color.green);
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				opacity = 1f;
				lbl.setForeground(Color.white);
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				opacity = 1f;
				lbl.setForeground(Color.white);
				BasePage.mf.swapView(new CategoryPage(((JPanel) e.getSource()).getName()));
				repaint();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
		g2.setComposite(ac);

		super.paint(g);
	}
}
