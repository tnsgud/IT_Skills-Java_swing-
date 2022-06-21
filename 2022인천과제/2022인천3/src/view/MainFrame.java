package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import db.DB;

public class MainFrame extends JFrame {

	public MainFrame() {
		super("국삐");
		setSize(1000, 600);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/covid.png"));
		setDefaultCloseOperation(3);
		setLocationRelativeTo(null);

		var defaults = UIManager.getLookAndFeelDefaults();
		for (var key : defaults.keySet()) {
			if (key.toString().contains("back")) {
				UIManager.put(key, new ColorUIResource(Color.WHITE));
			}
		}

		try {
			DB.stmt.execute("use covid");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void swapPage(BasePage page) {
		getContentPane().removeAll();
		getContentPane().setLayout(new BorderLayout());
		add(page);
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new MainFrame().setVisible(true);
	}
}
