package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.stream.Stream;

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
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class AdminPage extends BasePage {
	public AdminPage() {
		add(sz(w = new JPanel() {
			@Override
			public void setOpaque(boolean isOpaque) {
				super.setOpaque(true);
			}
		}, 250, 0), "West");
		add(c = new JPanel(new BorderLayout()));
		w.setLayout(new BoxLayout(w, BoxLayout.Y_AXIS));

		for (var cap : "&#128100 회원관리,&#127968 건물관리,&#128200 통계,&#128275 로그아웃".split(",")) {
			w.add(event(lblHyp("<html>" + cap, 2, 15), e -> {
				Stream.of(w.getComponents()).forEach(a -> ((JComponent) a).setBorder(null));

				var me = (JLabel) e.getSource();
				me.setBorder(
						new CompoundBorder(new MatteBorder(0, 2, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));
				me.setBackground(blue);

				c.removeAll();
				if (me.getText().contains("회원")) {
					c.add(new User());
				} else if (me.getText().contains("건물")) {
					c.add(new Building());
				} else if (me.getText().contains("통계")) {
					c.add(new Chart());
				} else {
					mf.swap(new LoginPage());
				}
			}));
			w.add(Box.createVerticalStrut(5));
		}

		c.add(new User());
		((JLabel) w.getComponent(0))
				.setBorder(new CompoundBorder(new MatteBorder(0, 2, 0, 0, Color.orange), new EmptyBorder(0, 5, 0, 0)));

		w.setBorder(new EmptyBorder(10, 10, 10, 10));
		w.setBackground(blue);
	}

	class User extends BasePage {
		DefaultTableModel m = new DefaultTableModel(null, "번호,이름,아이디,비밀번호,전화번호,생일,거주지".split(",")) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 1 && column != 2;
			}
		};
		JTable t = table(m);
		JComboBox<String> com = new JComboBox<String>(getRows("select name from building where type = 2").stream()
				.map(a -> a.get(0).toString()).toArray(String[]::new));

		public User() {
			setLayout(new BorderLayout(5, 5));

			add(new JScrollPane(t));
			add(s = new JPanel(new FlowLayout(2)), "South");

			for (var cap : "수정,삭제".split(",")) {
				s.add(btn(cap, a -> {
					if (cap.equals("삭제")) {
						if (t.getSelectedRow() == -1) {
							eMsg("삭제할 행을 선택해주세요.");
							return;
						}

						execute("delete from user where no =?", t.getValueAt(t.getSelectedRow(), 0));
						m.removeRow(t.getSelectedRow());
						return;
					}

					var data = m.getDataVector();
					if (data.stream().flatMap(e -> e.stream()).filter(e -> e.toString().isEmpty()).count() > 0) {
						eMsg("빈칸이 존재합니다.");
						return;
					}

					for (var row : m.getDataVector()) {
						var phone = row.get(4).toString();
						var birth = row.get(5).toString();
//						문제지에 없는 항목 하지만 나중에 질문으로 나올듯
//						if (!phone.matches("^\\d{3}-\\d{4}-\\d{4}$")) {
//							eMsg("전화번호를 확인해주세요.");
//							return;
//						}
//
//						try {
//							if (LocalDate.parse(birth).isAfter(LocalDate.now())) {
//								eMsg("생년월일을 확인해주세요.");
//								return;
//							}
//						} catch (Exception e) {
//							eMsg("생년월일을 확인해주세요.");
//							return;
//						}
						execute("update user set name = ?, pw= ?, phone = ?, birth = ?, building = ? where no = ?",
								row.get(1), data.get(3), phone, birth,
								getOne("select no from building where name = ?", row.get(6)), row.get(0));
					}
				}));

				t.setRowHeight(30);

				t.getColumn("거주지").setCellEditor(new DefaultCellEditor(com));

				setBorder(new EmptyBorder(5, 5, 5, 5));
				for (var rs : getRows(
						"select u.no, u.name, u.id, u.pw, u.phone, u.birth, b.name from user u, building b where u.building = b.no")) {
					System.out.println(rs);
					m.addRow(rs.toArray());
				}
			}
		}
	}

	class Building extends BasePage {
		DefaultTableModel m = new DefaultTableModel(null, "이름,종류,설명,시작시간,종료시간,사진,번호".split(",")) {
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
			add(new JScrollPane(t));
			add(s = new JPanel(new FlowLayout(2)), "South");

			s.add(btn("저장", a -> {
				for (var row : m.getDataVector()) {
					execute("update building set name = ?, info=?, open = ?, close = ? where no = ?", row.get(0),
							row.get(2), row.get(3), row.get(4), row.get(6));
				}
				
				iMsg("저장이 완료되었습니다.");
			}));

			t.setRowHeight(80);
			t.getColumn("사진").setCellRenderer(null);
			t.getColumn("번호").setMinWidth(0);
			t.getColumn("번호").setMaxWidth(0);

			setBorder(new EmptyBorder(5, 5, 5, 5));

			event(t, e -> {
				if ((t.getSelectedColumn() == 3 || t.getSelectedColumn() == 4)
						&& t.getValueAt(t.getSelectedRow(), 1).toString().equals("거주지")) {
					eMsg("거주지는 수정이 불가합니다.");
					return;
				}
			});

			for (var rs : getRows("select name, type, info, open, close, img, no from building where type <> 3")) {
				rs.set(1, "진료소,병원,거주지".split(",")[toInt(rs.get(1))]);
				rs.set(5, getIcon(rs.get(5), 200, 80));
				m.addRow(rs.toArray());
			}
		}
	}

	class Chart extends BasePage {

	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new AdminPage());
		mf.setVisible(true);
	}
}
