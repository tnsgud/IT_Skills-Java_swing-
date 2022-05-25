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
	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명,tno".split(","));
	JTable t = table(m, "");

	public GameList() {
		super("게임리스트", 800, 400);

		setLayout(new BorderLayout(5, 5));

		add(lblH("회원명 : " + rs("select u_name from user where u_no=?", uno).get(0).get(0), 0, 0, 25), "North");
		add(new JScrollPane(t));

		var rs = rs(
				"select r_no, r_date, c_name, g_name, t_name, t_no from reservation r, cafe c, theme t, genre g where r.t_no = t.t_no and t.g_no=g.g_no and c.c_no = r.c_no and r_attend= 0 and u_no=?",
				uno);
		for (var r : rs) {
			m.addRow(r.toArray());
		}

		t.getColumn("tno").setMinWidth(0);
		t.getColumn("tno").setMaxWidth(0);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + "").isAfter(LocalDate.now())) {
					eMsg("미래로 예약된 게임을 실행할 수 없습니다.");
					return;
				}

				rno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				tno = toInt(t.getValueAt(t.getSelectedRow(), 5));
				new RoomEscape().addWindowListener(new Before(GameList.this));
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		uno = 1;
		new GameList();
	}
}
