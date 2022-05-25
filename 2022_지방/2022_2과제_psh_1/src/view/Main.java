package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends BaseFrame {
	JComboBox<String> com = new JComboBox<>();
	static ArrayList<JButton> btns = new ArrayList<>();
	ArrayList<JPanel> panels = new ArrayList<>();
	Timer timer;
	String[] cap = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");

	public Main() {
		super("메인", 550, 450);

		timer = new Timer(1, a -> {
			panels.forEach(p -> {
				p.setLocation(p.getLocation().x, p.getLocation().y - 5);
				if (p.getLocation().y <= -425) {
					p.setLocation(p.getLocation().x, panels.get(toInt(p.getName())).getLocation().y + 430);
				}
			});
		});

		ui();
		event();
		data();
		logout();

		timer.start();

		setVisible(true);
	}

	private void logout() {
		btns.forEach(btn -> {
			btn.setEnabled(btns.indexOf(btn) == 0 || btns.indexOf(btn) == 5);
		});
	}

	private void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		com.addItemListener(a -> rank());

	}

	private void rank() {
		timer.stop();
		c.removeAll();
		panels.clear();
		var type = com.getSelectedIndex() == 0;
		var rs = rs("select count(*) as cnt, " + (type ? "c_name" : "t.t_no, t_name")
				+ " from reservation r, cafe c, theme t where " + (type ? "c.c_no = r.c_no" : "t.t_no = r.t_no")
				+ "  and r_date <= '2022-04-05' group by r.c_no order by cnt desc limit 5");
		try {
			while (rs.next()) {
				var p = new JPanel(new BorderLayout());
				p.add(img(
						(type ? "지점" : "테마") + "/" + (type ? rs.getString(2).split(" ")[0] : rs.getString(2)) + ".jpg",
						450, 400));
				p.add(lbl(rs.getString((type ? 2 : 3)), 0, 25), "South");
				p.setName((rs.getRow() == 1 ? 4 : rs.getRow() - 2) + "");
				panels.add(p);
				c.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.repaint();
		c.revalidate();
		timer.restart();
	}

	private void data() {
		Stream.of("지점,테마".split(",")).forEach(s -> com.addItem(s));
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new FlowLayout()));
		add(s = new JPanel(new FlowLayout(1, 5, 5)), "South");
		n.add(lbl("예약 TOP5", 0, 20));
		n.add(com);

		rank();

		for (var c : cap) {
			var b = btn(c, a -> {
				if (a.getActionCommand() == cap[0]) {
					new Login().addWindowListener(new Before(this));
				} else if (a.getActionCommand() == cap[1]) {
					new MyPage().addWindowListener(new Before(this));
				} else if (a.getActionCommand() == cap[2]) {
					new Search().addWindowListener(new Before(this));
				} else if (a.getActionCommand() == cap[3]) {
					new NoticeBoard().addWindowListener(new Before(this));
				} else if (a.getActionCommand() == cap[4]) {
					new GameList().addWindowListener(new Before(this));
				} else if (a.getActionCommand() == cap[5]){
					new ReservationState().addWindowListener(new Before(this));
				}else {
					uno = 0;
					logout();
				}
			});

			s.add(b);
			btns.add(b);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
