package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("날짜,시간,카페이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m, "");
	JLabel pricelbl;
	JComboBox com;

	public MyPage() {
		super("마이페이지", 800, 400);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(pricelbl = lbl("총 금액", 4, 15), "South");

		n.add(nw = new JPanel(new FlowLayout(2)), "West");
		n.add(btn("삭제", a -> {
			if(t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}
			
			var dateTime = LocalDateTime.of(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0)+""), LocalTime.parse(t.getValueAt(t.getSelectedRow(), 1)+"", DateTimeFormatter.ofPattern("HH:mm")));
			if(LocalDateTime.now().isAfter(dateTime)) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}
			
			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 6));
			data();
		}), "East");
		
		nw.add(lbl("날짜 :", 2));
		nw.add(com = new JComboBox<>());
		com.addItem("전체");
		for (int i = 0; i < 12; i++) {
			com.addItem(i+1+"월");
		}
		com.addActionListener(a->data());
		
		t.getColumn("rno").setMinWidth(0);
		t.getColumn("rno").setMaxWidth(0);
		
		data();
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}
	
	private void data() {
		m.setRowCount(0);
		var rs= rs("select r_date, r_time, c_name, t_name, r_people, format(r_people * c_price, '#,##0'), r_no from reservation r, user u, cafe c, theme t where r.u_no=u.u_no and c.c_no=r.c_no and t.t_no=r.t_no and u.u_no=? "+(com.getSelectedIndex() == 0 ? "":"and month(r_date)="+com.getSelectedIndex()), uno);
		for (var r : rs) {
			m.addRow(r.toArray());
		}		
		if(m.getRowCount() == 0 ) {
			eMsg("예약현황이 없습니다.");
			com.setSelectedIndex(0);
			data();
		}
	}

	public static void main(String[] args) {
		uno = 1;
		new MyPage();
	}
}
