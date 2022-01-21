package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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

}

class ReserveManage extends JPanel {

}