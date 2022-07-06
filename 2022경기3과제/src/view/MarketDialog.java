
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MarketDialog extends BaseDialog {
	DefaultTableModel m = model("날짜,판매가".split(","));
	JTable t = table(m);
	JTextField txt;
	JButton btn;
	int s_no, m_no, price;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.black);
		return lbl;
	}

	public MarketDialog(String tit, int i_no) {
		super(tit, 300, 400);

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(sz(new JScrollPane(t), 250, 100), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var rs = getRows(
				"select d_date, m_price from deal d, market m, storage s where d.m_no = m.m_no and m.s_no = s.s_no and m_ox = 1 and s.i_no = ? order by d_date desc",
				i_no);

		var min = rs.stream().mapToInt(r -> toInt(r.get(1))).min().getAsInt();
		var avg = format(toInt(getOne(
				"select round(avg(m_price), 0) from deal d, market m, storage s where d.m_no = m.m_no and m.s_no = s.s_no and m_ox = 1 and s.i_no = ? order by d_date desc",
				i_no)));
		var idx = rs.stream().filter(r -> r.get(1).equals(min)).map(r -> rs.indexOf(r)).findFirst().get();

		for (var r : rs) {
			var lbl1 = lbl(r.get(0).toString(), 0);
			var lbl2 = lbl(format(toInt(r.get(1))), 0);

			if (rs.indexOf(r) == idx) {
				lbl1.setForeground(Color.red);
				lbl2.setForeground(Color.red);
			}

			r.set(0, lbl1);
			r.set(1, lbl2);

			m.addRow(r.toArray());
		}

		var cap = "아이템명,마지막 판매가,평균 판매가,판매가".split(",");
		var obj = new Object[] { getOne("select i_name from item where i_no=?", i_no),
				((JLabel) t.getValueAt(0, 1)).getText(), avg };
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 5, 5));

			tmp.add(sz(lbl(cap[i], 2, 15), 100, 20), "West");

			if (i == 3) {
				tmp.add(txt = new JTextField(15));
			} else {
				tmp.add(lbl(obj[i].toString(), 2, 15));
			}

			cc.add(tmp);
		}

		s.add(btn = btn("등록", a -> {
			if (a.getActionCommand().equals("등록")) {
				var text = txt.getText();
				if (text.isEmpty() || toInt(text) < 1) {
					eMsg("1보다 큰 숫자로 입력하세요.");
					return;
				}
				var storage = (StoragePage) BasePage.mf.c.getComponent(0);

				iMsg("등록이 완료되었습니다.");
				execute("isnert into market values(0,?,?,?,?)", BasePage.user.get(0), s_no, txt.getText(), 0);

				storage.addRow();
				dispose();
			} else {
				if (toInt(BasePage.user.get(5)) < price) {
					eMsg("금액이 부족합니다.");
					new Charge().setVisible(true);;
					return;
				}

				execute("update market set m_ox = 1 where m_no = ?", m_no);
				execute("insert into deal values(0, ?, ?, ?)", BasePage.user.get(0), m_no, LocalDate.now());
				execute("update user set u_money=u_money-? where u_no = ?", price, BasePage.user.get(0));
				BasePage.user = getRows("select * from user where u_no = ?", BasePage.user.get(0)).get(0);
				
				new Info(Arrays.asList());
			
				dispose();
			}
		}));
	}

	public MarketDialog(String t, int i_no, String price) {
		this(t, i_no);
		this.price = toInt(price);
		txt.setText(price);
		btn.setText("구매");
	}
}
