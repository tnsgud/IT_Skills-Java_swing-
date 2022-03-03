package Project;

import Base.Base;
import Joption.Jop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Schedule extends JPanel implements Base{
	
	JPanel p1 = get(new JPanel(new FlowLayout(FlowLayout.RIGHT)));
	
	JButton btn1 = get(new JButton("저장"), set(150, 30));
	JButton btn2 = get(new JButton("삭제"), set(150, 30));
	
	JLabel lab1 = get(new JLabel("일정 관리"), set(20));
	
	Vector v1;
	Vector v2 = new Vector<>(Arrays.asList("순번, 출발지, 도착지, 출발날짜, 이용시간".split(", ")));
	DefaultTableModel model  =new DefaultTableModel(v1, v2) {
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
	
	ArrayList<ArrayList<String>> lo1 = new ArrayList<>();
	ArrayList<ArrayList<String>> lo2 = new ArrayList<>();
	
	JPopupMenu pop = new JPopupMenu();
	
	public Schedule() {

		setLayout(new BorderLayout(10, 10));
		setBackground(LOgin.back);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		design();
		action();
		
	}

	@Override
	public void design() {
		
		add(lab1, "North");
		add(scl);
		add(p1, "South");
		
		p1.add(btn1);
		p1.add(btn2);
		
		table();
		tblcenter(tbl);
		
	}
	
	public void table() {
		
		Query("select s.no, (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.departure_location2_no), (select concat(l.name, ' ', l2.name) from location l, location2 l2 where l.no = l2.location_no and l2.no = s.arrival_locaion2_no), date, elapsed_time from schedule s;", list);
		model.setNumRows(0);
		
		for (int i = 0; i < list.size(); i++) {
			model.addRow(new Object[] {intnum(list.get(i).get(0)), list.get(i).get(1), list.get(i).get(2), list.get(i).get(3), list.get(i).get(4)});
		}
		
		revalidate();
		repaint();
		
	}
	
	@Override
	public void action() {
		
		tbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				if (tbl.getSelectedRow() != -1) {
					if (tbl.getSelectedColumn() == 1 || tbl.getSelectedColumn() == 2) {
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
			int r = 0;
			
			for (int i = 0; i < tbl.getRowCount(); i++) {
				
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).get(0).contentEquals(tbl.getValueAt(i,0).toString())) {
						r = j;
					}
				}	
				
				if (!tbl.getValueAt(i, 1).toString().contentEquals(list.get(r).get(1)) || !tbl.getValueAt(i, 2).toString().contentEquals(list.get(r).get(2))) {
					
					tf = true;

					Query("select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = ? and l2.name = ?;", lo1, tbl.getValueAt(i, 1).toString().split(" ")[0],tbl.getValueAt(i, 1).toString().split(" ")[1] );
					Query("select l2.no from location l, location2 l2 where l.no = l2.location_no and l.name = ? and l2.name = ?;", lo2, tbl.getValueAt(i, 2).toString().split(" ")[0],tbl.getValueAt(i, 2).toString().split(" ")[1] );
					
					Updat("update schedule set departure_location2_no = ?, arrival_locaion2_no = ? where no = ?;", lo1.get(0).get(0), lo2.get(0).get(0), list.get(r).get(0));
					
				}
				
			}
			
			if (tf) {
				new Jop("수정내용을 저장 완료하였습니다.");
			}
			
			table();
			
		});
		
		btn2.addActionListener(e->{
			
			if (tbl.getSelectedRow() != -1) {
				
				Updat("delete from reservation where schedule_no = ?;", tbl.getValueAt(tbl.getSelectedRow(), 0).toString());
				Updat("delete from schedule where no = ?;", tbl.getValueAt(tbl.getSelectedRow(), 0).toString());
				
				table();
				
				new Jop("삭제를 완료하였습니다.");
				
			}
			
		});
		
	}

}
