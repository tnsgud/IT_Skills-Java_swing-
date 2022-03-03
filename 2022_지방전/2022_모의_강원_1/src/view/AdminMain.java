package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import db.DB;

public class AdminMain extends BaseFrame {
	JTabbedPane tab;

	public AdminMain() {
		super(1200, 600);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(tab = new JTabbedPane(JTabbedPane.LEFT));
		tab.add("사용자 관리", new UserManage());
		tab.add("추천 여행지 관리", new Recommend());
		tab.add("일정 관리", new Schedule());
		tab.add("예매 관리", new ReserveManage());
		tab.add("테마", null);
		tab.add("로그아웃", null);

		setTheme(this, curCon);
		tab.setBackgroundAt(4, curCon ? Color.DARK_GRAY : Color.white);
		tab.setForegroundAt(4, curCon ? Color.white : Color.DARK_GRAY);

		tab.addChangeListener(a -> {
			if (tab.getSelectedIndex() == 4) {
				curCon = !curCon;
				setTheme(this, curCon);

				var t = UserManage.txt;
				var name = t.getName();
				if (t.getText().equals(name)) {
					t.setForeground(curCon ? Color.LIGHT_GRAY : Color.white);
				} else {
					t.setForeground(curCon ? Color.BLACK : Color.white);
				}

				var source = (JTabbedPane) a.getSource();
				source.setBackground(curCon ? Color.darkGray : Color.white);
				source.setForeground(curCon ? Color.white : Color.black);

				this.repaint();
				this.revalidate();

				for (int i = 0; i < tab.getTabCount(); i++) {
					if (i != 4) {
						tab.setBackground(curCon ? Color.WHITE : Color.DARK_GRAY);
						tab.setForeground(curCon ? Color.DARK_GRAY : Color.WHITE);
					}
				}

				tab.setBackgroundAt(4, curCon ? Color.DARK_GRAY : Color.WHITE);
				tab.setForegroundAt(4, curCon ? Color.WHITE : Color.DARK_GRAY);
				tab.setSelectedIndex(0);
			} else if (tab.getSelectedIndex() == 5) {
				dispose();
			}
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}

class UserManage extends JPanel {
	static JTextField txt;
	JPopupMenu menu = new JPopupMenu();
	JMenuItem item;
	DefaultTableModel m = new DefaultTableModel(null, "순번,아이디,비밀번호,성명,이메일,포인트,예매수".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return column != 0;
		};
	};
	JTable t = BaseFrame.table(m);
	String key = "%%";
	boolean isChange = false;

	JPanel n, s;

	public UserManage() {
		ui();
		data();
		event();
	}

	private void event() {
		m.addTableModelListener(e -> {
			isChange = true;
		});

		item.addActionListener(e -> {
			new Booking(BaseFrame.toInt(t.getValueAt(t.getSelectedRow(), 0)));
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() != -1 && e.getButton() == 3) {
					menu.show(t, e.getX(), e.getY());
				}
			}
		});
	}

