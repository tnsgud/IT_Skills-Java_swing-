package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import tool.Tool;

public class Schedule extends JPanel implements Tool {
	DefaultTableModel m = model("순번,출발지,도착지,출발날짜,이동시간".split(","));
	JTable t = table(m);
	HashSet<Integer> rows = new HashSet<Integer>();
	boolean isChange = false;

	public Schedule() {
		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() != -1 && e.getButton() == 3
						|| !(t.getSelectedColumn() != 1 || t.getSelectedColumn() != 2)) {
					return;
				}
				
				showLocation(((JTable)e.getSource()), 0).show((JTable)e.getSource(), e.getX(), e.getY());
			}
		});
				
		m.addTableModelListener(a->{
			rows.add(t.getSelectedRow());
			isChange = true;
		});
	}

	private void data() {
		m.setRowCount(0);
		var rs = DB.rs(
				"select v1.sno, concat(v1.l11name, ' ', v1.l21name), concat(v1.l12name, ' ', v1.l22name), s.date, time_format(s.elapsed_time, '%H:%i:%s') from v1, schedule s where s.no=v1.sno");
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var s = new JPanel(new FlowLayout(2));

		add(lbl("일정 관리", 2, 35), "North");
		add(new JScrollPane(t));
		add(s, "South");

		for (var cap : "저장,삭제".split(",")) {
			s.add(btn(cap, a -> {
				if (a.getActionCommand().contentEquals("저장")) {
					rows.forEach(row -> {
						var dp = toInt(DB.getOne("select l21no from v1 where l11name=? and l21name=?",
								t.getValueAt(row, 1).toString().split(" ")));
						var ar = toInt(DB.getOne("select l22no from v1 where l12name=? and l22name=?",
								t.getValueAt(row, 1).toString().split(" ")));

						DB.execute("update schedule set departure_location2_no=?, arrival_location2_no=? where no=?",
								dp, ar, toInt(t.getValueAt(row, 0)));

						iMsg("수정내용을 저장 완료하였습니다.");
						DB.createV();

						data();
					});
				} else {
					DB.execute("delete from schedule where no=?", t.getValueAt(t.getSelectedRow(), 0));
					iMsg("삭제를 완료하였습니다.");
					data();
				}
			}));
		}

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
