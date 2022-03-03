package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	West w;
	DefaultTableModel m = model("pno,공연날짜,공연명,공연가격".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	JTextField txt = new JTextField(15);
	JMenuBar bar = new JMenuBar();
	JMenu menu = new JMenu("분류");
	String name[] = ",M,O,C".split(","), type = "", key = "",
			sql = "select p_no, p_date,p_name, format(p_price, '#,##0') from perform where p_date >= '2021-10-06' and p_name like ? and pf_no like ? order by p_date asc, p_price desc";

	public Search() {
		super("검색", 800, 650);

		bar.add(menu);
		var i = 0;
		for (var cap : "전체,뮤지컬,오페라,콘서트".split(",")) {
			var item = new JMenuItem(cap);
			item.setName(name[i]);
			menu.add(item);
			if (cap.equals("전체")) {
				menu.addSeparator();
			}
			item.addActionListener(a -> {
				w.tit.setText("분류 : " + a.getActionCommand());
				type = ((JMenuItem) a.getSource()).getName();

				w.idx = 0;
				w.addImage();
			});
			i++;
		}

		setJMenuBar(bar);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pno = toInt(((JTable)e.getSource()).getValueAt(t.getSelectedRow(), 0));
				new Reserve().addWindowListener(new Before(Search.this));
			}
		});
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout(2)), "North");
		add(w = new West(), "West");
		add(c = new JPanel(new BorderLayout()));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		addRow(m, sql, "%" + key + "%", "%" + type + "%");

		n.add(lbl("공연명 : ", 0));
		n.add(txt);
		n.add(btn("검색", a -> {
			key = txt.getText();
			var rs = rs(sql, "%" + key + "%", "%" + type + "%");
			try {
				if (rs.next()) {
					addRow(m, sql, "%" + key + "%", "%" + type + "%");
				} else {
					eMsg("검색 결과가 없습니다.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		c.add(lbl("현재 예매가능 공연", 4), "North");
		c.add(scr);

		scr.setBorder(new LineBorder(Color.black));
	}

	class West extends JPanel {
		int idx = 0;
		JLabel tit = lbl("분류 : 전체", 4, 15), prev = lbl("◀", 0, 15), next = lbl("▶", 0, 15), first = lbl("《처음으로", 0, 15),
				last = lbl("마지막으로》", 0, 15), lbls[];
		JPanel c, s, s_n, s_c;
		ArrayList<ArrayList<String>> performs = new ArrayList<ArrayList<String>>();

		public West() {
			setLayout(new BorderLayout(5, 5));
			setBorder(new EmptyBorder(100, 5, 100, 5));

			this.ui();
			this.event();
		}

		private void event() {
			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (idx != 0) {
						idx--;
						addImage();
					}
				}
			});
			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (idx != performs.size() / 4) {
						idx++;
						addImage();
					}
				}
			});
			first.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					idx = 0;
					addImage();
				}
			});
			last.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					idx = performs.size() / 4;
					addImage();
				}
			});
		}

		private void ui() {
			add(tit, "North");
			add(prev, "West");
			add(c = new JPanel(new GridLayout(2, 2, 5, 5)));
			add(next, "East");
			add(s = new JPanel(new BorderLayout()), "South");

			s.add(s_n = new JPanel(), "North");
			s.add(s_c = new JPanel());

			s_c.add(first);
			s_c.add(last);

			addImage();
		}

		private void addImage() {
			c.removeAll();
			s_n.removeAll();

			performs = toArray(
					"select * from perform where '2021-10-06' <= p_date and pf_no like ? and p_name like ? group by p_name order by p_date asc, p_price desc",
					"%" + type + "%", "%" + key + "%");

			for (int i = idx * 4; i <= idx * 4 + 3; i++) {
				var l = lbl("", 0);

				if (i < performs.size()) {
					l.setIcon(img("공연사진/" + performs.get(i).get(1) + ".jpg", 150, 150).getIcon());
					l.setName(i + "");
					l.setToolTipText(performs.get(i).get(2) + "/" + performs.get(i).get(6));
				} else {
					l.setName("-1");
				}

				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var n = ((JLabel) e.getSource()).getName();
						if (n.contentEquals("-1")) {
							eMsg("공연정보가 없습니다.");
							return;
						}

						BaseFrame.pno = toInt(performs.get(toInt(n)).get(0));
						new Reserve().addWindowListener(new Before(Search.this));
					}
				});

				l.setBorder(new LineBorder(Color.black));
				c.add(sz(l, 150, 150));
			}

			lbls = new JLabel[performs.size() % 4 == 0 ? performs.size() : (performs.size() / 4) + 1];
			for (int i = 0; i < lbls.length; i++) {
				lbls[i] = lbl("●", 0);
				s_n.add(lbls[i]);
			}
			lbls[idx].setForeground(Color.red);

			repaint();
			revalidate();
		}
	}
}
