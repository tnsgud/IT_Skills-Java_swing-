package Project;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Base.Base;

public class UserReserve extends JFrame implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout()), set(new EmptyBorder(10, 10, 10, 10)));
	JPanel p2 = get(new JPanel());
	
	JLabel lab1 = get(new JLabel("사용자 예매 정보"), set(20));
	
	Vector v1;
	Vector v2 = new Vector<>(Arrays.asList("no, 출발지, 도착지, 도착시간, 출발날짜".split(", "))) ;
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
	JScrollPane scl = get(new JScrollPane(tbl), set(0, 200));
	
	ArrayList<ArrayList<String>> list= new ArrayList<>();
	ArrayList<ArrayList<String>> temp = new ArrayList<>();
	
	String no;
	
	public UserReserve(String no) {

		this.no = no;
		
		SetFrame(this, "예매 정보", DISPOSE_ON_CLOSE, 800, 600);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(p1);
		
		p1.add(lab1, "North");
		p1.add(scl, "South");
		
		Query("select (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.departure_location2_no), (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.arrival_locaion2_no), date, elapsed_time from reservation r, schedule s where r.schedule_no = s.no and r.user_no = ? order by date;", list, no);
		
		for (int i = 0; i < list.size(); i++) {
			
			LocalDateTime start = LocalDateTime.of(LocalDate.parse(list.get(i).get(2).split(" ")[0]), LocalTime.parse(list.get(i).get(2).split(" ")[1]));
			LocalTime time = LocalTime.parse(list.get(i).get(3));
			LocalDateTime end = start.plusMinutes(time.getMinute()).plusSeconds(time.getSecond());
			
			model.addRow(new Object[] {i + 1, list.get(i).get(0), list.get(i).get(1), end.format(df2), list.get(i).get(2)});
			
		}
		tblcenter(tbl);
		
		p1.add(p2 = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
				
				this.setBackground(LOgin.back);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2.setColor(LOgin.fore);
				g2.drawLine(50, 30, 50, 251);
				g2.drawLine(50, 40, 700, 40);
				g2.drawLine(50, 145, 700, 145);
				g2.drawLine(50, 250, 700, 250);
				
				int h[] = {40, 145, 240};
				int num[] = {210, 105, 10};
				
				Query("select month(date), count(*), rank() over(order by count(*) desc) from reservation r, user u, schedule s where r.user_no = u.no and r.schedule_no = s.no and u.no = ? group by month(date) order by month(date) limit 3;", temp, no);
				
				for (int i = 0; i < temp.size(); i++) {
					
					int row = intnum(temp.get(i).get(2)) - 1;
					
					g2.setColor(blue);
					g2.fillRect(165 + (i * 200), h[row], 80, num[row]);
					
					g2.setColor(LOgin.fore);
					g2.fillOval(205 + (i * 200), h[row] - 5, 10, 10);
					
					g2.drawString(temp.get(i).get(0) + "월", 200 + (i * 200), 270);
					g2.drawString(temp.get(i).get(1), 30, 40 + (row * 108));
					
				}
				
				for (int i = 0; i < temp.size() - 1; i++) {
					g2.drawLine(205 + (i * 200), h[ intnum(temp.get(i).get(2)) - 1], 205 + ((i + 1) * 200), h[ intnum(temp.get(i + 1).get(2)) - 1]);
				}
				
			}
		});
		
	}
	@Override
	public void action() {
		
	}

}
