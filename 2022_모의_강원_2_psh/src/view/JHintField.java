package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class JHintField extends JTextField {
	String holder;

	public JHintField(int s, String holder) {
		super(s);
		this.holder = holder;
		setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!getText().isEmpty()) {
			return;
		}

		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawString(holder, getInsets().left, g2.getFontMetrics().getMaxAscent() + getInsets().bottom);
	}
}