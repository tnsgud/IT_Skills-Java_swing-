package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class UserManageFrame extends BaseFrame {
	DefaultTableModel m = model("번호,아이디,비밀번호,이름,생년월일,성별,예매 횟수,등급".split(","));
	JTable t = table(m);
	ArrayList<ArrayList<Object>> rs;
	ArrayList<String> grade = new ArrayList<String>(
			getRows("select gr_name from grade").stream().map(x -> x.get(0).toString()).collect(Collectors.toList()));

	public UserManageFrame() {
		super("관리자", 900, 500);

		add(lbl("회원 관리", 2, 30), "North");
		add(scroll(t));

		setT();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1 || e.getButton() != 3)
					return;

				var pop = new JPopupMenu();
				var i1 = new JMenuItem("승급 ▲");
				var i2 = new JMenuItem("강등 ▼");

				pop.add(i1);
				pop.add(i2);

				i1.addActionListener(a -> {
					var uGrade = t.getValueAt(t.getSelectedRow(), 7).toString();
					var cnt = toInt(t.getValueAt(t.getSelectedRow(), 6));

					if (uGrade.equals("SVIP")) {
						eMsg("최고 등급입니다.");
						return;
					}

					var nextGrade = grade.get(grade.indexOf(uGrade) + 1);
					var min = 5 * (grade.indexOf(uGrade) + 1);

//					일반 -> VIP => 5 <= cnt 
//					VIP -> RVIP => 10 <= cnt
//					RVIP -> VVIP => 15 <= cnt
//					VVIP -> SVIP => 20 <= cnt
//					패턴 = 5 * (현재 등급의 인덱스 + 1) <= cnt
					if (min > cnt) {
						eMsg("등급 기준에 충족되지 않은 회원입니다.");
						return;
					}

					execute("update user set gr_no = ? where u_no = ?", grade.indexOf(nextGrade) + 1,
							t.getValueAt(t.getSelectedRow(), 0));

					setT();
				});
				i2.addActionListener(a -> {
					var uGrade = t.getValueAt(t.getSelectedRow(), 7).toString();

					if (grade.indexOf(uGrade) == 0) {
						eMsg("최하 등급입니다.");
						return;
					}

					execute("update user set gr_no = ? where u_no = ?", grade.indexOf(uGrade),
							t.getValueAt(t.getSelectedRow(), 0));

					setT();
				});

				pop.show(t, e.getX(), e.getY());
			}
		});

		setVisible(true);
	}

	void setT() {
		rs = getRows(
				"select u.u_no, u_id, u_pw, u_name, u_birth, if(u_gender = 1, '남','여'), count(*), gr_name from user u, reservation r, grade gr where u.u_no = r.u_no and u.gr_no = gr.gr_no group by u.u_no");
		addRow(m, rs);
	}

	public static void main(String[] args) {
		new AdminFrame();
	}
}
