package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import db.DB;
import model.User;

public class Stage extends BaseFrame {
	int curidx;
	JLabel peo[] = new JLabel[5], prlbl;
	JPanel seats[][] = new JPanel[6][10];
	JButton btn1, btn2;
	ArrayList<Item> items  = new ArrayList<Item>();
	JPanel e_c = new JPanel();
	String t_no;
	
	public Stage() {
		super("좌석", 950, 500);
		totPrice = 0;
		ui();
		
		setVisible(true);
	}
	
	public Stage(String t_no) {
		this();
		this.t_no = t_no;
		btn1.setText("취소하기");
		btn2.setText("수정하기");
		
		try {
			var rs = DB.rs("select * from ticket t inner join perform p on t.p_no=p.p_no where t.t_no=?", toInt(t_no));
			if(rs.next()) {
				for (int i = 0; i < peo.length; i++) {
					peo[i].setEnabled(false);
				}
				for (int i = 0; i < rs.getString(4).split(",").length; i++) {
					peo[i].setText("●");
					curidx = toInt(peo[i].getName());
				}
				
				String seats[] = rs.getString(4).split(",");
				String dc[] = rs.getString(5).trim().split(",");
				for (int i = 0; i < dc.length; i++) {
					var r = Arrays.binarySearch("A,B,C,D,E,F".split(","), seats[i].substring(0, 1));
					var c = toInt(seats[i].substring(1, 3)) - 1;
					this.seats[r][c].setBackground(Color.orange);
					addItem(this.seats[r][c].getName());
					if(toInt(dc[i]) != 0) {
						items.get(items.size()-1).box[toInt(dc[i])-1].setSelected(true);
						items.get(items.size()-1).boxAction();
					}						
					changePrice();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ui() {
		var c = new JPanel(new BorderLayout());
		var c_n = new JPanel(new BorderLayout());
		var c_c = new JPanel(new GridLayout(0, 11, 5, 5));
		
		add(c);
		c.add(c_n, "North");
		c.add(c_c);
		
		c_n.add(lbl("STAGE", 0, 25), "North");
		c_n.add(new JLabel(" ") {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(0, 0, getWidth(), 0);
				super.paint(g);
			}
		});
		c_n.add(lbl("날짜 : "+perform.p_date, JLabel.RIGHT), "South");
		
		for (int i = 0; i < 6; i++) {
			c_c.add(lbl("A,B,C,D,E,F".split(",")[i], 0, 15));
			for (int j = 0; j < seats[i].length; j++) {
				seats[i][j] = new JPanel(new BorderLayout());
				seats[i][j].add(lbl(j+1+"", 0));
				seats[i][j].setName("A,B,C,D,E,F".split(",")[i]+(j+1)+"");
				seats[i][j].setBorder(new LineBorder(Color.black));
				seats[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						var p = ((JPanel)e.getSource());
						
						if(items.size() == 5) {
							eMsg("더 이상은 선책이 불가합니다.");
							return;
						}
						if(p.getBackground().equals(Color.LIGHT_GRAY)) {
							return;
						}else if(p.getBackground().equals(Color.orange)) {
							removeItem(p.getName());
							p.setBackground(null);
						}else {
							addItem(p.getName());
							p.setBackground(Color.orange);
						}
					}
				});
				c_c.add(seats[i][j]);
			}
		}
		
		seatsEnable();
		
		mkEast();
		
		c_c.setBorder(new EmptyBorder(5,5,5,5));
	}

	private void mkEast() {
		var e = new JPanel(new BorderLayout());
		var e_n = new JPanel(new BorderLayout());
		var e_n_s = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var e_s = new JPanel(new BorderLayout());
		var e_s_c = new JPanel(new GridLayout(1, 0, 5, 5));
		
		add(e, "East");
		
		e.add(e_n, "North");
		e.add(sz(e_c, 200, 300) );
		e.add(e_s, "South");
		e_n.add(e_n_s, "South");
		e_s.add(e_s_c);
		
		e_n.add(lbl(perform.p_name, JLabel.LEFT, 25), "North");
		e_n.add(new JLabel(" ") {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(0, 0, getWidth(), 0);
				super.paint(g);
			}
		});
		
		e_n_s.add(lbl("인원수 : ", JLabel.LEFT));
		
		for (int i = 0; i < peo.length; i++) {
			peo[i] = lbl(i == 0 ? "●":"○", 0);
			peo[i].setName(i+1+"");
			peo[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < peo.length; j++) {
						peo[j].setText("○");
					}
					
					curidx = toInt(((JLabel)e.getSource()).getName());
					
					for (int k = 0; k < curidx; k++) {
						peo[k].setText("●");
					}
				}
			});
			e_n_s.add(peo[i]);
		}
		
		e_s.add(prlbl = lbl("총금액: 0", JLabel.LEFT), "North");
		e_s_c.add(btn1 = btn("이전으로", a-> {
			dispose();
		}));
		e_s_c.add(btn2 = btn("다음으로", a->{
			if(items.size() < 1) {
				eMsg("좌석을 선택해주세요.");
				return;
			}
			if(curidx != items.size()) {
				eMsg("인원수에 맞게 좌석을 선택해주세요.");
				return;
			}
			if(a.getActionCommand().equals("다음으로")) {
				new Purchase(Stage.this).addWindowListener(new Before(Stage.this));
			}else {
				ArrayList<String> tmp = new ArrayList<String>();
				items.forEach(t->tmp.add(t.t_seat));
				DB.execute("update ticket set t_seat=? where t_no=?", String.join(",", tmp), t_no);
			}
		}));
		
		e.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		e_c.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.black), "선택한 좌석"), new EmptyBorder(10, 10, 10, 10)));
	}

	private void seatsEnable() {
		try {
			var rs  = DB.rs("select t_seat from ticket where p_no=?", perform.p_no);
			while(rs.next()) {
				for(var s: rs.getString(1).split(",")) {
					int r = Arrays.binarySearch("A,B,C,D,E,F".split(","), s.substring(0, 1));
					int c = toInt(s.substring(1))-1;
					seats[r][c].setBackground(Color.lightGray);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void addItem(String t_seat) {
		e_c.removeAll();
		items.add(new Item(t_seat));
		items.forEach(e-> {
			e_c.add(sz(e, 150, 20));
		});
		
		changePrice();
		
		repaint();
		revalidate();
	}
	
	void changePrice() {
		totPrice = 0;
		items.forEach(e->totPrice+=e.price);
		prlbl.setText("총금액 : " + format.format(totPrice));
	}

	void removeItem(String t_seat) {
		e_c.removeAll();
		for (Item item : items) {
			if(item.t_seat.equals(t_seat)) {
				items.remove(item);
				break;
			}
		}
		items.forEach(e_c::add);
		
		repaint();
		revalidate();
	}
	
	class Item extends JPanel {
		JLabel title, lbl;
		String t_seat;
		int price = toInt(perform.p_price);
		JPanel n, c;
		JCheckBox box[] = {new JCheckBox("청소년 할인 20%"), new JCheckBox("어린이 할인 40%"), new JCheckBox("장애인 할인 50%")};
		ButtonGroup group = new ButtonGroup();
		int selDc = 0;
		
		public Item(String t_seat) {
			this.t_seat = t_seat;
			setLayout(new BorderLayout());
			
			add(n = new JPanel(new BorderLayout()), "North");
			add(c = new JPanel(new GridLayout(0, 1)));
			
			n.add(title = lbl(t_seat+":"+format.format(price), 0));
			n.add(lbl = lbl("▼", 0), "East");
			
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					var l =((JLabel)e.getSource());
					if(l.getText().equals("▼")) {
						l.setText("▲");
						sz(Item.this, 150, 100);
						c.setVisible(true);
					}else {
						l.setText("▼");
						sz(Item.this, 150, 20);
						c.setVisible(false);
					}
					
					repaint();
					revalidate();
				}
			});
			
			for (int i = 0; i < box.length; i++) {
				c.add(box[i]);
				group.add(box[i]);
				box[i].addActionListener(a->boxAction());
			}
			
			c.setVisible(false);
			setBorder(new LineBorder(Color.black));
		}

		private void boxAction() {
			if(box[0].isSelected()) {
				price = (int)(toInt(perform.p_price)*0.8);
				selDc = 1;
			}else if(box[1].isSelected()) {
				price = (int)(toInt(perform.p_price)*0.6);
				selDc = 2;
			}else if(box[2].isSelected()){
				price = (int)(toInt(perform.p_price)*0.5);
				selDc = 3;
			}else {
				price = toInt(perform.p_price);
				selDc = 0;
			}
			
			title.setText(t_seat+":"+format.format(price));
			
			changePrice();
			
			repaint();
			revalidate();
		}
	}
	
	public static void main(String[] args) {
		user = DB.getModel(User.class, "select * from user where u_no=?", 1);
		new Search();
	}
}