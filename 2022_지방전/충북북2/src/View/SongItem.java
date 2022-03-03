package View;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SongItem extends JPanel {
	int s_serial;
	JLabel img;

	public SongItem(String path, String title) {
		super(new BorderLayout(5, 5));
		events();
		add(img = BasePage.imglbl(path, 100, 100), "West");
		add(BasePage.lbl(title, JLabel.LEFT, 10));
		setOpaque(true);
		setBackground(Color.BLACK);
	}

	public SongItem(String path, String title, String align) {
		super(new BorderLayout(5, 5));
		events();
		add(img = BasePage.imglbl(path, 100, 100));
		add(BasePage.lbl(title, JLabel.LEFT, 10), align);

		setOpaque(true);
		setBackground(Color.BLACK);
	}

	void events() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(Color.GRAY);
				super.mouseEntered(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(Color.BLACK);
				super.mouseExited(e);
			}
		});
	}

}
