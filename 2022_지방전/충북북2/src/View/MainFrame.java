package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import DB.DB;
import View.BasePage.Timebar;

public class MainFrame extends JFrame {

	JPanel playP;
	JLabel menuP, prev_lbl, play_lbl, next_lbl, queue_lbl, album_img, state_lbl, add_lbl;
	JScrollPane view;
	ArrayList<JLabel> playList = new ArrayList<>();
	static final String IMG_PATH = "./지급자료/images/";
	public static Connection con = DB.con;
	public static Statement stmt = DB.stmt;
	static {
		try {
			stmt.execute("use music");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MainFrame() {
		super("Music");
		default_ui();
		home();
	}

	void setPlayList() {
		playList.clear();

		for (var c : menuP.getComponents()) {
			if (c.getName() != null) {
				menuP.remove(c);
			}

		}

		try {
			var rs = stmt.executeQuery("select * from playlist where user = " + BasePage.u_serial);
			while (rs.next()) {
				var lbl = BasePage.lbl(rs.getString(2), JLabel.LEFT, 10);
				lbl.setName(rs.getInt(1) + "");
				menuP.add(lbl);
				var tmp = Box.createVerticalStrut(5);
				menuP.add(tmp);
				tmp.setName("null만 아니면 되니깐~~~");
				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var my_serial = ((JLabel) e.getSource()).getName();
						swapView(new PlayListPage(my_serial));
						super.mousePressed(e);
					}
				});
				playList.add(lbl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void default_ui() {
		setSize(1300, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		setIconImage(Toolkit.getDefaultToolkit().getImage(IMG_PATH + "logo.png"));
		getContentPane().setBackground(Color.BLACK);
	}

	void home() {
		var mainP = getContentPane();
		mainP.removeAll();

		add(view = new JScrollPane());
		add(BasePage.size(menuP = BasePage.imglbl("./지급자료/images/side.png", 250, 650), 250, 1), "West");
		add(BasePage.size(playP = new JPanel(new BorderLayout(5, 5)), 1, 150), "South");

		// menuP
		menuP.setLayout(new BoxLayout(menuP, BoxLayout.Y_AXIS));
		menuP.add(BasePage.lbl("MENU", JLabel.LEFT, 15));
		menuP.add(Box.createVerticalStrut(5));
		menuP.add(BasePage.imglbl("홈", IMG_PATH + "home.png", 15, 15, new Events()));
		menuP.add(Box.createVerticalStrut(5));
		menuP.add(BasePage.imglbl("검색하기", IMG_PATH + "search.png", 15, 15, new Events()));
		menuP.add(Box.createVerticalStrut(20));
		menuP.add(BasePage.lbl("LIBRARY", JLabel.LEFT, 15));
		menuP.add(Box.createVerticalStrut(5));
		menuP.add(BasePage.imglbl("좋아요", IMG_PATH + "like.png", 15, 15, new Events()));
		menuP.add(Box.createVerticalStrut(5));
		menuP.add(BasePage.imglbl("재생기록", IMG_PATH + "history.png", 15, 15, new Events()));
		menuP.add(Box.createVerticalStrut(20));
		menuP.add(BasePage.lbl("PLAYLIST", JLabel.LEFT, 15));
		menuP.add(Box.createVerticalStrut(5));
		menuP.add(add_lbl = BasePage.lbl("재생목록 추가", JLabel.LEFT, 10));
		setPlayList();
		// playBar

		var p_n = new JPanel(new BorderLayout());
		var p_c = new JPanel(new BorderLayout());
		p_c.setLayout(new BoxLayout(p_c, BoxLayout.Y_AXIS));
		var p_c_c = new JPanel(new FlowLayout(FlowLayout.CENTER));
		var p_e = new JPanel(new BorderLayout(5, 5));
		var p_w = new JPanel(new BorderLayout(5, 5));
		var p_e_e = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var p_e_c = new JPanel(new GridBagLayout());
		var p_w_c = new JPanel(new GridBagLayout());
		var p_w_w = new JPanel(new BorderLayout(5, 5));

		playP.add(BasePage.size(p_n, 1, 20), "North");
		playP.add(p_e, "East");
		playP.add(p_w, "West");
		playP.add(p_c);
		p_c.add(p_c_c);
		p_n.add(BasePage.bar = new Timebar());

		p_c_c.add(BasePage.size(state_lbl = BasePage.lbl("재생중이 아님", JLabel.CENTER, 10), 100, 30), "North");
		p_c_c.add(Box.createVerticalStrut(30));
		p_c_c.add(play_lbl = BasePage.imglbl(IMG_PATH + "play.png", 40, 40));

		p_e.add(p_e_e, "East");
		p_e.add(BasePage.size(p_e_c, 500, 1));

		p_w.add(BasePage.size(p_w_c, 500, 1));
		p_w.add(BasePage.size(p_w_w, 120, 100), "West");
		p_w_w.add(album_img = new JLabel());
		p_w_w.setBorder(new LineBorder(Color.WHITE));

		p_w_c.add(prev_lbl = BasePage.imglbl(IMG_PATH + "prev.png", 30, 30));
		p_e_c.add(next_lbl = BasePage.imglbl(IMG_PATH + "next.png", 30, 30));
		p_e_e.add(queue_lbl = BasePage.imglbl(IMG_PATH + "queue.png", 60, 60));

		playP.setBorder(new EmptyBorder(5, 5, 5, 5));
		p_n.setOpaque(false);
		p_c.setOpaque(false);
		p_w.setOpaque(false);
		p_e.setOpaque(false);
		p_c_c.setOpaque(false);
		p_e_e.setOpaque(false);
		p_e_c.setOpaque(false);
		p_w_c.setOpaque(false);
		p_w_w.setOpaque(false);
		view.setOpaque(false);
		menuP.setOpaque(false);
		playP.setOpaque(false);

		menuP.setBorder(new EmptyBorder(5, 5, 5, 5));
		album_img.setBorder(new LineBorder(Color.WHITE));
		mainP.repaint();
		mainP.revalidate();
		view.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			protected JButton createDecreaseButton(int orientation) {
				super.decrButton = EmptyButton();
				return super.decrButton;
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				super.incrButton = EmptyButton();
				return super.incrButton;
			}

			private JButton EmptyButton() {
				JButton btn = new JButton();
				btn.setPreferredSize(new Dimension(0, 0));
				btn.setMinimumSize(new Dimension(0, 0));
				btn.setMaximumSize(new Dimension(0, 0));
				return btn;
			}

			@Override
			protected void configureScrollBarColors() {
				super.thumbColor = Color.LIGHT_GRAY;
				super.trackColor = Color.GRAY;
			}
		});

		events();
	}

	void swapView(JPanel p) {
		view.setViewportView(p);
	}

	void events() {

		BasePage.barTimer = new Timer(1, a -> {
			BasePage.bar.setValue(BasePage.value);
			BasePage.value++;
		});

		add_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (playList.size() == 5) {
					BasePage.eMsg("플레이리스트는 5개까지 만들 수 있습니다");
					return;
				}

				String input = JOptionPane.showInputDialog("플레이리스트 제목을 입력해주세요.");
				if (input == null)
					return;
				if (input.equals("")) {
					BasePage.eMsg("플레이리스트 제목을 입력해주세요.");
					return;
				} else {
					BasePage.execute("insert into playlist values(0, '" + input + "', '" + BasePage.u_serial + "')");
					setPlayList();
					repaint();
					revalidate();
				}
			}
		});

