package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import db.DB;

public class BaseFrame extends JFrame {
	JPanel n, w, c, e, s;
	static boolean isLogin = false;
	static int uno = 0, pno = 0, totPrice = 0;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);

		setIconImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./Datafiles/오렌지.jpg")).getImage());

		DB.execute("use 2021전국");
	}

}
