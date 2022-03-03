package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.ImageIcon;
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

public class UserMain extends BaseFrame {
	String[] h = "출발지,도착지,날짜".split(",");
	JTextField txt[] = new JTextField[h.length];
	JPopupMenu pop1 = new JPopupMenu(), pop2 = new JPopupMenu(), pop3 = new JPopupMenu();
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
					var source = (JTextField) e.getSource();
					if (source.getName().equals("날짜")) {
						pop2.show(source, 0, 0);
						click = true;
					} else {
						pop1 = popup(source);
						pop1.show(source, 0, 25);
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
			var root = new JLabel(new ImageIcon(img("main.jpg")));
			var n = new JPanel(new FlowLayout(2));
			var c_c = new JPanel(new BorderLayout(20, 20));
			var c_c_c = new JPanel(new FlowLayout());

			root.setLayout(new BorderLayout());
			add(root);
			root.add(n, "North");
			root.add(c = new JPanel(new GridBagLayout()));
			c.add(c_c);

			for (var t : "테마,계정,예매,로그아웃".split(",")) {
				n.add(t.equals("테마") ? themeBtn(this, txt) : btn(t, a -> {
					if (a.getActionCommand().equals("계정")) {
						var an = JOptionPane.showInputDialog(UserMain.this, "비밀번호를 입력해주세요.", "입력",
								JOptionPane.QUESTION_MESSAGE);
						if (DB.getOne("select * from user where pwd=?", an) == null) {
							return;
						}

						new Account();
					} else if (a.getActionCommand().equals("예매")) {
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
				c_c_c.add(txt[i] = txt(JTextField.class, 15, h[i]));
				if (i == 0) {
					c_c_c.add(btn("<html>←<br/>→</html>", a -> {
						if (txt[0].getText().equals(txt[0].getName()) || txt[1].getText().equals(txt[1].getName())) {
							return;
						}

						var temp = txt[0].getText();
						txt[0].setText(txt[1].getText());
						txt[1].setText(temp);

						txt[0].setForeground(Color.black);
						txt[1].setForeground(Color.black);
					}));
				}
			}

			c_c_c.add(sz(btn("조회", a -> {
				for (var t : txt) {
					if (t.getText().equals(t.getName())) {
						eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
						return;
					}
				}

				var txt0 = txt[0].getText().split(" ");
				var txt1 = txt[1].getText().split(" ");

				var startLocation = toInt(DB.getOne("select no from location where name=?", txt0[0]));
				var endLocation = toInt(DB.getOne("select no from location where name=?", txt1[0]));

				var start = toInt(
						DB.getOne("select no from location2 where name=? and location_no=?", txt0[1], startLocation));
				var end = toInt(
						DB.getOne("select no from location2 where name=? and location_no=?", txt1[1], endLocation));

				var result = new HashMap<Integer, String>();
				try {
					var rs = DB.rs(
							"select * from schedule where departure_location2_no = ? and arrival_location2_no = ? and date(date)=?",
							start, end, txt[2].getText());
					while (rs.next()) {
						result.put(rs.getInt("no"), txt[0].getText() + "/" + txt[1].getText());
					}
				} catch (SQLException e) {
					eMsg("예매할 수 없는 일정입니다.");
					e.printStackTrace();
				}

				new Reserve(result);
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

			try {
				var rs = DB.rs(
						"select l.no, l.name, ri.img from location l, recommend r, recommend_info ri where l.no = r.location_no and ri.recommend_no = r.no group by l.name");
				while (rs.next()) {
					var p = new JPanel();
					var lbl = new JLabel(new ImageIcon(
							Toolkit.getDefaultToolkit().createImage(rs.getBlob(3).getBinaryStream().readAllBytes())
									.getScaledInstance(130, 130, Image.SCALE_SMOOTH)));
					var menu = new JPopupMenu();
					menu.setName(rs.getString(1));
					for (var s : "상세설명,예매".split(",")) {
						var item = new JMenuItem(s);
						item.setName(rs.getString(1));
						item.addActionListener(a -> {
							if (a.getActionCommand().equals("상세설명")) {
								new Detail(toInt(menu.getName()));
							} else {
								showPopupLocation2(txt[1], toInt(((JMenuItem) a.getSource()).getName()));
							}
						});
						menu.add(item);
					}

					c_c.add(p);
					p.add(lbl);

					lbl.setComponentPopupMenu(menu);

					p.setBorder(new CompoundBorder(
							new EmptyBorder(rs.getRow() % 2 == 0 ? 25 : 0, 0, rs.getRow() % 2 == 0 ? 0 : 25, 0),
							new TitledBorder(new LineBorder(Color.black), rs.getString(2))));
				}
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		{
			pop2.setLayout(new GridLayout(1, 0));
			var p = new JPanel[3];
			var lbl = new JLabel[3];
			var up = new JButton[3];
			var down = new JButton[3];

			for (int i = 0; i < p.length; i++) {
				pop2.add(p[i] = new JPanel(new BorderLayout()));
				p[i].add(up[i] = new JButton("▲"), "North");
				p[i].add(lbl[i] = lbl(
						(i == 0 ? date.getYear() : i == 1 ? date.getMonthValue() : date.getDayOfMonth()) + "", 0));
				p[i].add(down[i] = new JButton("▼"), "South");

				up[i].setName(i + "");
				down[i].setName(i + "");

				up[i].addActionListener(a -> {
					var name = toInt(((JButton) a.getSource()).getName());
					if (name == 0) {
						date = date.plusYears(1);
					} else if (name == 1) {
						date = date.plusMonths(1);
					} else {
						date = date.plusDays(1);
					}

					for (int j = 0; j < lbl.length; j++) {
						var txt = j == 0 ? date.getYear() : j == 1 ? date.getMonthValue() : date.getDayOfMonth();
						lbl[j].setText(txt + "");
					}

					if (name < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				});

				down[i].addActionListener(a -> {
					var name = toInt(((JButton) a.getSource()).getName());
					if (name == 0) {
						date = date.minusYears(1);
					} else if (name == 1) {
						date = date.minusMonths(1);
					} else {
						date = date.minusDays(1);
					}

					for (int j = 0; j < lbl.length; j++) {
						var txt = j == 0 ? date.getYear() : j == 1 ? date.getMonthValue() : date.getDayOfMonth();
						lbl[j].setText(txt + "");
					}

					if (name < 2 && toInt(lbl[2].getText()) > date.getDayOfMonth()) {
						var last = date.withDayOfMonth(date.lengthOfMonth()).getDayOfMonth();
						lbl[2].setText(last + "");
					}
				});
			}
		}
	}

	public static void main(String[] args) {
		BaseFrame.no = 1;
		new UserMain();
	}
}
