package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import db.DB;

public class Reserve extends BaseDialog {
	HashMap<Integer, String> sno;
	DefaultTableModel m = new DefaultTableModel(null, "sno,no,출발지,도착지,출발시작,도착시간, ".split(",")) {
		public boolean isCellEditable(int row, int column) {
			if (column == 6) {
				return true;
			} else {
				return false;
			}
		};
	};
	JTable t = BaseFrame.table(m);

	public Reserve(JFrame jf, HashMap<Integer, String> sno) {
		super(jf, "버스예매", 900, 500);
		this.sno = sno;

		ui();
		data();

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		for (var no : sno.keySet()) {
			try {
				var rs = DB.rs(
						"select time_format(date, '%H:%i:%s'), time_format(addtime(date, elapsed_time), '%H:%i:%s') from schedule where no=?",
						no);
				if (rs.next()) {
					var str = sno.get(no).split("/");
					var row = new Object[] { no, rs.getRow(), str[0], str[1], rs.getString(1), rs.getString(2),
							new JButton("예매") };
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

		add(BaseFrame.lbl("예매", 2, 45), "North");
		add(new JScrollPane(t));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		t.getColumnModel().getColumn(6).setCellEditor(new Btn());
		t.getColumnModel().getColumn(6).setCellRenderer(new Btn());

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}

	public class Btn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
		JButton btn;

		public Btn() {
			btn = new JButton("예매");
			btn.addActionListener(e -> {
				var msg = "<html>[이동시간 각겨 안내]<br>1시간 이하 100Point,<br>2시간 이하 300Point,<br>3시간 이하 500Point,<br>이외 700Point<br><br>예매를 진행하시겠습니까?</html>";

				int a = JOptionPane.showConfirmDialog(null, msg, "안내", JOptionPane.YES_NO_OPTION);
				if (a == JOptionPane.YES_OPTION) {
					var date = LocalDate
							.parse(DB.getOne("select date_format(date, '%Y-%m-%d') from schedule where no=?",
									t.getValueAt(t.getSelectedRow(), 0)));
					if (BaseFrame.now.isBefore(date)) {
						BaseFrame.eMsg("예매할 수 없는 일정입니다.");
						return;
					}

					var cnt = BaseFrame.toInt(DB.getOne("select count(*) from reservation where schedule_no = ?",
							t.getValueAt(t.getSelectedRow(), 0)));
					if (cnt == 25) {
						BaseFrame.eMsg("해당 일정에 좌서이 모두 매진되었습니다.");
						return;
					}

					var time = BaseFrame
							.toInt(DB.getOne("select time_format(elapsed_time, '%H') from schedule where no = 1",
									t.getValueAt(t.getSelectedRow(), 0)));
					var point = 700;
					if (time <= 1) {
						point = 100;
					} else if (time <= 2) {
						point = 300;
					} else if (time <= 3) {
						point = 500;
					}

					if (BaseFrame.toInt(DB.getOne("select point from user where no=?", BaseFrame.no)) < point) {
						BaseFrame.eMsg("포인트가 부족합니다.");
						return;
					}

					BaseFrame.iMsg("예매가 완료되었습니다.");
					DB.execute("insert into reservation values(0, ?, ?)", BaseFrame.no,
							t.getValueAt(t.getSelectedRow(), 0));
					DB.execute("update user set point=point-? where no=?", point, BaseFrame.no);
					dispose();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return btn;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return btn;
		}

	}

	public static void main(String[] args) {
		BaseFrame.no = 1;
		new UserMain();
//		new Reserve(new UserMain(), "서울 중구", "대구 중구", 4);
	}
}
