import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BaseFrame extends JFrame {
	public BaseFrame() {
		setSize(1200, 700);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setLayout(new GridBagLayout());
	}

	static JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	static JLabel lbl(String c, int a, int size) {
		return lbl(c, a, Font.BOLD, size);
	}

	static JLabel lbl(String c, int a, int style, int size) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", style, size));
		return l;
	}
	
	static <T extends JComponent> T sz( T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}
	
	static JButton btn(String c, ActionListener a) {
		var b = new JButton(c);
		b.addActionListener(a);
		return b;
	}
	
	static JPanel tmp(LayoutManager l) {
		var panel = new JPanel(l);
		return panel;
	}
	
	static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	
	static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "에러", JOptionPane.INFORMATION_MESSAGE);
	}
}