		prev_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				BasePage.bar.max = 0;
				BasePage.prev();
				super.mousePressed(e);
			}
		});

		next_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				BasePage.bar.max = 0;
				BasePage.next();
				super.mousePressed(e);
			}
		});

		queue_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				swapView(new QueuePage());
				super.mousePressed(e);
			}
		});

		play_lbl.setName("play");

		play_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (BasePage.cur_t.getValueAt(0, 2).equals("재생중이지 않음"))
					return;

				switch (play_lbl.getName()) {
				case "play":
					play();
					break;
				default:
					pause();
					break;
				}
				super.mousePressed(e);
			}
		});
	}

	void play() {
		play_lbl.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(IMG_PATH + "pause.png")
				.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
		play_lbl.setName("pause");
		BasePage.barTimer.start();
	}

	void pause() {
		play_lbl.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(IMG_PATH + "play.png").getScaledInstance(40,
				40, Image.SCALE_SMOOTH)));
		play_lbl.setName("play");
		BasePage.barTimer.stop();
	}

	class Events extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			var me = (JLabel) e.getSource();

			if (me.getText().equals("홈")) {
				swapView(new HomePage());
			} else if (me.getText().equals("검색하기")) {
				swapView(new SearchPage());
			} else if (me.getText().equals("좋아요")) {
				swapView(new FavoritePage());
			} else {
				swapView(new PlayHistoryPage());
			}

			super.mousePressed(e);
		}
	}

	void swapPage(BasePage p) {
		var mainP = getContentPane();
		mainP.removeAll();
		mainP.add(p);
		mainP.repaint();
		mainP.revalidate();
	}

	public static void main(String[] args) {
		BasePage.u_serial = 1;
		BasePage.u_region = 1;
		BasePage.mf.setVisible(true);
		BasePage.mf.swapView(new HomePage());
	}
}
