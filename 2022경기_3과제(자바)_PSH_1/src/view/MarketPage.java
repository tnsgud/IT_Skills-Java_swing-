package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class MarketPage extends BasePage {
	DefaultTableModel m1 = model("아이템,게임명,아이템명,s_no,i_no".split(","));
	JTable t = table(m1);
	JTextField txt = new JTextField();
	JLabel lblExp;

	public static void main(String[] args) {
		new LoginFrame();
	}
	
	public MarketPage() {
		super("장터");

		add(lblExp = lbl("", 4, 20), "North");
		add(c = new JPanel(new GridLayout(1, 0, 40, 20)));

		c.add(cw = new JPanel(new BorderLayout()));
		c.add(ce = new JPanel(new BorderLayout()));

		c.setBorder(new EmptyBorder(30, 30, 30, 30));

		storage();
		search();

		mf.repaint();
		
		setBackground(Color.white);
		setOpaque(true);
	}

	void storage() {
		cw.removeAll();
		
		var cws = new JPanel(new FlowLayout(2));
		var btn = btn("등록하기", a -> {

			var dia = new MarketDialog("장터등록", toInt(t.getValueAt(t.getSelectedRow(), 4)));
			dia.s_no = toInt(t.getValueAt(t.getSelectedRow(), 3));
			dia.setVisible(true);
		});

		cw.setBorder(new LineBorder(Color.black));

		cw.add(lbl("<html><font color='black'>보관함", 0, 25), "North");
		cw.add(new JScrollPane(t));
		cw.add(cws, "South");
		cws.add(btn);

		for (var col : "s_no,i_no".split(",")) {
			t.getColumn(col).setMinWidth(0);
			t.getColumn(col).setMaxWidth(0);
		}
		;

		t.setRowHeight(50);

		m1.setRowCount(0);
		for (var rs : getRows(
				"select i_img, g_name, i_name, s.s_no, i.i_no from v2, storage s, game g, item i where v2.s_no = s.s_no and g.g_no = v2.g_no and i.i_no=s.i_no and v2.u_no = ?",
				user.get(0))) {
			rs.set(0, new JLabel(getIcon(rs.get(0), 50, 50)));
			m1.addRow(rs.toArray());
		}
		
		lblExp.setText("<html><font color='black'>경험치 : "+u_exp+"[등급 : "+g_gd[u_gd]+"]");
		
		cw.repaint();
		cw.revalidate();
	}

	void search() {
		ce.removeAll();
		
		var cen = new JPanel(new BorderLayout());
		var cec = new JPanel(new GridLayout(0, 1));

		ce.add(cen, "North");
		ce.add(new JScrollPane(cec));

		cen.add(lbl("<html><font color='black'>검색", 2), "West");
		cen.add(txt);
		cen.add(btn("검색", a -> {
			cec.removeAll();

			for (var rs : getRows(
					"select i_img, g_name, i_name, format(m_price, '#,##0'), m.u_no, m_no, i.i_no from market m, storage s, item i, game g where m.s_no = s.s_no and s.i_no = i.i_no and i.g_no = g.g_no and m.m_ox = 0 and (i_name like ? or g_name like ?)",
					"%" + txt.getText() + "%", "%" + txt.getText() + "%")) {
				var tmp = new JPanel(new BorderLayout(5, 5));
				var tmp_c = new JPanel(new GridLayout(0, 1));
				var cap = "게임명,아이템명,가격".split(",");
				var flag = rs.get(4).equals(user.get(0));
				var pop = new JPopupMenu();

				for (var c : "구매하기,판매취소".split(",")) {
					var i = new JMenuItem(c);

					i.addActionListener(e -> {
						if (c.equals("구매하기")) {
							var dia = new MarketDialog("장터구매", toInt(rs.get(6)), rs.get(3).toString());
							dia.m_no = toInt(rs.get(5));
							dia.setVisible(true);
						} else {
							iMsg("판매취소가 완료되었습니다.");
							execute("delete from market where m_no =?", rs.get(5));
							createV();
							storage();
							((JButton) a.getSource()).doClick();
						}
					});

					if (c.equals("구매하기")) {
						i.setEnabled(!flag);
					} else {
						i.setEnabled(flag);
					}

					pop.add(i);
				}

				for (int i = 0; i < cap.length; i++) {
					var lbl = lbl(cap[i] + " : " + rs.get(i + 1), 2, 15);
					lbl.setForeground(flag ? Color.blue : Color.black);
					tmp_c.add(lbl);
				}

				tmp.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
				tmp.setComponentPopupMenu(pop);

				tmp.setOpaque(false);
				tmp_c.setOpaque(false);

				tmp.add(new JLabel(getIcon(rs.get(0), 100, 100)), "West");
				tmp.add(tmp_c);

				cec.add(tmp);
			}

			repaint();
			revalidate();
		}), "East");
		
		ce.repaint();
		ce.revalidate();
	}
}
