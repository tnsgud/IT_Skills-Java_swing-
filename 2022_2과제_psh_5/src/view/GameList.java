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
		super("게임리스트", 1000, 500);

		setLayout(new BorderLayout(10, 10));

		add(lblH("회원명 : " + user.get(3), 0, 0, 35), "North");
		add(new JScrollPane(t));

		load();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (LocalDate.now().isBefore(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + ""))) {
					eMsg("미래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}

				rno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				new RoomEscape().addWindowListener(new Before(GameList.this));
			}
		});

		setVisible(true);
	}

	private void load() {
		m.setRowCount(0);
		var rs = rs(
				"select r_no, r_date, c_name, g_name, t_name from reservation r, user u, cafe c, theme t, genre g where g.g_no=t.g_no and r.t_no = t.t_no and c.c_no = c.c_no and u.u_no=r.u_no and r_attend=0 and u.u_no =? group by r_no",
				user.get(0));
		for (var r : rs) {
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new GameList();
	}
}
