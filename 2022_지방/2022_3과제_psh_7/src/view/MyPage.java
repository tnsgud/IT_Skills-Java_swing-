package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = table(m);

	public MyPage() {
		super("Mypage", 800, 300);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");


		n.add(lblH("Mypage", 0, 0, 30));
		n.add(ns = new JPanel(new GridLayout(0, 1)), "South");

		var cap = "성명,성별,최종학력".split(",");
		for (int i = 0; i < cap.length; i++) {
			ns.add(lbl(cap[i] + " : "
					+ (i == 0 ? user.get(1) : i == 1 ? gender[toInt(user.get(6)) - 1] : graduate[toInt(user.get(7))]),
					2, 12));
		}

		var c = "번호,기업명,시급,모집정원,최종학력,성별,합격여부,ano".split(",");
		var w = new int[] { 40, 80, 90, 60, 90, 60, 60, 0 };
		for (int i = 0; i < c.length; i++) {
			t.getColumn(c[i]).setMinWidth(w[i]);
			t.getColumn(c[i]).setMaxWidth(w[i]);
		}

		s.add(btn("PDF 인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));
		data();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getValueAt(t.getSelectedRow(), 7).equals("불합격") && e.getButton() == 3) {
					var menu = new JPopupMenu();
					var item = new JMenuItem("삭제");
					menu.add(item);
					item.addActionListener(a -> {
						iMsg("삭제가 완료되었습니다.");
						execute("delete from applicant where a_no=?", t.getValueAt(t.getSelectedRow(), 8));
						data();
					});
					menu.show(t, e.getX(), e.getY());
				}
			}
		});

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		var rs = rs(
				"select c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from company c, employment e, applicant a where a.e_no = e.e_no and c.c_no = e.c_no and a.u_no=?",
				user.get(0));
		for (var r : rs) {
			r.add(0, rs.indexOf(r) + 1);
			r.set(5, graduate[toInt(r.get(5))]);
			r.set(6, gender[toInt(r.get(6)) - 1]);
			r.set(7, toInt(r.get(7)) == 0 ? "심사중" : toInt(r.get(7)) == 1 ? "합격" : "불합격");
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new MyPage();
	}
}
