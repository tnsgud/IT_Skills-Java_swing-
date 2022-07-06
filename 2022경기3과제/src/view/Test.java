package view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JPanel {

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int w = getSize().width;
		int h = getSize().height;

//		Arc2D arc = new Arc2D.Double(0.0, 0.0, w, h, 0.0, 60.0, Arc2D.CHORD);
//
//		g2.draw(arc);

		var arc = new Arc2D.Float(0.0f, 0.0f, w, h, 80.0f, 110.0f, Arc2D.PIE);

		g2.fill(arc);

//		arc = new Arc2D.Float(0.0f, 0.0f, w, h, 210.0f, 130.0f, Arc2D.OPEN);
//
//		g2.draw(arc);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new Test());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
}
