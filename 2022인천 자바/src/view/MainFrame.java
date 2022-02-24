package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
	JPanel main_bg, main_p;
	Navigater nav;

	public MainFrame() {
		super("국삐");
		ui();
		setVisible(true);
	}

	void ui() {
		setSize(1000, 600);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setLayout(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("datafiles/Covid.png"));
		add(main_bg = new JPanel(new BorderLayout()));
		main_bg.setBounds(0, 0, 1000 - 16, 600 - 39);
		main_bg.add(main_p = new JPanel(new BorderLayout()));
	}

	void swapPage(BasePage page) {
		main_p.removeAll();
		main_p.setLayout(new BorderLayout());
		main_p.add(page);
		main_p.repaint();
		main_p.revalidate();
		repaint();
		revalidate();
	}

	void addNavigater() {
		add(nav = new Navigater());
		getContentPane().setComponentZOrder(nav, 0);
		main_bg.setBounds(30, 0, 1000 - 46, 600 - 39);
		repaint();
		revalidate();
		System.out.println("Nav added into Frame!");
	}

	void removeNavigater() {
		try {
			remove(nav);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		main_bg.setBounds(0, 0, 1000 - 16, 600 - 39);
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UIManager.getDefaults().put("OptionPane.background", new ColorUIResource(Color.WHITE));
		UIManager.getDefaults().put("CheckBox.background", new ColorUIResource(Color.WHITE));
		UIManager.getDefaults().put("Button.background", Color.ORANGE);
		UIManager.getDefaults().put("Panel.background", new ColorUIResource(Color.WHITE));
		BasePage.mf.swapPage(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}

class Navigater extends JPanel {
	int width = 300;
	int x = -width + 30, y = 0, incr = 0;
	Timer evntTimer;
	JLabel ctrlLbl;
	boolean isShown = false;
	JLabel infoLabel;
	JLabel imglbl;

	public Navigater() {
		super(new BorderLayout());
		ui();
		events();
	}

	void ui() {
		setBounds(x, 0, width, 600 - 39);
		add(ctrlLbl = BasePage.lbl("≡", JLabel.RIGHT, 25), "North");
		var n = new JPanel(new BorderLayout(5, 5));
		var c = new JPanel(new GridLayout(0, 1));
		add(n, "North");
		add(c);

		var nc = new JPanel(new GridLayout(0, 1, 5, 5));
		var ne = new JPanel(new BorderLayout(5, 5));
		n.setPreferredSize(new Dimension(width, 130));

		n.add(nc);
		n.add(ne, "East");

		n.add(imglbl = new JLabel(BasePage.getIcon("datafiles/유저사진/" + BasePage.uno + ".jpg", 130, 130)), "West");
		nc.add(BasePage.lbl(BasePage.uname, JLabel.LEFT, 13));
		nc.add(BasePage.lbl(BasePage.uage, JLabel.LEFT, 13));
		nc.add(BasePage.lbl(BasePage.residence.get(BasePage.toInt(BasePage.upoint))[1].toString(), JLabel.LEFT, 13));
		ne.add(ctrlLbl = BasePage.lbl("≡", JLabel.CENTER, 15), "North");
		setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		imglbl.setBorder(new LineBorder(Color.black));
		imglbl.setPreferredSize(new Dimension(130, 130));
		n.setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
		for (var navM : "로그아웃,마이페이지,검색,코로나 현황".split(",")) {
			var navlbl = BasePage.lbl(navM, JLabel.LEFT, 13);
			navlbl.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					BasePage.mf.removeNavigater();
					var me = (JLabel) e.getSource();
					if (me.getText().equals("로그아웃")) {
						BasePage.mf.swapPage(new LoginPage());
						return;
					} else if (me.getText().equals("마이페이지")) {
						BasePage.mf.swapPage(new MyPage());
					} else if (me.getText().equals("검색")) {
						BasePage.mf.swapPage(new SearchPage());
					} else {
						BasePage.mf.swapPage(new ChartPage());
					}
					BasePage.mf.addNavigater();
				}

			});
			c.add(navlbl);
		}

		c.setBorder(new EmptyBorder(0, 0, 100, 0));
	}

	void events() {
		evntTimer = new Timer(2, a -> {
			x += incr;
			setBounds(x, 0, width, BasePage.mf.main_bg.getHeight());
			revalidate();
			repaint();
			if (x <= -width + 30 || x >= 0) {
				isShown = !isShown;
				evntTimer.stop();
			}
		});

		ctrlLbl.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				BasePage.mf.getContentPane().requestFocusInWindow();
				incr = isShown ? -10 : 10;
				if (!evntTimer.isRunning())
					evntTimer.start();
				super.mousePressed(e);
			}

		});
	}

}
