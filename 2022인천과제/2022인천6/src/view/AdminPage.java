package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AdminPage extends BasePage {
	public AdminPage() {
		add(w = sz(new JPanel(new FlowLayout(0)), 200, 240), "West");
		add(c = new JPanel(new BorderLayout()));

		w.setBackground(blue);

		c.add(new User());

		for (var cap : "<html>&#128100 회원관리,<html>&#127968 건물관리,<html>&#128200 통계,<html>&#128275 로그아웃".split(",")) {
			var lbl = sz(hyplbl(cap, 2, 20, Color.orange, e -> {
				c.removeAll();

				var l = (JLabel) e.getSource();
				for (var com : w.getComponents()) {
					((JComponent) com).setBorder(null);
				}

				l.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));

				if (cap.contains("회원")) {
					c.add(new User());
				} else if (cap.contains("건물")) {
					c.add(new Building());
				} else if (cap.contains("통계")) {
					c.add(new Chart());
				} else {
					mf.swapPage(new LoginPage());
				}
			}), 200, 40);
			w.add(lbl);
		}

		var lbl = (JLabel) w.getComponents()[0];
		lbl.setBorder(new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));
	}

	class User extends JPanel {
		DefaultTableModel m = new DefaultTableModel(null, "번호,이름,아이디,비밀번호,전화번호,생일,거주지".split(",")) {
			public boolean isCellEditable(int row, int column) {
				return column == 1 || (column > 2 && column < 7);
			};
		};
		JTable t = table(m);
		JComboBox<String> com = new JComboBox<>(getRows("select name from building where type = 2").stream()
				.flatMap(a -> a.stream()).toArray(String[]::new));

		public User() {
			var s = new JPanel(new FlowLayout(2));

			setLayout(new BorderLayout());

			setBorder(new EmptyBorder(10, 10, 10, 10));

			add(new JScrollPane(t));
			add(s, "South");

			t.setRowHeight(30);
			t.getColumn("거주지").setCellEditor(new DefaultCellEditor(com));

			for (var cap : "수정,삭제".split(",")) {
				s.add(btn(cap, nula -> {
					if (cap.equals("수정")) {
						for (int i = 0; i < t.getRowCount(); i++) {
							for (int j = 0; j < t.getColumnCount(); j++) {
								if (t.getValueAt(i, j).toString().isEmpty()) {
									eMsg("빈칸이 존재합니다.");
									return;
								}
							}
						}

						for (int i = 0; i < t.getRowCount(); i++) {
							execute("update user set name=?, pw=?, phone=?, birth=?, building=? where no = ?",
									t.getValueAt(i, 1), t.getValueAt(i, 3), t.getValueAt(i, 4), t.getValueAt(i, 5),
									getOne("select no from building where name = ?", com.getSelectedItem()),
									t.getValueAt(i, 0));
						}

						iMsg("수정이 완료되었습니다.");
					} else {
						if (t.getSelectedRow() == -1) {
							eMsg("삭제할 행을 선택해주세요.");
							return;
						}

						execute("delete from user where no=?", t.getValueAt(t.getSelectedRow(), 0));
						eMsg("삭제가 완료되었습니다.");
						addRow();
					}
				}));
			}

			addRow();
		}

		private void addRow() {
			m.setRowCount(0);
			for (var rs : getRows(
					"select u.no,u.name,u.id,u.pw,u.phone,u.birth,b.name from user u, building b where u.building = b.no")) {
				m.addRow(rs.toArray());
			}
		}
	}

	class Building extends JPanel {
		DefaultTableModel m = new DefaultTableModel(null, "이름,종류,설명,시작시간,종료시간,사진".split(",")) {
			public boolean isCellEditable(int row, int column) {
				if (t.getValueAt(row, 1).toString().equals("거주지")) {
					eMsg("거주지는 수정이 불가합니다.");
					return false;
				}
				return column != 1 && column != 5;
			};

			public java.lang.Class<?> getColumnClass(int columnIndex) {
				return JComponent.class;
			};
		};
		DefaultTableCellRenderer d = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JComponent) {
					return (JComponent) value;
				} else {
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			};
		};
		JTable t = table(m);

		public Building() {
			setLayout(new BorderLayout());

			setBorder(new EmptyBorder(10, 10, 10, 10));

			t.setRowHeight(80);
			d.setHorizontalAlignment(0);

			for (int i = 0; i < t.getColumnCount(); i++) {
				t.getColumnModel().getColumn(i).setCellRenderer(d);
			}

			var cap = "이름,설명,시작시간,종료시간".split(",");
			for (var c : cap) {
				t.getColumn(c).setCellEditor(new DefaultCellEditor(new JTextField()));
			}

			add(new JScrollPane(t));
			add(s = new JPanel(new FlowLayout(2)), "South");

			s.add(btn("저장", a -> {
				for (int i = 0; i < t.getRowCount(); i++) {
					execute("update building set name=?, info=?, open=?, close=? where no=?", t.getValueAt(i, 0),
							t.getValueAt(i, 2), t.getValueAt(i, 3), t.getValueAt(i, 4), t.getValueAt(i, 0));
				}

				eMsg("저장이 완료되었습니다.");
			}));

			addRow();
		}

		private void addRow() {
			m.setRowCount(0);

			for (var rs : getRows(
					"select name, type, info, time_format(open, '%H:%i'), time_format(close, '%H:%i'), img from building where type <> 3")) {
				rs.set(1, "진료소,병원,거주지".split(",")[toInt(rs.get(1))]);
				rs.set(5, new JLabel(getIcon(rs.get(5), 180, 80)));
				m.addRow(rs.toArray());
			}
		}
	}

	class Chart extends JPanel {
		Color col[] = { Color.red, Color.orange, Color.yellow, Color.green, Color.blue };

		JComboBox<String> com = new JComboBox<>("상위 백신 Top4,상위 병원 Top5,상위 진료소 Top5".split(","));
		double arc = 90;

		public Chart() {
			var n = new JPanel(new FlowLayout(2));
			var c = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					int h = 250, sum = 0;
					var g2 = (Graphics2D) g;
					ArrayList<ArrayList<Object>> rs = null;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					if (com.getSelectedIndex() == 0) {
						rs = getRows(
								"select v.name, count(*) from purchase p, vaccine v where p.vaccine=v.no group by v.no");
						sum = toInt(getOne("select count(*) from purchase p, vaccine v where p.vaccine=v.no"));
					} else if (com.getSelectedIndex() == 1) {
						rs = getRows(
								"select b.name, count(*) from building b, purchase p where b.no = p.building and type = 1 group by b.no order by count(*) desc limit 5");
						for (var r : rs) {
							sum += toInt(r.get(1));
						}
					} else {
						rs = getRows(
								"select b.name, count(*) from building b, purchase p where p.building = b.no and type = 0 group by b.no order by count(*) desc limit 5");
						for (var r : rs) {
							sum += toInt(r.get(1));
						}
					}

					for (var r : rs) {
						var a = ((double) toInt(r.get(1)) / (double) sum * 360) * -1;

						g2.setColor(col[rs.indexOf(r)]);
						var arc2d = new Arc2D.Float(Arc2D.PIE);
						arc2d.setFrame(150, 100, 300, 300);
						arc2d.setAngleStart(arc);
						arc2d.setAngleExtent(a);

						int midx = (int) (arc2d.getEndPoint().getX() + arc2d.getStartPoint().getX()) / 2;
						int midy = (int) (arc2d.getEndPoint().getY() + arc2d.getStartPoint().getY()) / 2;

						g2.draw(arc2d);
						g2.fill(arc2d);
						g2.fillOval(600, h - 15, 20, 20);
						g2.setColor(Color.black);
						g2.drawString(r.get(0) + "", 625, h);
						g2.drawString(String.format("%.1f", ((double) toInt(rs.get(1)) / (double) sum) * 100) + "%",
								midx, midy);

						arc += a;
						h += 25;
					}
				}
			};

			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(5, 5, 5, 5));

			add(n, "North");
			add(c);

			n.add(com);

			com.addActionListener(a -> repaint());
		}

	}

	public static void main(String[] args) {
		mf.swapPage(new AdminPage());
		mf.setVisible(true);
	}
}
