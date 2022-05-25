package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GameList extends BaseFrame {
	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명".split(","));
	JTable t = table(m, "");

	public GameList() {
		super("게임리스트", 500, 500);

		add(lblH("회원명 : " + user.get(3), 0, 0, 35), "North");
		add(new JScrollPane(t));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1) {
					return;
				}

				if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + "").isAfter(LocalDate.now())) {
					eMsg("미래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}
				
				new RoomEscape().addWindowListener(new Before(GameList.this));
			}
		});
		
		var rs = rs("select r_no, r_date, c_name, g_name, t_name from reservation r, genre g, cafe c, theme t where g.g_no = t.g_no and c.c_no = r.c_no and r.t_no= t.t_no and u_no = ? and r_attend = 0", user.get(0));
		for (var r : rs) {
			m.addRow(r.toArray());
		}

		setVisible(true);
	}
	
	public static void main(String[] args) {
		user.add(1);
		user.add("");
		user.add("");
		user.add("dlrlgus");
		new GameList();
	}
}