	private void data() {
		m.setRowCount(0);
		try {
			var rs = DB.rs("select * from user where name like ?", key);
			while (rs.next()) {
				var row = new Object[t.getColumnCount()];
				for (int i = 0; i < row.length - 1; i++) {
					row[i] = rs.getString(i + 1);
				}
				row[6] = DB.getOne("select count(*) from reservation where user_no=?", rs.getInt(1));
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		menu.add(item = new JMenuItem("예매 조회"));

		var n_e = new JPanel(new FlowLayout(2));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(BaseFrame.lbl("사용자 관리", 2, 35), "West");
		n.add(n_e);
		n_e.add(txt = BaseFrame.txt(JTextField.class, 12, "성명"));
		n_e.add(BaseFrame.btn("사용자 조회", a -> {
			key = txt.getText().equals(txt.getName()) || txt.getText().isEmpty() ? "%%" : "%" + txt.getText() + "%";
			data();
		}));

		for (var c : "저장,삭제".split(",")) {
			s.add(BaseFrame.btn(c, a -> {
				if (!isChange || t.getSelectedRow() == -1) {
					return;
				}

				if (a.getActionCommand().equals("저장")) {
					for (int i = 0; i < t.getRowCount(); i++) {
						var data = new ArrayList<Object>();
						for (int j = 1; j < t.getColumnCount() - 1; j++) {
							data.add(t.getValueAt(i, j));
						}
						data.add(i + 1);
						DB.execute("update user set id=?,pwd=?,name=?,email=?,point=? where no=?", data.toArray());
					}

					BaseFrame.img("수정내용을 저장 완료하였습니다.");
					isChange = false;
				} else {
					DB.execute("delete from user where no=?", t.getValueAt(t.getSelectedRow(), 0));
					data();
					BaseFrame.iMsg("삭제를 완료하였습니다.");
				}
			}));
		}

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

class Recommend extends JPanel {
	JPanel p[] = new JPanel[2];
	int rno;

	public Recommend() {
		ui();
	}

	private JPopupMenu showPopup(JComponent com, String title, int type) {
		var menu = new JPopupMenu();

		if (type == 0) {
			for (var c : "이미지,설명".split(",")) {
				var item = new JMenuItem(c + " 설정");

				item.addActionListener(a -> {
					if (a.getActionCommand().equals("설명 설정")) {
						loadImg();
					} else {
						chooser(com, title);
					}
				});

				menu.add(item);
			}
		}

		if (type == 1) {
			var panel = new JPanel(new GridLayout(0, 1));

			BaseFrame.sz(menu, 125, 250);

			menu.setLayout(new BorderLayout());
			menu.add(new JScrollPane(panel));

			try {
				var rs = DB.rs("select name, no from location");
				while (rs.next()) {
					var b = new JButton(rs.getString(1));
					b.setName(rs.getString(2));
					b.addActionListener(a -> {
						var lno = BaseFrame.toInt(((JButton) a.getSource()).getName());
						DB.execute("update recommend set location_no=? where no=?", lno, rno);
					});
					panel.add(b);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return menu;
	}

	private void chooser(JComponent com, String title) {
		var chooser = new JFileChooser();
		chooser.resetChoosableFileFilters();
		chooser.setFileFilter(new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(",")));

		int r = chooser.showOpenDialog(com);
		if (r == 0) {
			var f = chooser.getSelectedFile();
			var str = f.getName().split("\\\\");
			var fileName = str[str.length - 1].replace(".jpg", "").replace(".png", "");

			if (DB.getOne("select * from recommend_info where title = ?", fileName) != null) {
				BaseFrame.eMsg("중복된 제목입니다.");
				return;
			}

			if (com instanceof JButton) {
				try {
					DB.execute("insert into recommend_info values(?, ?, ?, ?)", rno, str, "", new FileInputStream(f));
					var rs = DB.rs("select img from recommend_info where title=? and recommend_no=?", str, rno);
					if (rs.next()) {
						loadImg();
					}
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					DB.execute("update recommend_info set img=? where title=? and recommend_no=?",
							new FileInputStream(f), title, rno);
					ui();
					if (rno != 0) {
						loadImg();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void loadImg() {
		p[1].removeAll();

		try {
			var rs = DB.rs("select title, img from recommend_info where recommend_no=?", rno);
			while (rs.next()) {
				var l = new JLabel(BaseFrame.img(rs.getBlob(2).getBinaryStream().readAllBytes(), 160, 160));
				l.setName(rs.getString(1));
				l.setBorder(new LineBorder(Color.black));
				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 3) {
							var menu = new JPopupMenu();
							for (var c : "삭제,설명 텍스트 입력".split(",")) {
								var item = new JMenuItem(c);
								item.addActionListener(a -> {
									if (a.getActionCommand().equals("삭제")) {
										DB.execute("delete from recommend_info where title=? and recommend_no=?",
												((JLabel) e.getSource()).getName(), rno);

										loadImg();
									} else {
										var ans = JOptionPane.showInputDialog(null, "설명 텍스트를 입력해주세요.", "입력",
												JOptionPane.QUESTION_MESSAGE);
										if (ans != null && !ans.isEmpty()) {
											DB.execute(
													"update recommend_info set descrption=? where recommend_no=? and title=?",
													ans, rno, ((JLabel) e.getSource()).getName());
										}
									}
								});
								menu.add(item);
							}

							menu.show((JLabel) e.getSource(), e.getX(), e.getY());
						}
					}
				});
				p[1].add(l);
			}
		} catch (SQLException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		p[1].repaint();
		p[1].revalidate();
	}

	private void ui() {
		removeAll();
		setLayout(new GridLayout(0, 1));

		for (var cap : "추천 여행지 관리,설명 설정".split(",")) {
			var root = new JPanel(new BorderLayout(10, 10));
			var n = new JPanel(new FlowLayout(0));
			var c = new JPanel(new FlowLayout(0));

			p[cap.equals("추천 여행지 관리") ? 0 : 1] = c;
			p[cap.equals("추천 여행지 관리") ? 0 : 1].removeAll();

			add(root);
			root.add(n, "North");
			root.add(c);

			n.add(BaseFrame.lbl(cap, 2, 35));

			if (cap.equals("추천 여행지 관리")) {
				try {
					var rs = DB.rs(
							"select l.name, r.no, ri.title, ri.img from recommend r, recommend_info ri, location l where ri.recommend_no = r.no and l.no = r.location_no group by ri.recommend_no");
					while (rs.next()) {
						var l = new JLabel(BaseFrame.img(rs.getBlob(4).getBinaryStream().readAllBytes(), 160, 160));
						l.setName(rs.getString(2) + "," + rs.getString(3));
						l.setBorder(new TitledBorder(new LineBorder(Color.black), rs.getString(1)));
						l.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								var arr = ((JLabel) e.getSource()).getName().split(",");
								rno = BaseFrame.toInt(arr[0]);
								var title = arr[1];
								showPopup((JLabel) e.getSource(), title, e.getClickCount() == 2 ? 1 : 0)
										.show((JLabel) e.getSource(), e.getX(), e.getY());
							}
						});
						c.add(l);
					}
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				n.add(BaseFrame.btn("추가", a -> {
					chooser((JComponent) a.getSource(), "");
				}));
			}

			p[cap.equals("추천 여행지 관리") ? 0 : 1].revalidate();
		}

		repaint();
		revalidate();
	}
}

class Schedule extends JPanel {
	DefaultTableModel m = BaseFrame.model("순번,출발지,도착지,출발날짜,이동시간".split(","));
	JPopupMenu menu;
	HashSet<Integer> rows = new HashSet<>();
	JTable t = BaseFrame.table(m);

	boolean isChange = false;

	public Schedule() {
		ui();
		data();
		evnet();

		setVisible(true);
	}

	private void evnet() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1 || e.getButton() != 3
						|| (t.getSelectedColumn() != 1 && t.getSelectedColumn() != 2)) {
					return;
				}

				menu = BaseFrame.popup((JComponent) e.getSource());
				menu.show((JTable) e.getSource(), e.getX(), e.getY());
			}
		});
		m.addTableModelListener(a -> {
			rows.add(t.getSelectedRow());
			isChange = true;
		});
	}

	private void data() {
		m.setRowCount(0);
		try {
			var rs = DB.rs(
					"select s.no, concat(l11.name, ' ', l21.name), concat(l12.name, ' ', l22.name), s.date, s.elapsed_time from location l11, location l12, location2 l21, location2 l22, schedule s where l11.no = l21.location_no and l12.no = l22.location_no and l21.no = s.departure_location2_no and l22.no = s.arrival_location2_no order by s.no");
			while (rs.next()) {
				var row = new Object[t.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var s = new JPanel(new FlowLayout(2));

		add(BaseFrame.lbl("일정 관리", 2, 35), "North");
		add(new JScrollPane(t));
		add(s, "South");

		for (var cap : "저장,삭제".split(",")) {
			s.add(BaseFrame.btn(cap, a -> {
				if (a.getActionCommand().equals("저장")) {
					rows.forEach(row -> {
						var departure = BaseFrame.toInt(DB.getOne(
								"select l2.no from location l1, location2 l2 where l1.no=l2.location_no and l1.name=? and  l2.name=?",
								t.getValueAt(row, 1).toString().split(" ")));
						var arrival = BaseFrame.toInt(DB.getOne(
								"select l2.no from location l1, location2 l2 where l1.no=l2.location_no and l1.name=? and  l2.name=?",
								t.getValueAt(row, 2).toString().split(" ")));

						DB.execute("update schedule set departure_location2_no=?, arrival_location2_no=? where no=?",
								departure, arrival, BaseFrame.toInt(t.getValueAt(row, 0)));
						data();
					});
				} else {
					DB.execute("delete from schedule where no=?", t.getValueAt(t.getSelectedRow(), 0));
					data();
				}
			}));
		}

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

class ReserveManage extends JPanel {
	JPanel chart;
	DefaultTableModel m = BaseFrame.model("sno,순번,예매자,출발지,도착지,출발날짜,도착시간".split(","));
	JTable t = BaseFrame.table(m);
	JComboBox<String> box;
	HashSet<Integer> rows = new HashSet<>();
	boolean mod = true, isChange = false;
	ArrayList<ArrayList<String>> area = new ArrayList<>();
	JPopupMenu menu;

	public ReserveManage() {
		ui();
		data();
	}

	private void data() {
		m.setRowCount(0);
		try {
			var rs = DB.rs(
					"select s.no, u.name, concat(l11.name, ' ', l21.name), concat(l12.name, ' ', l22.name), s.date, time_format(addtime(s.date,  s.elapsed_time), '%H:%i:%s') from location l11, location l12, location2 l21, location2 l22, reservation r, schedule s, user u where l11.no = l21.location_no and l12.no = l22.location_no and l21.no = s.departure_location2_no and l22.no = s.arrival_location2_no and r.schedule_no = s.no and r.user_no=u.no order by r.no");
			while (rs.next()) {
				var row = new Object[t.getColumnCount()];
				row[0] = rs.getString(1);
				row[1] = rs.getRow();
				for (int i = 2; i < row.length; i++) {
					row[i] = rs.getString(i);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));
		var n = new JPanel(new FlowLayout(0));
		var s = new JPanel(new FlowLayout(2));
		add(chart = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawString("<가장 예매가 많은 일정 TOP 6>",
						getWidth() / 2 - getFontMetrics(getFont()).stringWidth("<가장 예매가 많은 일정 TOP 6>") / 2, 20);

				if (mod) {
					g2.drawLine(50, 50, 50, 281);
					for (int i = 0; i < 5; i++) {
						g2.drawLine(50, 50 + (i * 58), 600, 50 + (i * 58));
					}

					var p = new Polygon();
					area = DB.queryResult(
							"select *, count(schedule_no) from reservation group by schedule_no order by count(schedule_no) desc, schedule_no limit 6");

					int max = BaseFrame.toInt(area.get(0).get(3));

					for (int i = 0; i < area.size(); i++) {

						if (i != 5) {
							g2.drawString((max - i * 2) + "", 30, 50 + (i * 58));
						}

						g2.drawString(area.get(i).get(2), 50 + (i * 108), 300);

						if (max >= 18) {
							p.addPoint(50 + (i * 110), 50 + ((max - BaseFrame.toInt(area.get(i).get(3))) * 12));
						} else {
							p.addPoint(50 + (i * 110), 50 + ((max - BaseFrame.toInt(area.get(i).get(3))) * 30));
						}
					}

					p.addPoint(600, 282);
					p.addPoint(50, 282);
					g2.setColor(new Color(0, 125, 255));
					g2.fillPolygon(p);
				} else {
					var num = new int[6];
					var po = new int[6][2];
					var pg = new Polygon[] { new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(),
							new Polygon() };

					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < pg.length; j++) {
							pg[i].addPoint((int) (380 + (100 - i * 20) * Math.cos(j * Math.PI / 3 + (Math.PI / 6))),
									(int) (200 + (100 - i * 20) * Math.sin(j * Math.PI / 3 + (Math.PI / 6))));
						}

						g2.drawPolygon(pg[i]);
					}

					for (int i = 0; i < pg.length; i++) {
						po[i][0] = (int) (375 + 110 * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
						po[i][1] = (int) (200 + 110 * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
					}

					try {
						var rs = DB.rs(
								"select *, count(schedule_no) as cnt from reservation group by schedule_no order by cnt desc, schedule_no asc limit 6");
						int i = 0, max = 0;
						while (rs.next()) {
							if (i == 0) {
								max = rs.getInt(4);
							}

							if (i < 5) {
								g2.drawString(max - 2 * i + "", 370, 100 + (i * 20));
							}

							g2.drawString(rs.getString(3), po[i][0], po[i][1]);
							num[i] = rs.getInt(4);
							i++;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					g2.setColor(new Color(0, 125, 255));
					var p = new Polygon();

					for (int i = 0; i < num.length; i++) {
						p.addPoint(
								(int) (380 + (100 - (num[0] - num[i]) / 2.0 * 20)
										* Math.cos(i * Math.PI / 3 - (Math.PI / 2))),
								(int) (200 + (100 - (num[0] - num[i]) / 2.0 * 20)
										* Math.sin(i * Math.PI / 3 - (Math.PI / 2))));
					}
					g2.drawPolygon(p);
				}
			}
		}, "North");
		n.setOpaque(false);

		chart.add(n, "North");

		n.add(BaseFrame.lbl("예매 관리", 2, 20));
		n.add(box = new JComboBox<>(new DefaultComboBoxModel<>("2차원 영역형,방사형".split(","))));
		add(new JScrollPane(t));
		add(s, "South");

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);
		
		box.addItemListener(i -> {
			mod = i.getItem().equals("2차원 영역형");
			repaint();
		});

		BaseFrame.sz(chart, 300, 350);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1) {
					return;
				}

				if ((t.getSelectedColumn() == 3 || t.getSelectedColumn() == 4) && e.getButton() == 3) {
					menu = BaseFrame.popup(t);
					menu.show(t, e.getX(), e.getY());
				}
			}
		});
		m.addTableModelListener(a -> {
			rows.add(t.getSelectedRow());
		});
		
		
		for (var cap : "저장,취소".split(",")) {
			s.add(BaseFrame.btn(cap, a -> {
				if (a.getActionCommand().equals("저장")) {
					rows.forEach(row -> {
						var departure = BaseFrame.toInt(DB.getOne(
								"select l2.no from location l1, location2 l2 where l1.no=l2.location_no and l1.name=? and  l2.name=?",
								t.getValueAt(row, 2).toString().split(" ")));
						var arrival = BaseFrame.toInt(DB.getOne(
								"select l2.no from location l1, location2 l2 where l1.no=l2.location_no and l1.name=? and  l2.name=?",
								t.getValueAt(row, 3).toString().split(" ")));

						DB.execute("update schedule set departure_location2_no=?, arrival_location2_no=? where no=?",
								departure, arrival, BaseFrame.toInt(t.getValueAt(row, 0)));
						data();
					});
				} else {
					DB.execute("delete from schedule where no=?", t.getValueAt(t.getSelectedRow(), 0));
					data();
				}
			}));
		}
	}
}