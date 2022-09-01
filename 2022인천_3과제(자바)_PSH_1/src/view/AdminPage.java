package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.geom.Arc2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

public class AdminPage extends BasePage {
	CardLayout card;

	public AdminPage() {
		add(w = sz(new JPanel(), 250, 0), "West");
		add(c = new JPanel(card = new CardLayout()));
		w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));

		c.add(new User(), "회원");
		c.add(new Building(), "건물");
		c.add(new Chart(), "통계");

		var cap = "&#128100 회원관리,&#127968 건물관리,&#128200 통계,&#128275 로그이웃".split(",");
		for (var ca : cap) {
			var lbl = lbl("<html>" + ca, 2, 0, 20, Color.orange, e -> {
				var me = (JLabel) e.getSource();

				for (var com : w.getComponents()) {
					((JComponent) com).setBorder(null);
				}

				me.setBorder(
						new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));

				if (me.getText().contains("회원")) {
					card.show(c, "회원");
				} else if (me.getText().contains("건물")) {
					card.show(c, "건물");
				} else if (me.getText().contains("통계")) {
					card.show(c, "통계");
				} else {
					mf.swap(new LoginPage());
				}
			});

			w.add(lbl);
			w.add(Box.createVerticalStrut(10));
		}

		w.setBackground(blue);
		w.setBorder(new EmptyBorder(5, 5, 5, 5));
		((JComponent) w.getComponent(0))
				.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));
	}

	class User extends JPanel {
		DefaultTableModel m = new DefaultTableModel(null, "번호,이름,아이디,비밀번호,전화번호,생일,거주지".split(",")) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0 && column != 2;
			}
		};
		JTable t = table(m);
		JComboBox<String> com = new JComboBox<>(getRows("select name from building where type = 2").stream()
				.map(a -> a.get(0).toString()).toArray(String[]::new));

		public User() {
			super(new BorderLayout());

			var s = new JPanel(new FlowLayout(2));

			add(new JScrollPane(t));
			add(s, "South");

			for (var cap : "수정,삭제".split(",")) {
				s.add(btn(cap, a -> {
					if (cap.equals("수정")) {
						for (int i = 0; i < t.getRowCount(); i++) {
							var phone = t.getValueAt(i, 4).toString();
							var birth = t.getValueAt(i, 5).toString();

							if (!phone.matches("\\d{3}-\\\\d{$}-\\\\d{4}")) {
								eMsg("전화번호 형식이 잘못되었습니다.");
								return;
							}

							try {
								var d = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
								if (d.isAfter(LocalDate.now())) {
									eMsg("생년월일을 확인하세요.");
									t.setRowSelectionInterval(i, i);
									return;
								}
							} catch (Exception e) {
								eMsg("생년월일을 확인하세요.");
								t.setRowSelectionInterval(i, i);
								return;
							}

							var data = m.getDataVector().get(i);
							execute("update user set name = ?, pw = ?, phone=?, birth=?, building=? where no = ?",
									data.get(1), data.get(3), phone, birth,
									getOne("select no from building where name = ?", data.get(6)), data.get(0));
						}
					} else {
						execute("delete from user where no = ?", t.getValueAt(t.getSelectedRow(), 0));
						iMsg("삭제가 완료되었습니다.");
					}

					data();
				}));
			}

			t.setRowHeight(30);
			t.addMouseListener(new MouseAdapter() {
			});
			t.getColumn("거주지").setCellEditor(new DefaultCellEditor(com));

			data();
		}

		private void data() {
			m.setRowCount(0);

			for (var rs : getRows("select u.*, b.name from user u, building b where u.building = b.no")) {
				rs.set(6, rs.get(7));
				rs.remove(7);
				m.addRow(rs.toArray());
			}
		}
	}

	class Building extends JPanel {
		DefaultTableModel m = new DefaultTableModel(null, "이름,종료,설명,시작시간,종료시간,사진,번호".split(",")) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 1 && column != 5;
			}

			@Override
			public java.lang.Class<?> getColumnClass(int columnIndex) {
				return columnIndex == 5 ? ImageIcon.class : String.class;
			}
		};
		JTable t = table(m);

		public Building() {
			super(new BorderLayout());

			var s = new JPanel(new FlowLayout(2));

			add(new JScrollPane(t));
			add(s, "South");

			s.add(btn("저장", a -> {
				for (int i = 0; i < t.getRowCount(); i++) {
					var data = m.getDataVector().get(i);
					execute("update building set name = ?, info = ?, open = ?, close = ? where no = ?", data.get(0),
							data.get(2), data.get(3), data.get(4), data.get(6));
				}
				iMsg("저장이 완료되었습니다.");
			}));

			t.setRowHeight(80);
			t.getColumn("사진").setCellRenderer(null);
			t.getColumn("번호").setMinWidth(0);
			t.getColumn("번호").setMaxWidth(0);

			setBorder(new EmptyBorder(5, 5, 5, 5));

			data();
		}

		private void data() {
			for (var rs : getRows(
					"select name, if(type=1,'병원',if(type=2,'진료소','거주지')), info, open, close, img, no from building where type <> 3")) {
				rs.set(5, getIcon(rs.get(5), 200, 80));
				m.addRow(rs.toArray());
			}
		}
	}

	class Chart extends JPanel {
		JComboBox<String> com = new JComboBox<>("상위 백신 Top4,상위 병원 Top5,상위 진료소 Top5".split(","));
		JPanel chart;
		Color[] col = { Color.red, Color.orange, Color.yellow, Color.green, Color.blue };
		String sql[] = {
				"select v.name, count(*) from vaccine v, purchase p where p.vaccine = v.no and p.date <= now() group by v.no order by count(*) desc",
				"select b.name, count(*) from building b, purchase p where p.building = b.no and p.date <= now() and b.type = 1 group by b.no order by count(*) desc limit 5",
				"select b.name, count(*) from building b, purchase p where p.building = b.no and p.date <= now() and b.type = 0 group by b.no order by count(*) desc limit 5" };

		public Chart() {
			setLayout(new BorderLayout());

			var n = new JPanel(new FlowLayout(2));

			add(n, "North");
			add(chart = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					var rs = getRows(sql[com.getSelectedIndex()]);
					int sum = rs.stream().mapToInt(a -> toInt(a.get(1))).sum();
					double arc = 90;
					int h = 250;

					for (var r : rs) {
						int i = rs.indexOf(r);
						var ar = toInt(r.get(1)) / (double) sum * 360 * -1;
						var shp = new Arc2D.Double(150, 100, 300, 300, arc, ar, Arc2D.PIE);

						g2.setColor(col[i]);
						g2.fill(shp);
						g2.fillOval(550, h-15, 20, 20);
						
						g2.setColor(Color.black);
						g2.drawString(r.get(0).toString(), 575, h);

						int x = (int) ((shp.getEndPoint().getX() + shp.getStartPoint().getX()) / 2);
						int y = (int) ((shp.getEndPoint().getY() + shp.getStartPoint().getY()) / 2);

						g2.drawString(String.format("%.1f", -(ar / 360) * 100) + "%", x, y);
						
						arc += ar;
						h += 25;
					}
				}
			});

			n.add(com);

			com.addActionListener(i -> repaint());
		}
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new AdminPage());
		mf.setVisible(true);
	}
}
