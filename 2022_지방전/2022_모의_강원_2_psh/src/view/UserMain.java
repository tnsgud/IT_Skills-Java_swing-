package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import db.DB;
import tool.Tool;

public class UserMain extends BaseFrame implements Tool {
	String[] h = "출발지,도착지,날짜".split(",");
	JTextField txt[] = new JTextField[h.length];
	JPopupMenu pop = new JPopupMenu();
	LocalDate date = LocalDate.parse("2021-10-06");
	boolean click = false;

	public UserMain() {
		super(1300, 650);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		for (var t : txt) {
			t.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						var t = (JTextField) e.getSource();
						(toInt(t.getName()) < 2 ? showLocation(t, 0) : pop).show(t, e.getX(), e.getY());
						click = toInt(t.getName()) == 2;
					}
				}
			});
		}

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (click) {
					txt[2].setText(date + "");
				}
			}
		});
	}

	private void ui() {
		setLayout(new GridLayout(0, 1));
		{
			var root = new JLabel(img("main.jpg"));
			var n = new JPanel(new FlowLayout(2));
			var c_c = new JPanel(new BorderLayout(20, 20));
			var c_c_c = new JPanel(new FlowLayout());

			root.setLayout(new BorderLayout());
			add(root);
			root.add(n, "North");
			root.add(c = new JPanel(new GridBagLayout()));
			c.add(c_c);

			for (var cap : "테마,계정,예매,로그아웃".split(",")) {
				n.add(cap.equals("테마") ? themeBtn(UserMain.this) : btn(cap, a -> {
					if (a.getActionCommand().contentEquals("계정")) {
						var an = JOptionPane.showInputDialog(UserMain.this, "비밀번호를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);
						if(DB.getOne("select * from user where pwd=?", an).isEmpty()) {
							return;
						}
						
						new Account();
					} else if (a.getActionCommand().contentEquals("예매")) {
						new LookUp();
					} else {
						no = 0;
						dispose();
					}
				}));
			}

			c_c.add(lbl("예매", 2, 35), "North");
			c_c.add(c_c_c);

			for (int i = 0; i < h.length; i++) {
				c_c_c.add(txt[i] = new JHintField(15, h[i]));
				txt[i].setName(i + "");
				if (i == 0) {
					c_c_c.add(btn("<html>←<br/>→</html>", a -> {
						for (int j = 0; j < txt.length - 1; j++) {
							if (txt[j].getText().isEmpty()) {
								return;
							}
						}

						var temp = txt[0].getText();
						txt[0].setText(txt[1].getText());
						txt[1].setText(temp);
					}));
				}
			}

			c_c_c.add(sz(btn("조회", a -> {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
						return;
					}
				}

				var sno = new ArrayList<Integer>();
				var rs = DB.rs("select * from v1 where l11name=? and l21name=? and l12name=? and l22name=?;",
						txt[0].getText().split(" "), txt[1].getText().split(" "));
				try {
					while (rs.next()) {
						sno.add(rs.getInt(1));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (sno.size() == 0) {
					eMsg("예매할 수 없는 일정입니다.");
					return;
				}

				new Reserve(sno);
			}), 150, 50));

			n.setOpaque(false);
			c.setOpaque(false);
		}

		{
			var root = new JPanel(new GridBagLayout());
			var c = new JPanel(new BorderLayout(50, 50));
			var c_c = new JPanel(new FlowLayout(1, 80, 0));

			add(root);
			root.add(c);
			c.add(lbl("추천 여행지", 2, 35), "North");
			c.add(c_c);

			var rs = DB.rs(
					"select l.no, l.name, ri.img from location l, recommend r, recommend_info ri where l.no = r.location_no and ri.recommend_no=r.no group by l.name");
			try {
				while (rs.next()) {
					var p = new JPanel();
					var lbl = new JLabel(img(rs.getBlob(3).getBinaryStream().readAllBytes(), 130, 130));
					var menu = new JPopupMenu();
					menu.setName(rs.getString(1));
					for (var cap : "상세설명,예매".split(",")) {
						var item = new JMenuItem(cap);
						item.addActionListener(a -> {
							if (a.getActionCommand().equals("상세설명")) {
								new Detail(toInt(menu.getName()));
							} else {
								showLocation(txt[1], toInt(menu.getName())).show(txt[1], 0, 20);
							}
						});
						menu.add(item);
					}

					p.add(lbl);
					c_c.add(p);

					lbl.setComponentPopupMenu(menu);

					int t = rs.getRow() % 2 == 0 ? 25 : 0;
					int b = rs.getRow() % 2 == 0 ? 0 : 25;

					p.setBorder(new CompoundBorder(new EmptyBorder(t, 0, b, 0),
							new TitledBorder(new LineBorder(Color.BLACK), rs.getString(2))));
				}
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		{
			pop.setLayout(new GridLayout(1, 0));
			var p = new JPanel[3];
			var lbl = new JLabel[3];
			var up = new JButton[3];
			var down = new JButton[3];

			for (int i = 0; i < p.length; i++) {
				pop.add(p[i] = new JPanel(new BorderLayout()));
				p[i].add(up[i] = btn("▲", a -> {
					var name = toInt(((JButton) a.getSource()).getName());

					if (name == 0) {
						date = date.plusYears(1);
					} else if (name == 1) {
						date = date.plusMonths(1);
					} else {
						date = date.plusDays(1);
					}

					for (int j = 0; j < lbl.length; j++) {
						var t = j == 0 ? date.getYear() : j == 1 ? date.getMonthValue() : date.getDayOfMonth();
						lbl[j].setText(t + "");
					}

					if (name < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				}), "North");
				p[i].add(lbl[i] = lbl(
						(i == 0 ? date.getYear() : i == 1 ? date.getMonthValue() : date.getDayOfMonth()) + "", 0));
				p[i].add(down[i] = btn("▼", a -> {
					var name = toInt(((JButton) a.getSource()).getName());

					if (name == 0) {
						date = date.minusYears(1);
					} else if (name == 1) {
						date = date.minusMonths(1);
					} else {
						date = date.minusDays(1);
					}

					for (int j = 0; j < lbl.length; j++) {
						var t = j == 0 ? date.getYear() : j == 1 ? date.getMonthValue() : date.getDayOfMonth();
						lbl[j].setText(t + "");
					}

					if (name < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				}), "South");

				up[i].setName(i + "");
				down[i].setName(i + "");
			}
		}

	}

	public static void main(String[] args) {
		no = 1;
		new UserMain();
	}
}
