package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class AdminMain extends BaseFrame {

	JTabbedPane tab;

	public AdminMain() {
		super("관리자", 1200, 600);
		add(tab = new JTabbedPane(JTabbedPane.LEFT));
		tab.addTab("사용자 관리", new UserManageMent());
		tab.addTab("추천 여행지 관리", new Recommend());
		tab.addTab("일정 관리", new Schdule());
		tab.addTab("예매 관리", new ReserveManage());
		tab.addTab("테마", null);
		tab.addTab("로그아웃", null);

		setTheme(this, theme);
		tab.setBackgroundAt(4, theme ? Color.DARK_GRAY : Color.WHITE);
		tab.setForegroundAt(4, theme ? Color.WHITE : Color.DARK_GRAY);

		tab.addChangeListener(a -> {
			if (tab.getSelectedIndex() == 4) {
				theme = !theme;
				setTheme(this, theme);
				for(int i = 0; i<tab.getTabCount(); i++) {
					if(i != 4) {
						tab.setBackground(theme ? Color.WHITE : Color.DARK_GRAY);
						tab.setForeground(theme ? Color.DARK_GRAY : Color.WHITE);
					}
				}
				
				tab.setBackgroundAt(4, theme ? Color.DARK_GRAY : Color.WHITE);
				tab.setForegroundAt(4, theme ? Color.WHITE : Color.DARK_GRAY);
				tab.setSelectedIndex(0);
			}

			if (tab.getSelectedIndex() == 5)
				dispose();

		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}

class UserManageMent extends JPanel {

	DefaultTableModel m = new DefaultTableModel(null, "순번,아이디,비밀번호,성명,이메일,포인트,예매수".split(","));
	JTable t = BaseFrame.table(m);
	JHintField field;

	JPopupMenu menu;
	JMenuItem item;
	boolean isChanged = false;

	public UserManageMent() {
		setLayout(new BorderLayout());
		var n = new JPanel(new BorderLayout());
		var ne = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var s = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		add(n, "North");
		add(new JScrollPane(t));
		n.add(ne, "East");

		add(s, "South");

		n.add(BaseFrame.lbl("사용자 관리", JLabel.LEFT, 20));
		ne.add(field = new JHintField("성명", 10));
		ne.add(BaseFrame.btn("사용자 조회", a -> setData()));

		t.getColumn("순번").setMinWidth(100);
		t.getColumn("순번").setMaxWidth(100);

		t.getColumn("이메일").setMaxWidth(200);
		t.getColumn("이메일").setMinWidth(200);

		t.setAutoCreateRowSorter(true);
		menu = new JPopupMenu();
		menu.add(item = new JMenuItem("예매 조회"));

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.getButton() == 3) {
					if (t.getSelectedRow() == -1)
						return;
					menu.show(t, e.getX(), e.getY());
				}
			};
		});

		item.addActionListener(a -> {
			new Booking(t.getValueAt(t.getSelectedRow(), 0).toString()).setVisible(true);
		});

		m.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				isChanged = true;
			}
		});

		s.add(BaseFrame.btn("저장", a -> {
			if (!isChanged)
				return;

			for (int i = 0; i < t.getRowCount(); i++) {
				BaseFrame.execute("update user set no = '" + t.getValueAt(i, 0) + "' , id = '" + t.getValueAt(i, 1)
						+ "', pwd = '" + t.getValueAt(i, 2) + "', name = '" + t.getValueAt(i, 3) + "', email = '"
						+ t.getValueAt(i, 4) + "', point = '" + t.getValueAt(i, 5) + "' where no = '"
						+ t.getValueAt(i, 0) + "'");
			}

			BaseFrame.iMsg("수정내용을 저장 완료하였습니다.");
			isChanged = false;
		}));
		s.add(BaseFrame.btn("삭제", a -> {
			if (t.getSelectedRow() == -1)
				return;
			BaseFrame.execute("delete from user where no = '" + t.getValueAt(t.getSelectedRow(), 0) + "'");
			setData();
			BaseFrame.iMsg("삭제를 완료하였습니다.");

		}));

		setBorder(new EmptyBorder(20, 20, 20, 20));
		setData();
	}

	void setData() {
		BaseFrame.addRow(
				"select u.no, id, pwd, u.name, email, point, count(r.no) from user u inner join reservation r on u.no = r.user_no where u.name like '%"
						+ field + "%' group by u.no  order by u.no",
				m);
	}

}

