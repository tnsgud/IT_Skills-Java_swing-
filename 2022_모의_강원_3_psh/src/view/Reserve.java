package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class Reserve extends BaseDialog {
	ArrayList<Integer> sno;
	DefaultTableModel m = new DefaultTableModel(null, "sno,no,출발지,도착지,출발시간,도착시간, ".split(",")) {
		public boolean isCellEditable(int row, int column) { 
			return column == 6;
		};
	};
	JTable t = table(m);

	public Reserve(ArrayList<Integer> sno) {
		super("버스예매", 900, 500);
		this.sno = sno;

		ui();
		data();

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		for (var no : sno) {
			var rs = rs(
					"select concat(l11name, ' ', l12name), concat(l21name, ' ', l22name), time_format(s.date, '%H:%i:%s'), time_format(addtime(s.date, s.elapsed_time), '%H:%i:%s') from v1, schedule s where s.no= v1.sno and no=?",
					no);
			try {
				if (rs.next()) {
					var row = new Object[] { no, m.getRowCount() + 1, rs.getString(1), rs.getString(2), rs.getString(3),
							rs.getString(4), new JButton("예매") };
					m.addRow(row);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		add(lbl("예매", 2, 45), "North");
		add(new JScrollPane(t));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		t.getColumnModel().getColumn(6).setCellEditor(new Btn());
		t.getColumnModel().getColumn(6).setCellRenderer(new Btn());

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	class Btn extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
		JButton btn;

		public Btn() {
			btn = btn("예매", a -> {
				var msg = "<html>[이동시간 가격 안내]<br/>1시간 이하 100Point,<br/>2시간 이하 300Point,<br/>3시간 이하 500Point,<br/>이외 700Point차감<br/><br/>예매를 진행하시겠습니까?</html>";

				var an = JOptionPane.showConfirmDialog(null, msg, "안내", JOptionPane.YES_NO_OPTION);
				if (an == JOptionPane.YES_OPTION) {
					var date = LocalDate.parse(getOne("select date_format(date, '%y-%m-%d') from schedule where no=?",
							t.getValueAt(t.getSelectedRow(), 0)));

					if (BaseFrame.now.isBefore(date)) {
						eMsg("예매할 수 없는 일정입니다.");
						return;
					}

					var cnt = toInt(getOne("select count(*) from reservation where schedule_no=?",
							t.getValueAt(t.getSelectedRow(), 0)));
					if (cnt == 25) {
						eMsg("해당 일정에 좌석이 모두 매진되었습니다.");
						return;
					}

					var time = toInt(getOne("select time_format(elapsed_time, '%H') from schedule where no=?",
							t.getValueAt(t.getSelectedRow(), 0)));
					var point = 100;

					if (time <= 2) {
						point = 300;
					} else if (time <= 3) {
						point = 500;
					} else if (time > 3) {
						point = 700;
					}

					if (toInt(getOne("select point from user where no=?", BaseFrame.uno)) < point) {
						eMsg("포인트가 부족합니다.");
						return;
					}

					iMsg("예매가 완료되었습니다.");
					execute("insert into reservation values(0, ?, ?)", BaseFrame.uno,
							t.getValueAt(t.getSelectedRow(), 0));
					execute("update user set point=point-? where no=?", point, BaseFrame.uno);
					dispose();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return btn;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return btn;
		}
	}

	public static void main(String[] args) {
		BaseFrame.uno = 1;
		new UserMain();
	}
}
