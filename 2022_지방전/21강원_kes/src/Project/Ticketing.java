package Project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class Ticketing extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout(10, 10)), set(new EmptyBorder(10, 10, 10, 10)));
	
	JLabel lab1 = get(new JLabel("예매"), set(25));
	
	Vector v1;
	Vector v2 = new Vector<>(Arrays.asList("no, 출발지, 도착지, 출발시간, 도착시간,  ".split(", ")));
	DefaultTableModel model = new DefaultTableModel(v1, v2) {
		public boolean isCellEditable(int row, int column) {
			if (column == 5) {
				return true;
			}else {
				return false;
			}
		};
	};
	JTable tbl = new JTable(model);
	JScrollPane scl = new JScrollPane(tbl);
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	ArrayList<String> data;
	String dep, arr;
	
	int h = 0;
	
	LocalDateTime start, end;
	
	public Ticketing(ArrayList<String> data, String dep, String arr) {
		
		this.data = data;
		this.dep = dep;
		this.arr = arr;
		
		SetDial(this, "버스예매", DISPOSE_ON_CLOSE, 700, 400);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(p1);
		
		p1.add(lab1, "North");
		p1.add(scl);
		
		start = LocalDateTime.of(LocalDate.parse(data.get(3).split(" ")[0]), LocalTime.parse(data.get(3).split(" ")[1]));
		LocalTime time = LocalTime.parse(data.get(4));
		end = start.plusMinutes(time.getMinute()).plusSeconds(time.getSecond());
		h = time.getHour();
		
		model.addRow(new Object[] {"1",dep, arr, start.format(df2), end.format(df2), new JButton("예매")});
		
		tbl.setRowHeight(30);
		tbl.getColumnModel().getColumn(5).setCellEditor(new btn());
		tbl.getColumnModel().getColumn(5).setCellRenderer(new btn());
		
		tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				
				this.setHorizontalAlignment(JLabel.CENTER);
				
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});
		
		tbl.setAutoCreateRowSorter(true);
		tbl.setSelectionMode(0);
		
		UIManager.getLookAndFeelDefaults().put("Table.ascendingSortIcon", sicon("↑"));
		UIManager.getLookAndFeelDefaults().put("Table.descendingSortIcon", sicon("↓"));
		
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
	
	public class btn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
		
		JButton btn;
		
		public btn() {
			
			btn = new JButton("예매");
			
			btn.addActionListener(e->{
				
				String msg = "<html>[이동시간 가격 안내]<br>1시간 이하 100Point,<br>2시간 이하 300Point,<br>3시간 이하 500Point,<br>이외 700Point 차감<br><br>예매를 진행하시겠습니까?";
				
				int a = JOptionPane.showConfirmDialog(null, msg, "안내", JOptionPane.YES_NO_OPTION);
				
				if (a == 0) {
					
					String point = "";
					
					if (h < 1) {
						point = "100";
					}else if (h < 2) {
						point = "300";
					}else if (h < 3) {
						point = "500";
					}else {
						point = "700";
					}
					
					if (start.isBefore(LocalDateTime.now())) {
						new Err("예매할 수 없는 일정입니다.");
					}else {
						
						Query("select count(*) from reservation where schedule_no = ?;", list, data.get(0));
						
						if (intnum(list.get(0).get(0)) >= 25) {
							new Err("해당 일정에 좌석이 모두 매진되었습니다.");
						}else if (intnum(member.get(0).get(5)) < intnum(point)) {
							new Err("포인트가 부족합니다.");
						}else {
							
							new Jop("예매가 완료되었습니다.");
							
							Updat("insert into reservation values(null, ?,?)", member.get(0).get(0), data.get(0));
							Updat("update user set point = point - ? where no = ?;", point, member.get(0).get(0));
							Query("select * from user where no = ?", member, member.get(0).get(0));
							
							dispose();
							
							
						}
						
					}
					
				}
				
			});
			
		}
		
		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			// TODO Auto-generated method stub
			return btn;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// TODO Auto-generated method stub
			return btn;
		}
		
	}
	
}
