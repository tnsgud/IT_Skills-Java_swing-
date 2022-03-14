package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;

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
		super("마이페이지", 650, 250);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(lblH("Mypage", 0, 0, 25), "North");
		n.add(lblH("성명 : " + uname, 2, 0, 15));
		n.add(lblH("성별 : " + ugender, 2, 0, 12), "South");

		c.add(lblH("최종학력 : " + ugraduate, 2, 0, 11), "North");
		c.add(new JScrollPane(t));

		s.add(btn("PDF 인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);
		
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		
		data();
		
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() != 3 || t.getSelectedRow() == -1) {
					return;
				}
				
				var pop = new JPopupMenu();
				var item = new JMenuItem("삭제");
				pop.add(item);
				item.addActionListener(a->{
					iMsg("삭제가 완료되었스빈다.");
					execute("delete from applicant where a_no=?", t.getValueAt(t.getSelectedRow(), 8));
					data();
				});
				
				pop.show(t, e.getX(), e.getY());
			}
		});

		setVisible(true);
	}
	
	void data() {
		m.setRowCount(0);
		var rs = rs(
				"select c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from applicant a, employment e, company c where a.e_no= e.e_no and c.c_no =e.c_no and u_no=?",
				uno);
		for (var r : rs) {
			r.add(0, rs.indexOf(r) + 1);
			r.set(5, toInt(r.get(5)) == 3 ? "무관" : graduate[toInt(r.get(5))]);
			r.set(6, gender[toInt(r.get(6)) - 1]);
			r.set(7, toInt(r.get(7)) == 0 ? "심사중" : toInt(r.get(7)) == 1 ? "합격" : "불합격");
			m.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		uno = "1";
		uname = "asd";
		ugender = "qwe";
		ugraduate = "zxc";
		new MyPage();
	}
}
