package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Mileage extends BaseFrame {
	DefaultTableModel m = model("no,income,expense,total".split(","));
	JTable t = table(m);
	JLabel tot;

	public Mileage() {
		super("마일리지", 500, 300);

		add(new JScrollPane(t));
		add(tot = lbl("총 마일리지:0", 4, 30), "South");

		addRow();
		
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if (e.getOppositeWindow() instanceof JDialog)
					return;

				dispose();
			}
		});

		setVisible(true);
	}

	private void addRow() {
		m.setRowCount(0);
		int sum = 0;
		var r = getRows(
				"select mi_no, format(mi_income, '#,##0'), format(mi_expense, '#,##0') from mileage where m_no = ?",
				user.get(0));

		for (var rs : r) {
			sum += toInt(rs.get(1)) - toInt(rs.get(2));

			rs.set(0, r.indexOf(rs) + 1);
			rs.set(1, toInt(rs.get(1)) == 0 ? "-" : rs.get(1));
			rs.set(2, toInt(rs.get(2)) == 0 ? "-" : rs.get(2));
			rs.add(sum == 0 ? "-" : format(sum));

			m.addRow(rs.toArray());
		}

		tot.setText("총 마일리지:" + format(sum));
	}
}
