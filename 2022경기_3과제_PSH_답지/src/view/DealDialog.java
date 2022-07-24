package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class DealDialog extends BaseDialog {
	LocalDate date;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.black);
		return lbl;
	}

	public DealDialog(LocalDate date) {
		super(date + " 거래내역", 500, 500);

		this.date = date;

		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));

		for (var rs : getRows(
				"select u1.u_img, u1.u_name, i_img, i_name, format(m_price, '#,##0'), u2.u_img, u2.u_name  from deal d, market m, storage s, item i, user u1, user u2 where d.m_no=m.m_no and m.s_no = s.s_no and s.i_no = i.i_no and s.u_no = u1.u_no and d.u_no = u2.u_no and d_date = ?",
				date.toString())) {
			var tmp = new JPanel(new BorderLayout());
			var tmpW = new JPanel(new BorderLayout());
			var tmpC = new JPanel(new BorderLayout());
			var tmpE = new JPanel(new BorderLayout());

			tmp.add(tmpW, "West");
			tmp.add(tmpC);
			tmp.add(tmpE, "East");

			tmpW.add(lbl("판매자", 0, 15), "North");
			tmpW.add(new JLabel(getIcon(rs.get(0), 100, 100)));
			tmpW.add(lbl(rs.get(1).toString(), 0, 15), "South");

			tmpC.add(new JLabel(getIcon(rs.get(2), 80, 80)), "North");
			tmpC.add(lbl("아이템명 : " + rs.get(3), 0));
			tmpC.add(lbl("판매 가격 : " + rs.get(4) + "원", 0), "South");

			tmpE.add(lbl("구매자", 0, 15), "North");
			tmpE.add(new JLabel(getIcon(rs.get(5), 100, 100)));
			tmpE.add(lbl(rs.get(6).toString(), 0, 15), "South");

			tmp.setBackground(Color.white);
			tmp.setOpaque(true);
			tmp.setBorder(new LineBorder(Color.black));

			c.add(tmp);
		}
	}
}
