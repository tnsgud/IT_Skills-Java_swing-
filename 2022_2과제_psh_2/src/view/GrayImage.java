package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class GrayImage extends JLabel {
	boolean isSelected = false;
	BufferedImage ori, gray;
	int width, height;

	public GrayImage(String path) {
		System.out.println(path);
		try {
			ori = ImageIO.read(new File(path));
			gray = ImageIO.read(new File(path));
			var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			op.filter(gray, gray);

			width = ori.getWidth();
			height = ori.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GrayImage(String path, int w, int h) {
		this(path);
		width = w;
		height = h;
		
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(isSelected ? ori : gray, 0, 0, width, height, this);
	}
}
