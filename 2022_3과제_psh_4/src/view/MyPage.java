package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.print.PrinterException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = table(m);

	public MyPage() {
		super("Mypage", 750, 500);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(lblH("Mypage", 0, 0, 25), "North");
		n.add(nc = new JPanel(new GridLayout(0, 1, 5, 5)));

		var cap = "성명,성별,최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			nc.add(lblH(
					cap[i] + " : "
							+ (i == 0 ? user.get(1)
									: i == 1 ? gender[toInt(user.get(6)) - 1] : graduate[toInt(user.get(7))]),
					2, 0, 15));
		}

		s.add(btn("PDF 인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		data();

		var col = "번호,기업명,시급,모집정원,최종학력,성별,합격여부".split(",");
		var w = new int[] { 50, 100, 80, 50, 80, 50, 50 };
		for (int i = 0; i < w.length; i++) {
			t.getColumn(col[i]).setMinWidth(w[i]);
			t.getColumn(col[i]).setMaxWidth(w[i]);
		}
		
		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if(e.getButton() == 3 && t.getValueAt(t.getSelectedRow(), 7).toString().equals("불합격")) {
					var pop = new JPopupMenu();
					var item = new JMenuItem("삭제");
					item.addActionListener(a -> {
						execute("delete from applicant where a_no=?", t.getValueAt(t.getSelectedRow(), 8));
						data();
					});
					pop.add(item);
					pop.show(t, e.getX(), e.getY());
				}
			};
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		var rs = rs(
				"select  c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from applicant a, user u, company c, employment e where a.e_no=e.e_no and c.c_no=e.c_no and u.u_no = a.u_no and u.u_no=?",
				user.get(0));
		for (var r : rs) {
			r.add(0, rs.indexOf(r)+1);
			r.set(5, graduate[toInt(r.get(5))]);
			r.set(6, gender[toInt(r.get(6)) - 1]);
			r.set(7, toInt(r.get(7)) == 0 ? "심사중" : toInt(r.get(7)) == 1 ? "합격" : "불학격");
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new MyPage();
	}
}
