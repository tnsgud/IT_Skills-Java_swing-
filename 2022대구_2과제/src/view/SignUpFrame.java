package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SignUpFrame extends BaseFrame {
	ImageIcon icon = getIcon("./지급자료/image/user/0.jpg", 80, 80);

	public SignUpFrame() {
		super("Sign up", 750, 600);

		add(w = sz(new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				int w = getWidth(), h = getHeight();
				var paint = new GradientPaint(0, 0, red, 2, h, Color.yellow);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setPaint(paint);
				g2.fillRect(0, 0, w, h);
			}
		}, getWidth() / 2 - 50, 0), "West");
		add(c = new JPanel(new BorderLayout()));

		w.add(lbl("<html><font color='white'>Create your Account<br>회원가입", 2, 20), "North");
		w.add(lbl("<html><font color='white'>SKILL CINEMA에 오신걸 환영합니다.<br>계정 정보를 입력해주세요.", 2, 15));

		c.add(cn = new JPanel(new FlowLayout(0)), "West");
		c.add(cc = new JPanel(new GridLayout(0, 1)));
		c.add(cs = new JPanel(new BorderLayout()), "South");

		w.setBorder(new EmptyBorder(20, 10, 300, 10));

		setVisible(true);
	}

	public static void main(String[] args) {
		new SignUpFrame();
	}
}
