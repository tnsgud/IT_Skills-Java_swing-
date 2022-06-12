package view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Building extends BasePage {

	DefaultTableModel m = new DefaultTableModel(null, "이름,설명,시작시간,종료시간,사진".split(",")) {
		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return JComponent.class;
		};
	};
	DefaultTableCellRenderer d = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof JComponent) {
				return (JComponent) value;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
	};
	JTable t = new JTable(m);
	ArrayList<Integer> rows = new ArrayList<>();

	public Building() {
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(4)), "South");

		s.add(btn("저장", a -> {
			for (var row : rows) {
				var data = new ArrayList<>();
				for (int i = 0; i < t.getColumnCount() - 1; i++) {
					data.add(t.getValueAt(row, i));
				}
				data.add(row + 1);
				execute("update building set name=?, info=?,open=?,close=?,img=? where no=?", data.toArray());
			}
		}));

		t.setRowHeight(50);
		t.setDefaultRenderer(JComponent.class, d);
		t.getColumnModel().getColumn(2).setCellEditor(new Spin(2));
		t.getColumnModel().getColumn(3).setCellEditor(new Spin(3));
		t.getColumnModel().getColumn(4).setMinWidth(50);
		t.getColumnModel().getColumn(4).setMaxWidth(50);

		for (var rs : getRows(
				"select name, info, time_format(open, '%H:%i'), time_format(close,'%H:%i'), img from building where type < 2")) {
			rs.set(4, new JLabel(img(rs.get(4), 50, 50)));
			m.addRow(rs.toArray());
		}

		t.getModel().addTableModelListener(e -> {
			if (e.getType() == 0) {
				rows.add(t.getSelectedRow());
			}
		});

		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	class Spin extends DefaultCellEditor {
		LocalTime date = LocalTime.of(4, 30);
		String[] open_times, close_times;
		JSpinner spinner;
		JSpinner.DefaultEditor editor;

		public Spin(int column) {
			super(new JTextField());
			open_times = Stream.generate(() -> {
				date = date.plusMinutes(30);
				return date + "";
			}).limit(10).toArray(String[]::new);
			date = LocalTime.of(19, 30);
			close_times = Stream.generate(() -> {
				date = date.plusMinutes(30);
				return date + "";
			}).limit(8).toArray(String[]::new);
			spinner = new JSpinner(new SpinnerListModel(column == 2 ? open_times : close_times));
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			spinner.setValue(t.getValueAt(row, column));
			return spinner;
		}

		@Override
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
	}
}
