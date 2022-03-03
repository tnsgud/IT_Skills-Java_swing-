package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import db.DB;
import tool.Tool;

public class Recommend extends JPanel implements Tool {
	JPanel p[] = new JPanel[2];
	JPopupMenu pop1 = new JPopupMenu(), pop2 = new JPopupMenu();
	String title;
	int rno;

	public Recommend() {
		for (var cap : "이미지,설명".split(",")) {
			var item = new JMenuItem(cap + " 설정");
			item.addActionListener(a -> {
				if (a.getActionCommand().contentEquals("이미지 설정")) {
					chooser((JMenuItem) a.getSource());
				} else {
					loadImg();
				}
			});
			pop1.add(item);
		}

		ui();
	}

	private void chooser(JComponent com) {
		var ch = new JFileChooser();
		ch.resetChoosableFileFilters();
		ch.setFileFilter(new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(",")));

		int r = ch.showOpenDialog(null);
		if (r == 0) {
			var f = ch.getSelectedFile();
			var str = f.getName().split("\\\\");
			var fileName = str[str.length - 1].replace(".jpg", "").replace(".png", "");

			if (DB.getOne("select * from recommend_info where title=?", fileName) != null) {
				eMsg("중복된 제목입니다.");
				return;
			}

			if (com instanceof JButton) {
				try {
					DB.execute("insert into recommend_info values(?, ?, ?, ?)", rno, str, "", new FileInputStream(f));
					var rs = DB.rs("select img from recommend_info where recommend_no=?", rno);
					if (rs.next()) {
						loadImg();
					}
				} catch (FileNotFoundException | SQLException e) {
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

		var rs = DB.rs("select title, img from recommend_info where recommend_no=?", rno);
		try {
			while (rs.next()) {
				var lbl = new JLabel(img(rs.getBlob(2).getBinaryStream().readAllBytes(), 160, 160));
				lbl.setName(rs.getString(1));
				lbl.setBorder(new LineBorder(Color.black));
				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 3) {
							pop2.removeAll();

							for (var cap : "삭제,설명 텍스트 입력".split(",")) {
								var item = new JMenuItem(cap);
								item.addActionListener(a -> {
									if (a.getActionCommand().contentEquals("삭제")) {
										DB.execute("delete from recommend_info where title=? and recommend_no=?",
												((JLabel) e.getSource()).getName(), rno);
										loadImg();
									} else {
										var ans = JOptionPane.showInputDialog(null, "설명 텍스트를 입력해주세요.", "입력",
												JOptionPane.QUESTION_MESSAGE);
										if (ans != null && !ans.isEmpty()) {
											DB.execute(
													"update recommend_info set description=? where recommend_no=? and title=?",
													ans, rno, ((JLabel) e.getSource()).getName());
										}
									}
								});
								pop2.add(item);
							}

							pop2.show(((JLabel) e.getSource()), e.getX(), e.getY());
						}
					}
				});

				p[1].add(lbl);
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new GridLayout(0, 1));

		for (var cap : "추천 여행지 관리,설명 설정".split(",")) {
			var root = new JPanel(new BorderLayout(10, 10));
			var n = new JPanel(new FlowLayout(0));
			var c = new JPanel(new FlowLayout(0));
			var idx = cap.contentEquals("추천 여행지 관리") ? 0 : 1;

			p[idx] = c;
			p[idx].removeAll();

			add(root);
			root.add(n, "North");
			root.add(c);

			n.add(lbl(cap, 2, 35));

			if (cap.equals("추천 여행지 관리")) {
				var rs = DB.rs(
						"select l.name, r.no, ri.title, ri.img from recommend r, recommend_info ri, location l where ri.recommend_no=r.no and l.no=r.location_no group by ri.recommend_no");
				try {
					while (rs.next()) {
						var l = new JLabel(img(rs.getBlob(4).getBinaryStream().readAllBytes(), 160, 160));
						l.setName(rs.getString(2) + "," + rs.getString(3));
						l.setBorder(new TitledBorder(new LineBorder(Color.black), rs.getString(1)));
						l.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								var s = (JLabel) e.getSource();

								rno = toInt(s.getName().split(",")[0]);
								title = s.getName().split(",")[1];

								if (e.getClickCount() == 2) {
									showPopup().show(s, e.getX(), e.getY());
								} else if (e.getButton() == 3) {
									pop1.show(s, e.getX(), e.getY());
								}
							}
						});
						c.add(l);
					}
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		repaint();
		revalidate();
	}

	private JPopupMenu showPopup() {
		var menu = new JPopupMenu();
		var p = new JPanel(new GridLayout(0, 1));

		sz(menu, 125, 250);

		menu.setLayout(new BorderLayout());
		menu.add(new JScrollPane(p));

		var rs = DB.rs("select no, name from location");
		try {
			while (rs.next()) {
				var btn = btn(rs.getString(2), a -> {
					var lno = toInt(((JButton) a.getSource()).getName());
					DB.execute("update recommend set location_no=? where no=?", lno, rno);
				});
				btn.setName(rs.getString(1));
				p.add(btn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return menu;
	}

	public static void main(String[] args) {
		new AdminMain();
	}
}
