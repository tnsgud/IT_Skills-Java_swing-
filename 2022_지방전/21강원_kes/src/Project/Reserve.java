package Project;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class Reserve extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout(10, 10)), set(new EmptyBorder(10, 10, 10, 10)));
	
	JLabel lab1 = get(new JLabel("예매조회"), set(30));
	
	Vector v1;
	Vector v2 = new Vector<>(Arrays.asList("no, 출발지, 도착지, 도착시간, 출발날짜".split(", ")));
	DefaultTableModel model = new DefaultTableModel(v1, v2) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
		public java.lang.Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return Integer.class;
			}else {
				return String.class;
			}
		};
	};
	JTable tbl = new JTable(model);
	JScrollPane scl = new JScrollPane(tbl);
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item1 = new JMenuItem("취소");
	
	public Reserve() {
		
		SetDial(this, "예매조회", DISPOSE_ON_CLOSE, 600, 500);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		pop.add(item1);
		
		add(p1);
		
		p1.add(lab1, "North");
		p1.add(scl);
		
		tabel();
		tblcenter(tbl);
		
	}
	
	public void tabel() {
		
		model.setNumRows(0);
		Query("select (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.departure_location2_no), (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.arrival_locaion2_no), date, elapsed_time, r.no from reservation r, schedule s where r.schedule_no = s.no and r.user_no = ? order by date", list, member.get(0).get(0));
		
		for (int i = 0; i < list.size(); i++) {
			
			LocalDateTime start = LocalDateTime.of(LocalDate.parse(list.get(i).get(2).split(" ")[0]), LocalTime.parse(list.get(i).get(2).split(" ")[1]));
			LocalTime time = LocalTime.parse(list.get(i).get(3));
			LocalDateTime end = start.plusMinutes(time.getMinute()).plusSeconds(time.getSecond()).plusHours(time.getHour());
			
			model.addRow(new Object[] {i + 1, list.get(i).get(0), list.get(i).get(1), end.format(df2), list.get(i).get(2)});
			
		}
		
		revalidate();
		repaint();
		
	}
	
	@Override
	public void action() {
		
		tbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (tbl.getSelectedRow() != - 1) {
					if (e.getButton() == 3) {
						pop.show(tbl, e.getX(), e.getY());
					}
				}
			}
		});
		
		item1.addActionListener(e->{
			
			int row = intnum(tbl.getValueAt(tbl.getSelectedRow(), 0).toString()) - 1;
			
			LocalDateTime start = LocalDateTime.of(LocalDate.parse(list.get(row).get(2).split(" ")[0]), LocalTime.parse(list.get(row).get(2).split(" ")[1]));
			
			if (start.isBefore(LocalDateTime.now())) {
				new Err("취소 불가능한 일정입니다.");
			}else {
				
				new Jop("예매 취소가 완료되었습니다.");
				String point = "";
				int h = LocalTime.parse(list.get(row).get(3)).getHour();
				
				if (h < 1) {
					point = "100";
				}else if (h < 2) {
					point = "300";
				}else if (h < 3) {
					point = "500";
				}else {
					point = "700";
				}
				
				Updat("delete from reservation where no = ?;", list.get(row).get(4));
				Updat("update user set point = point + ? where no = ?;", point, member.get(0).get(0));
				Query("select * from user where no = ?", member, member.get(0).get(0));
				
				tabel();
				
			}
			
		});
		
	}

}
