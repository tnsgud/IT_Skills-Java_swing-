package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class Reserve extends BaseDialog {
	DefaultTableModel m = new DefaultTableModel(null,
			new String[] { "no", "출발지", "도착지", "출발시간", "도착시간", "", "sno", "sdate", "elpased" });
	JTable t = BaseFrame.table(m);
	JScrollPane jsc = new JScrollPane(t);

	String sql;

	TableButtonRendererEditor tbre = new TableButtonRendererEditor();

	public Reserve(String sql) {
		super("버스예매", 900, 500);
		this.sql = sql;
		ui();
		setData();
	}

	void ui() {
		setLayout(new BorderLayout(5, 5));
		add(BaseFrame.lbl("예매", JLabel.LEFT, 20), "North");
		add(new JScrollPane(t));
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(BaseFrame.dtcr);
		}
		t.getColumnModel().getColumn(5).setCellRenderer(tbre);
		t.getColumnModel().getColumn(5).setCellEditor(tbre);
		t.getColumnModel().getColumn(6).setMinWidth(0);
		t.getColumnModel().getColumn(6).setMaxWidth(0);
		t.getColumnModel().getColumn(7).setMinWidth(0);
		t.getColumnModel().getColumn(7).setMaxWidth(0);
		t.getColumnModel().getColumn(8).setMinWidth(0);
		t.getColumnModel().getColumn(8).setMaxWidth(0);
		t.setRowHeight(25);

		((JPanel) (getContentPane())).setBorder(new EmptyBorder(20, 20, 20, 20));

	}

	void setData() {
		m.setRowCount(0);
		try {
			BaseFrame.dataInit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			var rs = BaseFrame.stmt.executeQuery(sql);
			while (rs.next()) {
				m.addRow(new Object[] { rs.getInt(1),
						BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(3)],
						BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(3)],
						rs.getString(6), rs.getString(7), rs.getString(1), rs.getString(4), rs.getString(5) });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class TableButtonRendererEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

		JButton btn;

		public TableButtonRendererEditor() {
			btn = new JButton("예매");

			btn.addActionListener(a -> {
				var r = t.getSelectedRow();
				LocalDate date = LocalDate.parse(t.getValueAt(r, 7).toString(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				LocalTime time = LocalTime.parse(t.getValueAt(r, 3).toString(),
						DateTimeFormatter.ofPattern("HH:mm:ss"));

				LocalDateTime datetime = LocalDateTime.of(date, time);
				if (datetime.isBefore(LocalDateTime.now())) {
					BaseFrame.eMsg("예매 할 수 없는 일정입니다.");
					return;
				}

				if (BaseFrame.toInt(BaseFrame
						.getOne("select count(*) from schedule where no = '" + t.getValueAt(r, 6) + "'")) >= 25) {
					BaseFrame.eMsg("해당 일정에 좌석이 모두 매진되었습니다.");
					return;
				}

				int point = 700;

				LocalTime elapsed_time = LocalTime.parse(t.getValueAt(r, 8).toString(),
						DateTimeFormatter.ofPattern("HH:mm:ss"));

				if (elapsed_time.getHour() <= 3) {
					point = 500;
				}
				if (elapsed_time.getHour() <= 2) {
					point = 300;
				}
				if (elapsed_time.getHour() <= 1) {
					point = 100;
				}
				if (BaseFrame.toInt(BaseFrame.upoint) < point) {
					BaseFrame.eMsg("포인트가 부족합니다.");
					return;
				}

				BaseFrame.execute("insert into reservation values(0," + BaseFrame.uno + "," + t.getValueAt(r, 6) + ")");
				BaseFrame.iMsg("예매가 완료되었습니다.");

			});
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// TODO Auto-generated method stub
			return btn;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			// TODO Auto-generated method stub
			return btn;
		}

	}

}
