package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class TradeHistory extends BaseFrame {
	ButtonGroup buttonGroup = new ButtonGroup();
	JRadioButton rad[] = Stream.of("구매,판매".split(",")).map(a -> {
		var r = new JRadioButton(a + "내역");
		buttonGroup.add(r);
		r.addActionListener(e -> data());
		return r;
	}).toArray(JRadioButton[]::new);
	JLabel img;
	JComboBox<String> com = new JComboBox<String>("전체,1월,2월,3월,4월,5월,6월,7월,8월,9월,10월,11월,12월".split(","));
	DefaultTableModel m = model("번호,상품명,날짜,수량,금액,bno".split(","));
	JTable t = table(m);
	JTextField txt = new JTextField(15);
	String sql[] = {
			"select p_no, b_name, p_date, p_quantity, format(f_amount, '#,##0'), b.b_no from purchase p, farm f, base b where p.f_no = f.f_no and f.b_no = b.b_no and p.u_no = ? and month(p_date) like ? order by p_date;",
			"select s_no, b_name, s_date, s_quantity,  format(f_amount, '#,##0'), b.b_no from sale s, farm f, base b where s.f_no = f.f_no and f.b_no = b.b_no and f.u_no = ? and month(s_date) like ? order by s_date" };

	public TradeHistory() {
		super("거래내역", 1000, 500);

		add(n = new JPanel(), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new FlowLayout(0)), "South");

		n.add(lbl("농산물 거래내역", 0, 30));
		n.add(rad[0]);
		n.add(rad[1]);

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(new JScrollPane(t));
		c.add(img = sz(new JLabel(), 300, 200), "East");

		cn.add(lbl("월", 2, 15));
		cn.add(com);

		s.add(lbl("합계 : ", 2));
		s.add(txt);

		event(t, e -> {
			if (t.getSelectedRow() == -1)
				return;

			if (e.getClickCount() == 2) {
				var blob = getRows("select b_img from base where b_no = ?", t.getValueAt(t.getSelectedRow(), 5)).get(0)
						.get(0);
				img.setIcon(getIcon(blob, 300, 400));
			}
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 3)
					return;

				var pop = new JPopupMenu();
				var i1 = new JMenuItem("가격비교");
				var i2 = new JMenuItem("농산물관리");

				pop.add(rad[0].isSelected() ? i1 : i2);

				i1.addActionListener(a -> new Chart(toInt(t.getValueAt(t.getSelectedRow(), 5)))
						.addWindowListener(new Before(TradeHistory.this)));
				i2.addActionListener(a -> new BaseManage(toInt(t.getValueAt(t.getSelectedRow(), 5)))
						.addWindowListener(new Before(TradeHistory.this)));
			}
		});

		rad[0].setSelected(true);

		data();

		t.getColumn("bno").setMinWidth(0);
		t.getColumn("bno").setMaxWidth(0);

		img.setBorder(new LineBorder(Color.black));
		txt.setEnabled(false);

		setVisible(true);
	}

	private void data() {
		img.setIcon(null);
		m.setRowCount(0);

		int tot = 0;
		for (var rs : getRows(sql[rad[0].isSelected() ? 0 : 1], user.get(0),
				com.getSelectedIndex() == 0 ? "%%" : "%" + com.getSelectedIndex() + "%")) {
			tot += toInt(rs.get(3)) * toInt(rs.get(4));

			m.addRow(rs.toArray());
		}

		txt.setText(format(tot));
	}

	public static void main(String[] args) {
		new TradeHistory();
	}
}
