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
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	JMenuBar bar = new JMenuBar();
	DefaultTableModel m = model("pno,공연날짜,공연명,공연가격".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	String name[] = ",M,O,C".split(","), type = "", key = "";
	West w;
	JTextField txt = new JTextField(17);
	JMenu menu = new JMenu("분류");
	JMenuItem items[] = { new JMenuItem("전체"), new JMenuItem("뮤지컬"), new JMenuItem("오페라"), new JMenuItem("콘서트") };

	{
		setJMenuBar(bar);
		bar.add(menu);
		menu.add(items[0]);
		menu.addSeparator();
		for (int i = 1; i < items.length; i++) {
			menu.add(items[i]);
		}
	}

	public Search() {
		super("검색", 850, 500);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		for (int i = 0; i < items.length; i++) {
			items[i].setName(name[i]);
			items[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					type = ((JMenuItem) e.getSource()).getName();

					w.idx = 0;
					w.tit.setText("분류 :" + ((JMenuItem) e.getSource()).getText());
					w.addImage();
				}
			});
		}
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				new Reserve().addWindowListener(new Before(Search.this));
			}
		});
	}

	private void data() {
		m.setRowCount(0);
		var rs = rs(
				"select p_no, p_date, p_name, format(p_price, '#,##0') from perform where '2021-10-06' <= p_date and pf_no like ? and p_name like ? order by p_date asc, p_price desc",
				"%" + type + "%", "%" + key + "%");
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
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
		add(n = new JPanel(new FlowLayout(2)), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));
		add(e = new JPanel(new BorderLayout(10, 10)), "East");

		n.add(lbl("공연명 : ", 0));
		n.add(txt);
		n.add(btn("검색", a -> {
			key = txt.getText();

			w.idx = 0;
			w.addImage();
			data();
		}));

		c.add(w = new West(), "West");

		e.add(lbl("현재 예매 가능 공연", 4, 15), "North");
		e.add(scr);

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		scr.setBorder(new LineBorder(Color.black));
	}

	class West extends JPanel {
		int idx = 0;
		JLabel tit = lbl("분류 : 전체", 4, 15), prev = lbl("◀", 0, 15), next = lbl("▶", 0, 15), first = lbl("《처음으로", 0, 15),
				last = lbl("마지막으로》", 0, 15), lbls[];
		JPanel c, s, s_n, s_c;
		ArrayList<ArrayList<String>> performs = new ArrayList<ArrayList<String>>();

		public West() {
			setLayout(new BorderLayout(10, 10));

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
					if (idx != lbls.length - 1) {
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
					idx = lbls.length - 1;
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
						var i = toInt(((JLabel) e.getSource()).getName());
						if (i == -1) {
							eMsg("공연정보가 없습니다.");
							return;
						}

						pno = i;
						new Reserve().addWindowListener(new Before(Search.this));
					}
				});

				l.setBorder(new LineBorder(Color.black));
				c.add(sz(l, 150, 150));
			}

			lbls = new JLabel[performs.size() % 4 == 0 ? performs.size() : performs.size() / 4 + 1];
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
