package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

import view.BaseFrame.Before;

public class TradeHistory extends BaseFrame {
	ButtonGroup bg = new ButtonGroup();
	JRadioButton rad[] = Stream.of("구매,판매".split(",")).map(c -> {
		var r = new JRadioButton(c + "판매");
		bg.add(r);
		r.addActionListener(e -> data());
		r.setOpaque(false);
		return r;
	}).toArray(JRadioButton[]::new);
	DefaultTableModel m = model("번호,상품명,날짜,수량,금액,bno".split(","));
	JTable t = table(m);
	JLabel lblImg;
	JComboBox<String> com = new JComboBox<String>(
			("전체," + IntStream.range(1, 13).mapToObj(num -> num + "월").collect(Collectors.joining(","))).split(","));
	JTextField txt = new JTextField(20);

	public TradeHistory() {
		super("거래내역", 1000, 500);

		user = getRows("select * from user where u_no = 1").get(0);

		add(n = new JPanel(), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		n.add(hylbl("농산물 거래내역", 0, 30));
		n.add(rad[0]);
		n.add(rad[1]);

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(new JScrollPane(t));
		c.add(lblImg = sz(new JLabel(), 350, 0), "East");
		c.add(cs = new JPanel(new FlowLayout(0)), "South");

		cn.add(lbl("월", 2, 15));
		cn.add(com);

		cs.add(lbl("합계 : ", 2, 15), "West");
		cs.add(txt);

		rad[0].setSelected(true);
		lblImg.setBorder(new LineBorder(Color.black));
		com.setBackground(Color.white);
		t.setBackground(Color.white);

		t.getTableHeader().setBackground(Color.white);
		t.setBackground(Color.white);

		txt.setEnabled(false);

		t.getColumn("bno").setMinWidth(0);
		t.getColumn("bno").setMaxWidth(0);

		data();

		com.addActionListener(e -> data());

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					lblImg.setIcon(getIcon("./datafiles/농산물/" + t.getValueAt(t.getSelectedRow(), 5) + ".jpg",
							lblImg.getWidth(), lblImg.getHeight()));
				}

				if (e.getButton() == 3) {
					var pop = new JPopupMenu();
					var i1 = new JMenuItem("가격 비교");
					var i2 = new JMenuItem("농산물관리");

					pop.add(rad[0].isSelected() ? i1 : i2);
					pop.show(t, e.getX(), e.getY());

					i1.addActionListener(a -> new Chart(toInt(t.getValueAt(t.getSelectedRow(), 5)))
							.addWindowListener(new Before(TradeHistory.this)));
					i2.addActionListener(a -> new BaseMange(toInt(t.getValueAt(t.getSelectedRow(), 5)), true)
							.addWindowListener(new Before(TradeHistory.this)));
				}
			}
		});

		setVisible(true);
	}

	private void data() {
		lblImg.setIcon(null);
		m.setRowCount(0);

		var sql = "select * from base b, farm f, "
				+ (rad[0].isSelected() ? "purchase p where p.f_no = f.f_no" : "sale s where s.f_no = f.f_no")
				+ " and b.b_no = f.b_no and "
				+ (rad[0].isSelected() ? "p.u_no = ? and month(p_date)" + " like ? order by p_date"
						: "s.u_no = ? and month(s_date) like ? order by s_date");
		var rs = getRows(sql, user.get(0), "%" + (com.getSelectedIndex() == 0 ? "" : com.getSelectedIndex()) + "%");
		int tot = 0;

		for (var r : rs) {
			var row = new Object[m.getColumnCount()];

			var idx = new int[] { 11, 2, 14, 15, 9, 0 };
			for (int i = 0; i < row.length; i++) {
				row[i] = i == 4 ? format(toInt(r.get(idx[i]))) : r.get(idx[i]);
			}

			tot += toInt(r.get(9)) * toInt(r.get(15));

			m.addRow(row);
		}

		txt.setText(format(tot));
	}

	public static void main(String[] args) {
		new TradeHistory();
	}
}
