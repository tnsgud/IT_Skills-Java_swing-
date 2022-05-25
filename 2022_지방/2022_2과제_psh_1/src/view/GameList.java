package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GameList extends BaseFrame {
	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명".split(","));;
	JTable t = table(m);

	public GameList() {
		super("방탈출 게임", 800, 400);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var date = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + "");
				if (date.isAfter(LocalDate.now())) {
					eMsg("미래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}

				rno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				new RoomEscape().addWindowListener(new Before(GameList.this));
			}
		});
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				data();
			}
		});
	}

	void data() {
		addRow(m,
				"select r_no, r_date, c_name, g_name, t_name from reservation r, theme t, cafe c, genre g where t.t_no = r.t_no and r.c_no = c.c_no and t.g_no = g.g_no and r_attend = 0 and u_no = ? order by r_no asc",
				uno);
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(lbl("회원명 : " + getOne("select u_name from user where u_no=?", uno), 0, 35), "North");
		add(new JScrollPane(t));

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setMaxWidth(i == 2 || i == 4 ? 250 : 100);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		uno = 1;
		new GameList();
	}
}
