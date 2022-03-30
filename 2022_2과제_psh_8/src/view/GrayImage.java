package view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.JLabel;

public class GrayImage extends JLabel {
	boolean selected = false;
	BufferedImage m;
	Image g;
	int w, h;

	public GrayImage(String path) {
		try {
			m = ImageIO.read(new File(path));
			g = GrayFilter.createDisabledImage(m);
			w = m.getWidth();
			h = m.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GrayImage(String path, int w, int h) {
		this(path);
		this.w = w;
		this.h = h;
	}

	protected void paintComponent(java.awt.Graphics g) {
		g.drawImage(selected ? m : this.g, 0, 0, w, h, this);
	};
}
