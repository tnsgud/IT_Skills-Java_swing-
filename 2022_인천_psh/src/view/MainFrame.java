package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import tool.Tool;

public class MainFrame extends JFrame implements Tool {
	JPanel main_bg, main_p;
	Navigater nav;

	public MainFrame() {
		super("국삐");
		ui();
		setVisible(true);
	}

	private void ui() {
		execute("use covid");
		setSize(1000, 600);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setLayout(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/Covid.png"));
		add(main_bg = new JPanel(new BorderLayout()));
		main_bg.setBounds(0, 0, 980, 560);
		main_bg.add(main_p = new JPanel(new BorderLayout()));

		var defaults = UIManager.getLookAndFeelDefaults();
		synchronized (defaults) {
			for (var k : defaults.keySet()) {
				if (k.toString().contains("fore")) {
					UIManager.getLookAndFeelDefaults().put(k, new ColorUIResource(Color.black));
				}

				if (k.toString().contains("back")) {
					UIManager.getLookAndFeelDefaults().put(k, new ColorUIResource(Color.white));
				}
			}
		}

		SwingUtilities.updateComponentTreeUI(this);
	}

	void swapPage(BasePage p) {
		main_p.removeAll();
		main_p.setLayout(new BorderLayout());
		main_p.add(p);
		main_p.repaint();
		main_p.revalidate();
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}

class Navigater extends JPanel implements Tool {
	int width = 300;
	int x = -width + 30, y = 0, incr = 0;
	Timer eventTimer;
	JLabel ctrlLbl;
	boolean isShown = false;
	JLabel info;
	JLabel img;

	public Navigater() {
		super(new BorderLayout());
		ui();
		event();
	}

	private void event() {

	}

	private void ui() {
		var n = new JPanel(new BorderLayout(5, 5));
		var c = new JPanel(new GridLayout(0, 1));
		var nc = new JPanel(new GridLayout(0, 1, 5, 5));
		var ne = new JPanel(new BorderLayout(5, 5));
		
		setBounds(x, 0, width, 560);
		add(ctrlLbl = lbl("≡", 4, 25), "North");

		add(sz(n, width, 130), "North");
		add(c);
		
		n.add(img =img("유저사진"+BasePage.uno+".jpg", 130, 130), "West");
		n.add(nc);
		n.add(ne, "East");
	}
}