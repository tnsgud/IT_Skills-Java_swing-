package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.JLabel;

public class GrayImage extends JLabel {
	boolean isSelect = false;
	BufferedImage m;
	Image g;
	int w, h;

	public GrayImage(String p, int w, int h) {
		this(p);
		this.w = w;
		this.h = h;
	}

	public GrayImage(String p) {
		try {
			m = ImageIO.read(new File(p));
			g = GrayFilter.createDisabledImage(m);

			w = m.getWidth();
			h = m.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(isSelect ? m : this.g, 0, 0, w, h, this);
	}
}
