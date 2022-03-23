package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.JLabel;

public class GrayImage extends JLabel {
	int width, height;
	BufferedImage m;
	Image gray;
	boolean isSelected = false;

	public GrayImage(String path) {
		try {
			m = ImageIO.read(new File(path));
			gray = GrayFilter.createDisabledImage(m);
			width = m.getWidth();
			height = m.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GrayImage(String path, int w, int h) {
		this(path);
		width = w;
		height = h;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(isSelected ? m : gray, 0, 0, width, height, this);
	}
}
