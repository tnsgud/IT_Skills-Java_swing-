package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class JHintField extends JTextField {

	String holder;
	public JHintField(String holder, int col) {
		super(col);
		this.holder = holder;
		setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
	}

	@Override
	public String toString() {
		return getText();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!getText().isEmpty())
			return;
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawString(holder, getInsets().left + 5, g2.getFontMetrics().getMaxAscent() + getInsets().top);
	}
}
