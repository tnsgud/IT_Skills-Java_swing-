package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

public class User extends BasePage {
	DefaultTableModel m = new DefaultTableModel(null, "번호,이름,아이디,비밀번호,전화번호,생일,거주지".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return column != 0 && column != 2;
		};
	};
	JTable table = table(m);
	JTextField txt;

	class Item {
		String key;
		String value;

		public Item(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	JComboBox<Item> editCombo;

	MaskFormatter mask1;
	MaskFormatter mask2;

	JFormattedTextField editField;
	JFormattedTextField editField2;

	public User() {

		try {
			mask1 = new MaskFormatter("###-####-####");
			mask2 = new MaskFormatter("####-##-##");

			mask1.setPlaceholderCharacter('_');
			mask2.setPlaceholderCharacter('_');

			editField = new JFormattedTextField(mask1);
			editField2 = new JFormattedTextField(mask2);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5)), "South");
		for (var rs : getRows(
				"select u.no, u.name,u.id,u.pw, u.phone,u.birth,b.name from user u, building b where u.building = b.no order by u.no")) {
			m.addRow(rs.toArray());
		}
		s.add(btn("수정", a -> {
			for (int i = 0; i < table.getColumnCount(); i++) {
				var name = table.getValueAt(i, 1).toString();
				var id = table.getValueAt(i, 2).toString();
				var pw = table.getValueAt(i, 3).toString();
				var phone = table.getValueAt(i, 4).toString();
				var birth = table.getValueAt(i, 5).toString();

				if (name.isEmpty() || id.isEmpty() || pw.isEmpty()
						|| phone.replace("-", "").replace("_", "").trim().isEmpty()
						|| birth.replace("-", "").replace("_", "").trim().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}

				var simple = new SimpleDateFormat("yyyy-MM-dd");
				simple.setLenient(false);

				try {
					simple.parse(birth);
				} catch (ParseException e1) {
					eMsg("생년월일 포맷이 잘못되었습니다.");
					return;
				}
			}

			for (int i = 0; i < table.getColumnCount(); i++) {
				var name = table.getValueAt(i, 1).toString();
				var id = table.getValueAt(i, 2).toString();
				var pw = table.getValueAt(i, 3).toString();
				var phone = table.getValueAt(i, 4).toString();
				var building = getOne("select no from building where name = ?", table.getValueAt(i, 6).toString());
				var birth = table.getValueAt(i, 5).toString();
				execute("update user set name = ?, id = ?, pw = ?, phone = ?, building =?, birth = ? where no = ?",
						name, id, pw, phone, building, birth, table.getValueAt(i, 0) + "");
			}

			iMsg("수정이 완료되었습니다.");

			for (var rs : getRows(
					"select u.no, u.name,u.id,u.pw, u.phone,u.birth,b.name from user u, building b where u.building = b.no order by u.no")) {
				m.addRow(rs.toArray());
			}
		}));

		s.add(btn("삭제", a -> {
			if (table.getSelectedRow() == -1) {
				eMsg("삭제할 행을 선택해주세요.");
				return;
			}
			execute("delete from user where no = ?", table.getValueAt(table.getSelectedRow(), 0));
			for (var rs : getRows(
					"select u.no, u.name,u.id,u.pw, u.phone,u.birth,b.name from user u, building b where u.building = b.no order by u.no")) {
				m.addRow(rs.toArray());
			}
		}));

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (table.getSelectedRow() == -1)
					return;
				if (table.getSelectedColumn() == 4)
					editField.setText(table.getValueAt(table.getSelectedRow(), 4).toString());

				if (table.getSelectedColumn() == 5)
					editField2.setText(table.getValueAt(table.getSelectedRow(), 5).toString());

				if (table.getSelectedColumn() == 6) {
					for (int i = 0; i < editCombo.getItemCount(); i++) {
						if (editCombo.getItemAt(i).value.equals(table.getValueAt(table.getSelectedRow(), 6)))
							editCombo.setSelectedIndex(i);
					}
				}
			}
		});
		table.setRowHeight(30);
		editCombo = new JComboBox<Item>(getRows("select no, name from building where type = 2").stream()
				.map(a -> new Item(a.get(0) + "", a.get(1) + "")).toArray(Item[]::new));
		table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(editCombo));
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(editField));
		table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(editField2));

		setBorder(new EmptyBorder(10, 10, 10, 10));

	}
}