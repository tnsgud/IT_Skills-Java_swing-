package view;

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
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import db.DB;
import tool.Tool;

public class Stage extends BaseFrame implements Tool {
	int curidx;
	JLabel peo[] = new JLabel[5], prlbl;
	JPanel seats[][] = new JPanel[6][10];
	ArrayList<Item> items = new ArrayList<Item>();
	JButton btn[] = new JButton[2];
	JPanel e_c = new JPanel();
	JPanel root;
	String t_no;

	public Stage() {
		super("좌석", 950, 500);
		totPrice = 0;
		ui();

		setVisible(true);
	}

	public Stage(String t_no) {
		this();
	}

	private void ui() {
		var c_c = new JPanel(new GridLayout(0, 11, 5, 5));

		add(root = new JPanel(new BorderLayout()));

		root.add(sz(n = new JPanel(new BorderLayout()), 10, 80), "North");
		root.add(c = new JPanel(new BorderLayout(10, 10)));

		n.add(lbl("STAGE", 0, 25));

		c.add(lbl("날짜 : " + DB.getOne("select p_date from perform where p_no=?", pno), 4, 15));
		c.add(c_c);

		for (int i = 0; i < 6; i++) {
			c_c.add(lbl("A,B,C,D,E,F".split(",")[i], 0, 15));
			for (int j = 0; j < seats[i].length; j++) {
				c_c.add(seats[i][j] = new JPanel(new BorderLayout()));
				seats[i][j].add(lbl(j + 1 + "", 0));
				seats[i][j].setName("A,B,C,D,E,F".split(",")[i] + (j + 1));
				seats[i][j].setBorder(new LineBorder(Color.black));
				seats[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var p = ((JPanel) e.getSource());

						if (p.getBackground().equals(Color.lightGray)) {
							return;
						} else if (p.getBackground().equals(Color.orange)) {
							removeItem(p.getName());
							p.setBackground(null);
						} else {
							if (items.size() == 5) {
								eMsg("더 이상 선택이 불가능합니다.");
								return;
							}
							
							addItem(p.getName());
							p.setBackground(Color.orange);
						}
					}
				});
			}
		}

		setEnable();

		mkEast();

		n.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		e_c.setBorder(new TitledBorder(new LineBorder(Color.black), "선택한 좌석"));
	}

	private void mkEast() {
		var e_n = new JPanel(new BorderLayout());
		var e_n_s = new JPanel(new FlowLayout(0));
		var e_s = new JPanel(new BorderLayout());
		var e_s_c = new JPanel(new GridLayout(1, 0, 5, 5));

		add(e = new JPanel(new BorderLayout()), "East");
		e.add(e_n, "North");
		e.add(sz(e_c, 200, 300));
		e.add(e_s, "South");
		e_n.add(e_n_s, "South");
		e_s.add(e_s_c);

		e_n.add(lbl(DB.getOne("select p_name from perform where p_no=?", pno), 2, 25), "North");
		e_n.add(new JLabel(" ") {
			@Override
			public void paint(Graphics g) {
				var g2 = (Graphics2D) g;
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(5));
				g2.drawLine(0, 0, getWidth(), 0);
				super.paint(g);
			}
		});

		e_n_s.add(lbl("인원수 : ", 2));

		for (int i = 0; i < peo.length; i++) {
			peo[i] = lbl(i == 0 ? "●" : "○", 0);
			peo[i].setName(i + "");
			peo[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var p : peo) {
						p.setText("○");
					}

					curidx = toInt(((JLabel) e.getSource()).getName());

					for (int j = 0; j <= curidx; j++) {
						peo[j].setText("●");
					}
				}
			});
			e_n_s.add(peo[i]);
		}

		e_s.add(prlbl = lbl("총금액: 0", 2), "North");
		e_s_c.add(btn[0] = btn("이전으로", a -> {
			dispose();
		}));
		e_s_c.add(btn[1] = btn("다음으로", a -> {
			if (items.size() < 1) {
				eMsg("좌석을 선택해주세요.");
				return;
			}

			if (curidx+1 != items.size()) {
				eMsg("인원수에 맞게 좌석을 선택해주세요.");
				return;
			}

			if (a.getActionCommand().equals("다음으로")) {
				new Purchase(items).addWindowListener(new Before(Stage.this));
			} else {
				var tmp = new ArrayList<String>();
				items.forEach(t->tmp.add(t.t_seat));
				DB.execute("update ticket set t_seat=? where t_no=?", String.join(",", tmp), t_no);
			}
		}));

		e.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	private void setEnable() {
		var rs = DB.rs("select t_seat from ticket where p_no=?", pno);
		try {
			while (rs.next()) {
				for (var s : rs.getString(1).split(",")) {
					int r = Arrays.binarySearch("A,B,C,D,E,F".split(","), s.substring(0, 1));
					int c = toInt(s.substring(1)) - 1;
					seats[r][c].setBackground(Color.LIGHT_GRAY);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addItem(String name) {
		e_c.removeAll();
		items.add(new Item(name));
		items.forEach(e->{
			e_c.add(sz(e, 150, 20));
		});
		
		changePrice();
		
		repaint();
		revalidate();
	}

	private void changePrice() {
		totPrice = 0;
		items.forEach(e->totPrice+=e.price);
		prlbl.setText("총금액 : "+format.format(totPrice));
	}

	private void removeItem(String name) {
		e_c.removeAll();
		for (var item : items) {
			if(item.t_seat.equals(name)) {
				items.remove(item);
				break;
			}
		}
		items.forEach(e->{
			e_c.add(sz(e, 150, 20));
		});
		
		changePrice();
		
		repaint();
		revalidate();
	}

	class Item extends JPanel {
		JLabel title, lbl;
		String t_seat;
		int price = toInt(DB.getOne("select p_price from perform where p_no=?", pno));
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
			var price = toInt(DB.getOne("select p_price from perform where p_no=?", pno));
			
			if(box[0].isSelected()) {
				this.price = (int)(price*0.8);
				selDc = 1;
			}else if(box[1].isSelected()) {
				this.price = (int)(price*0.6);
				selDc = 2;
			}else if(box[2].isSelected()){
				this.price = (int)(price*0.5);
				selDc = 3;
			}else {
				this.price = price;
				selDc = 0;
			}
			
			title.setText(t_seat+":"+format.format(price));
			
			changePrice();
			
			repaint();
			revalidate();
		}
	}

	public static void main(String[] args) {
		pno = 1;
		new Stage();
	}
}
