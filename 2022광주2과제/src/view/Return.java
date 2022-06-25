package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Return extends BaseFrame {
	ArrayList<JLabel> tabs = new ArrayList<>();
	ArrayList<Item> items = new ArrayList<>();
	JCheckBox chk = new JCheckBox("Select All");
	JLabel cnt = lbl("0", 0);
	JPanel ccc;

	DefaultTableModel m = new DefaultTableModel(null, "No,Title,Num,Reading,State,Renew".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	JTable t = new JTable(m);

	public Return() {
		super("Return", 600, 400);

		var l = lbl("Return", 0, 30);

		setLayout(new BorderLayout(0, 0));

		add(l, "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(e = new JPanel(), "East");
		add(s = new JPanel(new FlowLayout(1)), "South");

		c.add(cn = new JPanel(new GridLayout(1, 0, 5, 5)), "North");
		c.add(cc = new JPanel(new BorderLayout(5, 5)));
		c.add(cs = new JPanel(new FlowLayout(1, 5, 5)), "South");

		var rs1 = getRows(
				"SELECT b.b_no, b_title FROM v2 inner join book b on v2.b_no = b.b_no where m_no = ? order by b.b_no asc",
				user.get(0));
		var rs2 = getRows(
				"select b.b_no, b_title from book b inner join borrow bo on b.b_no = bo.b_no inner join `return` r on bo.bo_no = r.bo_no where m_no = ? and bo_endate < r_date",
				user.get(0));

		for (var cap : "Return,Close".split(",")) {
			var btn = btn(cap, a -> {
				if (cap.equals("Return")) {
					if (toInt(cnt.getText()) == 0) {
						eMsg("반납할 도서를 선택해주세요.");
						return;
					}

					items.stream().filter(i -> i.isSelect).forEach(item -> {
						var boNo = getOne("select bo_no from borrow where m_no =? and b_no = ?", user.get(0),
								item.getName());

						execute("insert into `return` values(0, ?, ?)", boNo, LocalDate.now());
					});
				} else {
					dispose();
				}
			});
			btn.setForeground(Color.black);
			btn.setBackground(Color.white);
			s.add(btn);
		}

		var cap = "대출,연체,이전 대출 내역".split(",");
		var data = new Object[] { rs1.size(), rs2.size() };
		for (int i = 0; i < cap.length; i++) {
			var lbl = sz(lbl(cap[i] + (i < 2 ? ":" + data[i] : ""), 0), 20, 30);
			int j = i;
			lbl.setOpaque(true);
			lbl.setForeground(i < 2 ? Color.white : Color.black);
			lbl.setBackground(i < 2 ? Color.gray : Color.white);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (cap[j].contains("이전 대출")) {
						borrowTable();
					} else if (cap[j].contains("대출")) {
						ccUI(rs1);
					} else if (cap[j].contains("연체")) {
						ccUI(rs2);
					}
				}
			});
			cn.add(lbl);
		}

		{
			var temp = new JPanel(new FlowLayout(0, 10, 10));
			temp.setOpaque(false);

			temp.add(lbl("<html><font color='white'>Select", 0));
			temp.add(cnt);
			temp.add(chk);

			chk.addActionListener(a -> {
				items.forEach(lbl -> {
					lbl.isSelect = chk.isSelected();
					lbl.setGround();

					repaint();
					revalidate();
				});
			});

			cc.add(temp, "North");
		}

		ccc = new JPanel(new FlowLayout(0, 5, 5));
		ccc.setOpaque(false);

		cc.add(ccc);

		getContentPane().setBackground(Color.white);
		c.setBackground(Color.black);
		c.setBorder(new EmptyBorder(10, 10, 10, 10));
		cn.setOpaque(false);
		cc.setOpaque(false);
		cs.setOpaque(false);
		s.setBackground(Color.black);
		s.setOpaque(true);
		cnt.setForeground(Color.orange);
		chk.setForeground(Color.white);
		chk.setOpaque(false);

		ccUI(rs1);

		setVisible(true);
	}
	


//	select * from book b inner join borrow bo on b.b_no = bo.b_no inner join `return` r on bo.bo_no = r.bo_no where r_date <= now() and m_no= 1;
//	select * from v2 where bo_no = (select bo_no from borrow where m_no = 1 and b_no = 1);
//	select * from `return` r inner join borrow bo on r.bo_no = bo.bo_no where m_no = 1 and b_no = 1;
//	select b_title, bo_num, concat(round(bo_reading/b_page*100, 0), '%') as persent, if((select * from re where re.bo_no = bo.bo_no) is not null, '', if((), 0, 1))from book b inner join borrow bo on b.b_no = bo.b_no where m_no = 1
	
	private void borrowTable() {
		ccc.removeAll();
		ccc.setBorder(null);
		ccc.setLayout(new BorderLayout());
		ccc.add(new JScrollPane(t));

		var rs = getRows("select b_title, bo_num, concat(round(bo_reading/b_page*100, 0), '%'),  from book b inner join borrow bo on b.b_no = bo.b_no where m_no = ?", user.get(0));

		for (var r : rs) {
			r.add(0, rs.indexOf(r));
			
			m.addRow(r.toArray());
		}

		chk.setEnabled(false);

		repaint();
		revalidate();
	}

	void ccUI(ArrayList<ArrayList<Object>> rs) {
		cnt.setText("0");
		chk.setEnabled(true);
		chk.setSelected(false);
		ccc.removeAll();
		items.clear();

		var btn = (JButton) s.getComponent(0);
		btn.setEnabled(true);

		if (rs.isEmpty()) {
			var lbl = lbl("No Book", 0, 25);
			lbl.setForeground(Color.black);
			lbl.setBackground(Color.white);
			lbl.setOpaque(true);
			ccc.setBorder(null);
			ccc.setLayout(new BorderLayout());
			btn.setEnabled(false);
			ccc.add(lbl);
		}

		for (var r : rs) {
			var item = sz(new Item(r), 130, 150);
			items.add(item);
			ccc.add(item);
		}

		ccc.setBorder(new CompoundBorder(new LineBorder(Color.white), new EmptyBorder(5, 5, 5, 5)));

		repaint();
		revalidate();
	}

	class Item extends JPanel {
		JLabel img, lbl;

		boolean isSelect = false;

		public Item(ArrayList<Object> r) {
			super(new BorderLayout());

			setName(r.get(0).toString());

			img = new JLabel(getIcon("./Datafiles/book/" + r.get(1) + ".jpg", 120, 130));
			lbl = lbl(r.get(1).toString(), 0);

			setBackground(Color.black);
			setBorder(new LineBorder(Color.white));

			lbl.setForeground(Color.white);

			add(img);
			add(lbl, "South");

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					isSelect = getBackground() == Color.black;

					setGround();

				}
			});
		}

		void setGround() {
			setBackground(isSelect ? Color.white : Color.black);
			lbl.setForeground(isSelect ? Color.black : Color.white);

			cnt.setText(toInt(cnt.getText()) + (isSelect ? 1 : -1) + "");
		}
	}

	public static void main(String[] args) {
		new Return();
	}
}
