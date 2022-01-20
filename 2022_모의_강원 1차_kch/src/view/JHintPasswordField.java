package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPasswordField;
import javax.swing.border.MatteBorder;

public class JHintPasswordField extends JPasswordField {

	String holder;

	public JHintPasswordField(String holder, int col) {
		super(col);
		this.holder = holder;
		setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
	}

	@Override
	public String toString() {
		return String.valueOf(getPassword());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!String.valueOf(getPassword()).isEmpty())
			return;

		var g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawString(holder, getInsets().left, g2.getFontMetrics().getMaxAscent() + getInsets().bottom);
	}
}
