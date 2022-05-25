package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GameList extends BaseFrame {
	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명".split(","));
	JTable t = table(m, "");

	public GameList() {
		super("게임리스트", 800, 400);

		setLayout(new BorderLayout(5, 5));

		add(lblH("회원명 : " + user.get(3), 0, 0, 30), "North");
		add(new JScrollPane(t));

		data();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1) {
					return;
				}

				if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + "").isAfter(LocalDate.now())) {
					eMsg("이래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}

				rno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				new Room().addWindowListener(new Before(GameList.this));
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		var rs = rs(
				"select r_no, r_date, c_name, g_name, t_name from genre g, reservation r, user u, cafe c, theme t where g.g_no=t.g_no and u.u_no = r.u_no and t.t_no = r.t_no and r.c_no = c.c_no and r.r_attend = 0 and u.u_no = ?",
				user.get(0));
		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new GameList();
	}
}