class Recommend extends JPanel {

	JPanel m1, m2;
	JPanel sub1, sub2n, sub2c;

	JPopupMenu pop1, pop2, pop3;
	JMenuItem item1, item2, item3, item4;
	JScrollPane jsp;
	JPanel loc;
	int idx;
	String title;

	public Recommend() {
		setLayout(new GridLayout(0, 1));
		add(m1 = new JPanel(new BorderLayout()));
		add(m2 = new JPanel(new BorderLayout()));

		m1.add(BaseFrame.lbl("추천 여행지 관리", JLabel.LEFT, 20), "North");
		m1.add(sub1 = new JPanel(new GridLayout(1, 0, 5, 5)));
		m2.add(sub2n = new JPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		m2.add(sub2c = new JPanel(new FlowLayout(FlowLayout.LEFT)));
		sub2n.add(BaseFrame.lbl("설명 설정", JLabel.LEFT, 20));
		sub2n.add(BaseFrame.btn("추가", a -> {
			JFileChooser jfc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(","));
			jfc.setFileFilter(filter);

			int condition = jfc.showOpenDialog(this);

			if (condition == 0) {
				File f = jfc.getSelectedFile();
				String[] str = f.toString().split("\\\\");
				if (!BaseFrame.getOne("select * from recommend_info where title = '"
						+ str[str.length - 1].replace(".jpg", "").replace(".png", "") + "'").equals("")) {
					BaseFrame.eMsg("중복된 제목입니다.");
					return;
				} else {
					try {
						var pst = BaseFrame.con.prepareStatement("insert into recommend_info values(?,?,?,?)");
						pst.setInt(1, idx);
						pst.setString(2, str[str.length - 1].replace(".jpg", "").replace(".png", ""));
						pst.setString(3, "");
						try {
							pst.setBinaryStream(4, new FileInputStream(f));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pst.execute();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}));

		setPopup();
		loadImg();

		setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	void setPopup() {
		pop1 = new JPopupMenu();
		pop2 = new JPopupMenu();
		pop3 = new JPopupMenu();
		pop1.add(jsp = new JScrollPane(loc = new JPanel(new GridLayout(0, 1))));
		pop2.add(item1 = new JMenuItem("이미지 설정"));
		pop2.add(item2 = new JMenuItem("설명 설정"));
		pop3.add(item3 = new JMenuItem("삭제"));
		pop3.add(item4 = new JMenuItem("설명 텍스트 입력"));
		try {
			var rs = BaseFrame.stmt.executeQuery("select * from location");
			while (rs.next()) {
				final int i = rs.getInt(1);
				JButton btn = new JButton(rs.getString(2));
				btn.addActionListener(a -> {
					BaseFrame.execute("update recommend set location_no = " + i + " where no = " + idx);
					loadImg();
				});
				loc.add(btn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		item1.addActionListener(a -> {
			JFileChooser jfc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images", "JPG,png".split(","));
			jfc.setFileFilter(filter);

			int condition = jfc.showOpenDialog(this);

			if (condition == 0) {
				File f = jfc.getSelectedFile();
				try {
					var pst = BaseFrame.con
							.prepareStatement("update recommend_info set img = ? where recommend_no = ? and title = 1");
					try {
						pst.setBinaryStream(1, new FileInputStream(f));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					pst.setString(2, idx + "");
					pst.execute();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadImg();
				loadDetail();
			}
		});
		item2.addActionListener(a -> {
			loadDetail();
		});
		item3.addActionListener(a -> {
			BaseFrame
					.execute("delete from recommend_info where recommend_no = " + idx + " and title = '" + title + "'");
			loadImg();
			loadDetail();
		});
		item4.addActionListener(a -> {
			String str = JOptionPane.showInputDialog(null, "설명 텍스트를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);

			if (str != null && !str.isBlank()) {
				BaseFrame.execute("update recommend_info set description = '" + str + "' where recommend_no = " + idx
						+ " and title = '" + title + "'");
			}
		});
	}

	void loadImg() {
		sub1.removeAll();
		try {
			var rs = BaseFrame.stmt.executeQuery(
					"select * from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and l.no = r.location_no group by recommend_no order by recommend_no, title");
			while (rs.next()) {
				try {
					JLabel img = new JLabel(
							new ImageIcon(Toolkit.getDefaultToolkit().createImage(rs.getBinaryStream(4).readAllBytes())
									.getScaledInstance(135, 130, Image.SCALE_SMOOTH)));
					img.setName(rs.getString(5));

					img.setBorder(
							new CompoundBorder(new TitledBorder(new LineBorder(Color.BLACK), rs.getString("name")),
									new EmptyBorder(5, 5, 5, 5)));

					img.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if (e.getClickCount() == 2) {
								idx = BaseFrame.toInt(((JLabel) e.getSource()).getName());
								pop1.show(img, e.getX(), e.getY());
							}

							if (e.getButton() == 3) {
								idx = BaseFrame.toInt(((JLabel) e.getSource()).getName());
								pop2.show(img, e.getX(), e.getY());
							}

							super.mouseClicked(e);
						}
					});
					sub1.add(img);
					img.setBorder(new LineBorder(Color.BLACK));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		revalidate();
		repaint();
	}

	void loadDetail() {
		sub2c.removeAll();

		try {
			var rs = BaseFrame.stmt
					.executeQuery("select * from recommend_info where recommend_no = " + idx + " order by title");
			while (rs.next()) {
				try {
					JLabel img = new JLabel(
							new ImageIcon(Toolkit.getDefaultToolkit().createImage(rs.getBinaryStream(4).readAllBytes())
									.getScaledInstance(135, 130, Image.SCALE_SMOOTH)));
					img.setName(rs.getString(2));
					img.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							title = ((JLabel) e.getSource()).getName();
							if (e.getButton() == 3)
								pop3.show(img, e.getX(), e.getY());
							super.mouseClicked(e);
						}
					});
					sub2c.add(img);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Schdule extends JPanel {

	DefaultTableModel m = BaseFrame.model("순번,출발지,도착지,출발날짜,이동시간".split(","));
	JTable t = BaseFrame.table(m);
	JPopupMenu menu;
	boolean isChanged;

	public Schdule() {
		setLayout(new BorderLayout(5, 5));
		add(BaseFrame.lbl("일정관리", JLabel.LEFT, 20), "North");
		add(new JScrollPane(t));
		var s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(s, "South");

		s.add(BaseFrame.btn("저장", a -> {
			if (isChanged) {
				for (var row : m.getDataVector()) {
					BaseFrame.execute("update schedule set departure_location2_no = "
							+ Arrays.asList(BaseFrame.loc2).indexOf(row.get(1).toString().split(" ")[1])
							+ ", arrival_location2_no = "
							+ Arrays.asList(BaseFrame.loc2).indexOf(row.get(2).toString().split(" ")[1])
							+ " where no = " + row.get(0));
				}

				BaseFrame.iMsg("수정내용을 저장 완료하였습니다.");
				isChanged = false;
			}
		}));
		s.add(BaseFrame.btn("삭제", a -> {
			int r = t.getSelectedRow();
			BaseFrame.execute("delete from schedule where no = " + t.getValueAt(r, 0));
			BaseFrame.iMsg("삭제를 완료하였습니다.");
			setData();
		}));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				if (t.getSelectedColumn() == 1 || t.getSelectedColumn() == 2) {
					if (e.getButton() == 3) {
						menu = BaseFrame.locPopup(t, t.getSelectedRow(), t.getSelectedColumn(), 200, 300);
						menu.show(t, e.getX(), e.getY());
					}
				}
				super.mousePressed(e);
			}
		});
		m.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				isChanged = true;
			}
		});
		setData();
		setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	void setData() {
		m.setRowCount(0);
		try {
			BaseFrame.dataInit();
			var rs = BaseFrame.stmt.executeQuery("select * from schedule");
			while (rs.next()) {
				m.addRow(new Object[] { rs.getString(1),
						BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(3)],
						BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(3)],
						rs.getString(4), rs.getString(5) });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ReserveManage extends JPanel {

	JPanel chart;
	DefaultTableModel m = BaseFrame.model("순번,예매자,출발지,도착지,출발날짜,도착시간,sno".split(","));
	JTable t = BaseFrame.table(m);
	JComboBox<String> box;
	JScrollPane jsp;
	boolean mod = true, isChanged;
	ArrayList<ArrayList<String>> area = new ArrayList<>();
	JPopupMenu menu;

	public ReserveManage() {
		setLayout(new BorderLayout(5, 5));
		var n = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(chart = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawString("<가장 예매가 많은 일정 TOP 6>",
						getWidth() / 2 - getFontMetrics(getFont()).stringWidth("<가장 예매가 많은 일정 TOP 6>") / 2, 20);
				if (mod) { // 쉬운거
					g2.drawLine(50, 50, 50, 281);
					for (int i = 0; i < 5; i++) {
						g2.drawLine(50, 50 + (i * 58), 600, 50 + (i * 58));
					}

					Polygon p = new Polygon();
					BaseFrame.Query(
							"select *, count(schedule_no) from reservation group by schedule_no order by count(schedule_no) desc, schedule_no limit 6;",
							area);

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

				} else { // 어렵고 슬픈거.
					int num[] = new int[6];
					int po[][] = new int[6][2];
					Polygon pg[] = { new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(),
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
						var rs = BaseFrame.stmt.executeQuery(
								"select *, count(schedule_no) as cnt from reservation group by schedule_no order by cnt desc, schedule_no asc limit 6");
						int i = 0, max = 0;
						while (rs.next()) {
							if (i == 0)
								max = rs.getInt(4);

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
					Polygon p = new Polygon();

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
		n.add(BaseFrame.lbl("예매 관리", JLabel.LEFT, 20));
		n.add(box = new JComboBox<String>(new DefaultComboBoxModel<String>("2차원 영역형, 방사형".split(","))));
		add(jsp = new JScrollPane(t));

		box.addItemListener(i -> {
			mod = i.getItem().equals("2차원 영역형");
			repaint();
		});

		chart.setPreferredSize(new Dimension(300, 350));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				if (t.getSelectedColumn() == 2 || t.getSelectedColumn() == 3) {
					if (e.getButton() == 3) {
						menu = BaseFrame.locPopup(t, t.getSelectedRow(), t.getSelectedColumn(), 200, 300);
						menu.show(t, e.getX(), e.getY());
					}

				}
				super.mousePressed(e);
			}
		});

		m.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				isChanged = true;
			}
		});

		var s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(s, "South");
		s.add(BaseFrame.btn("저장", a -> {
			if (isChanged) {
				for (int i = 0; i < m.getRowCount(); i++) {
					var dep = m.getValueAt(i, 2).toString().split(" ");
					var arrv = m.getValueAt(i, 3).toString().split(" ");
					var dep_loc2 = BaseFrame.getOne(
							"select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = '"
									+ dep[0] + "' and l2.name = '" + dep[1] + "'");
					var arrv_loc2 = BaseFrame.getOne(
							"select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = '"
									+ arrv[0] + "' and l2.name = '" + arrv[1] + "'");
					BaseFrame.execute("update schedule set departure_location2_no ="+dep_loc2+", arrival_location2_no = "+arrv_loc2+" where no = "+t.getValueAt(i, 6));			
				}
				
				BaseFrame.iMsg("수정내용을 저장 완료하였습니다.");
				isChanged = false;
			}
		}));
		s.add(BaseFrame.btn("삭제", a -> {
			int r = t.getSelectedRow();
			if (r == -1)
				return;

			BaseFrame.execute("delete from reservation where no = '" + t.getValueAt(r, 0) + "'");
			setData();
			BaseFrame.iMsg("삭제를 완료하였습니다.");
			revalidate();
		}));
		setData();
		setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	void setData() {

		m.setRowCount(0);
		try {
			BaseFrame.dataInit();
			var rs = BaseFrame.stmt.executeQuery(
					"select r.no, u.name, s.departure_location2_no, s.arrival_location2_no, s.date, time(date_add(s.date, interval elapsed_time hour_second)), s.no  from reservation r inner join schedule s on r.schedule_no = s.no inner join user u on u.no = r.user_no order by r.no");
			while (rs.next()) {
				Object row[] = new Object[7];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}

				row[2] = BaseFrame.loc1[BaseFrame.locMap[BaseFrame.toInt(row[2])]] + " "
						+ BaseFrame.loc2[BaseFrame.toInt(row[2])];
				row[3] = BaseFrame.loc1[BaseFrame.locMap[BaseFrame.toInt(row[3])]] + " "
						+ BaseFrame.loc2[BaseFrame.toInt(row[3])];
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}