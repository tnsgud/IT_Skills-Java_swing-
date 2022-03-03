package Project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Base.Base;
import Joption.Jop;

public class ReserveManager extends JPanel implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout()));
	JPanel p2 = get(new JPanel(new FlowLayout(0)), set(280, 30));
	JPanel p3 = get(new JPanel(new BorderLayout()), set(0, 200));
	JPanel p4 = get(new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)));
	JPanel p5 = get(new JPanel());
	
	ArrayList<ArrayList<String>> area = new ArrayList<>();
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	ArrayList<ArrayList<String>> lo1 = new ArrayList<>();
	ArrayList<ArrayList<String>> lo2 = new ArrayList<>();
	
	JButton btn1 = get(new JButton("저장"), set(150, 30));
	JButton btn2 = get(new JButton("삭제"), set(150, 30));
	
	Vector v1;
	Vector v2 = new Vector<>(Arrays.asList("순번, 에매자, 출발지, 도착지, 출발날짜, 도착시간".split(", ")));
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
	
	JLabel lab1 = get(new JLabel("예매 관리"), set(20));
	JLabel lab2 = get(new JLabel("<가장 예매가 많은 일정 TOP 6>"), set(12));
	JComboBox com1 = get(new JComboBox<>("2차원 영역형, 방사형".split(", ")));
	
	JPopupMenu pop = new JPopupMenu();
	
	public ReserveManager() {
		
		setLayout(new BorderLayout());
		setBackground(LOgin.back);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		design();
		action();
		
		com1.setSelectedIndex(0);
		
	}

	@Override
	public void design() {
		
		add(p1, "North");
		add(p5);
		add(p3, "South");
		
		p1.add(p2, "West");
		p1.add(lab2);
		
		p2.add(lab1);
		p2.add(com1);
		
		p3.add(scl);
		p3.add(p4, "South");
		
		p4.add(btn1);
		p4.add(btn2);
		
		table();
		tblcenter(tbl);
		
		chart1();
		
	}
	
	public void table() {
		
		Query("select r.no,u.name, (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.departure_location2_no), (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.arrival_locaion2_no), date, elapsed_time, r.no, s.no from reservation r, schedule s, user u where r.schedule_no = s.no and r.user_no = u.no order by r.no;", list);
		model.setNumRows(0);
		
		for (int i = 0; i < list.size(); i++) {
			
			LocalDateTime start = LocalDateTime.of(LocalDate.parse(list.get(i).get(4).split(" ")[0]), LocalTime.parse(list.get(i).get(4).split(" ")[1]));
			LocalTime time = LocalTime.parse(list.get(i).get(5));
			LocalDateTime end = start.plusMinutes(time.getMinute()).plusSeconds(time.getSecond());
			
			model.addRow(new Object[] {intnum(list.get(i).get(0)), list.get(i).get(1), list.get(i).get(2), list.get(i).get(3), list.get(i).get(4), end.format(df2)});
			
		}
		
		revalidate();
		repaint();
		
	}
	
	@Override
	public void action() {
		
		tbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (tbl.getSelectedRow() != -1) {
					if (tbl.getSelectedColumn() == 2 || tbl.getSelectedColumn() == 3) {
						if (e.getButton() == 3) {
							popup(pop, tbl, tbl.getSelectedRow(), tbl.getSelectedColumn());
							pop.show(tbl, e.getX(), e.getY());
						}
					}
				}
			}
		});
		
		btn1.addActionListener(e->{
			
			boolean tf = false;
			int r =0;
			
			for (int i = 0; i < tbl.getRowCount(); i++) {
				
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).get(0).contentEquals(tbl.getValueAt(i, 0).toString())) {
						r = j;
					}
				}
				
				if (!tbl.getValueAt(i, 2).toString().contentEquals(list.get(r).get(2)) || !tbl.getValueAt(i, 3).toString().contentEquals(list.get(r).get(3))) {
					
					tf = true;
					
					Query("select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = ? and l2.name = ?;", lo1, tbl.getValueAt(i, 2).toString().split(" ")[0],tbl.getValueAt(i, 2).toString().split(" ")[1] );
					Query("select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = ? and l2.name = ?;", lo2, tbl.getValueAt(i, 3).toString().split(" ")[0],tbl.getValueAt(i, 3).toString().split(" ")[1] );
					
					Updat("update schedule set departure_location2_no = ?, arrival_locaion2_no = ? where no = ?;", lo1.get(0).get(0), lo2.get(0).get(0), list.get(r).get(7));
					
				}
				
			}
			
			if (tf) {
				new Jop("수정내용을 저장 완료하였습니다.");
			}
			
			table();
			
		});
		
		btn2.addActionListener(e->{
			
			if (tbl.getSelectedRow() != -1) {
				
				Updat("delete from reservation where no = ?;", list.get(tbl.getSelectedRow()).get(0));
				
				new Jop("삭제를 완료하였습니다.");
				table();
				
			}
			
		});
		
		com1.addActionListener(e->{
			if (com1.getSelectedIndex() == 0) {
				chart1();
			}else {
				chart2();
			}
		});
		
	}

	public void chart1() {
		
		p5 = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
				
				this.setBackground(LOgin.back);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2.setColor(LOgin.fore);
				g2.drawLine(50, 20, 50, 251);
				for (int i = 0; i < 5; i++) {
					g2.drawLine(50, 20 + (i * 58), 600, 20 + (i * 58));
				}
				
				Polygon p = new Polygon();
				Query("select *, count(schedule_no) from reservation group by schedule_no order by count(schedule_no) desc, schedule_no limit 6;", area);
				
				int max = intnum(area.get(0).get(3));
				
				for (int i = 0; i < area.size(); i++) {
					
					if (i != 5) {
						g2.drawString((max - i * 2) + "", 30, 30 + (i * 58));
					}
					g2.drawString(area.get(i).get(2), 55 + (i * 108), 270);
					
					if (max >= 18) {
						p.addPoint(50 + (i * 110), 20 + ((max - intnum(area.get(i).get(3))) * 11));
					}else {
						p.addPoint(50 + (i * 110), 20 + ((max - intnum(area.get(i).get(3))) * 29));
					}
					
				}
				
				p.addPoint(600, 252);
				p.addPoint(50, 252);
				g2.setColor(blue);
				g2.fillPolygon(p);
				
			}
		};
		
		add(p5);
		
		revalidate();
		repaint();
		
	}
	
	public void chart2() {
		
		p5 = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
				
				this.setBackground(LOgin.back);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2.setColor(LOgin.fore);
				
				int po[][] = new int[6][2];
				Polygon p[] = {new Polygon(),new Polygon(),new Polygon(),new Polygon(),new Polygon(),new Polygon()};
				
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 6; j++) {
						int x = (int) (380 + (100 - i * 20) * Math.cos(j * Math.PI / 3 + (Math.PI / 6)));
						int y = (int) (150 + (100 - i * 20) * Math.sin(j * Math.PI / 3 + (Math.PI / 6)));
						p[i].addPoint(x, y);
					}
					g2.drawPolygon(p[i]);
				}
				
				for (int i = 0; i < 6; i++) {
					po[i][0] = (int) (375 + 110 * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
					po[i][1] = (int) (150 + 110 * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
				}
				
				Query("select *, count(schedule_no) from reservation group by schedule_no order by count(schedule_no) desc, schedule_no limit 6;", area);
				
				int max = intnum(area.get(0).get(3));
				int num[] = new int[6];
				
				for (int i = 0; i < area.size(); i++) {
					
					String index = max - i * 2 <= 0 ? "" : (max - i * 2) + "";
					
					g2.drawString(index, 370, 55 + (i * 20));
					g2.drawString(area.get(i).get(2), po[i][0], po[i][1]);
					num[i] = intnum(area.get(i).get(3));
					
				}
				
				Polygon poly = new Polygon();
				int x , y;
				
				for (int i = 0; i < num.length; i++) {
					
					if (num[0] >= 18) {
						
						if (i >= 1) {
							x = (int) (380 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.cos(i * Math.PI / (-3) - (Math.PI / (-2))));
							y = (int) (150 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.sin(i * Math.PI / (-3) - (Math.PI / (-2))));
							poly.addPoint(x, y);
						}else {
							x = (int) (380 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
							y = (int) (150 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
							poly.addPoint(x, y);
						}
						
					}else {
						x = (int) (380 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
						y = (int) (150 + (100 - (num[0] - num[i]) / 2.0 * 20) * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
						poly.addPoint(x, y);
					}
					
				}
				
				g2.setColor(blue);
				g2.drawPolygon(poly);
				
			}
		};
		
		add(p5);
		
		revalidate();
		repaint();
		
	}
	
}
