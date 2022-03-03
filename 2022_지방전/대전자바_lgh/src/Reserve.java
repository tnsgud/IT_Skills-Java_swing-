import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseFrame {

	DefaultTableModel m = model("날짜,여유좌석,pdate,pno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	JLabel img, date;

	public Reserve() {
		super("예매", 650, 300);

		if (getone("select p_name from perform where p_no = " + pno).equals("싸이")) {
			this.add(lbl("(공연명)싸이", 2, 30), "North");
		} else {
			this.add(lbl(getone("select p_name from perform where p_no = " + pno), 2, 30), "North");
		}

		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		var c_w = new JPanel(new BorderLayout());
		var c_e = new JPanel();

		c.add(c_w, "West");
		c.add(c_e, "East");

		s.add(btn("예매하기", e -> {
			if (uno == "") {
				int yn = JOptionPane.showConfirmDialog(null, "회원만이 가능한 서비스 입니다.\n로그인 하시겠습니까?", "로그인",
						JOptionPane.YES_NO_OPTION);

				if (yn == JOptionPane.YES_OPTION) {
					new Login().addWindowListener(new Before(Reserve.this));
				} else {
					return;
				}
				return;
			}

			pno = t.getValueAt(t.getSelectedRow(), 3).toString();
			new Stage().addWindowListener(new Before(Reserve.this));
		}));

		var imgp = new JPanel();
		var infop = new JPanel(new GridLayout(0, 1, 10, 30));

		imgp.add(img = new JLabel(icon(getone("select pf_no from perform where p_no = " + pno), 150, 150)));
		infop.add(lbl("장소 : " + getone("select p_place from perform where p_no = " + pno), 2, 20));
		infop.add(lbl("출연 : " + getone("select p_actor from perform where p_no = " + pno), 2, 20));
		infop.add(lbl("가격 : " + getone("select format(p_price,'#,##0') from perform where p_no = " + pno), 2, 20));
		infop.add(date = lbl("날짜 : " + getone("select p_date from perform where p_no = " + pno), 2, 20));

		c_e.add(sz(jsc, 150, 170));
		c_w.add(imgp, "West");
		c_w.add(infop);

		addRow(m,
				"select date_format(p.p_date, '%m. %d.'), 60-count(t.t_no), p.p_date, p.p_no from perform p, ticket t where t.p_no = p.p_no and p.p_name like '%"
						+ getone("select p_name from perform where p_no = " + pno) + "%' group by p_no");

		for (int i = 0; i < t.getRowCount(); i++) {
			if (t.getValueAt(i, 3).toString().equals(pno)) {
				t.addRowSelectionInterval(i, i);
			}
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				date.setText("날짜 : " + t.getValueAt(t.getSelectedRow(), 2).toString());
			}
		});

		t.getColumnModel().getColumn(2).setMinWidth(0);
		t.getColumnModel().getColumn(2).setMaxWidth(0);
		t.getColumnModel().getColumn(3).setMinWidth(0);
		t.getColumnModel().getColumn(3).setMaxWidth(0);

		jsc.setBorder(new LineBorder(Color.BLACK));
		c_w.setBorder(new LineBorder(Color.BLACK));

		pack();

		this.setVisible(true);
	}
}
