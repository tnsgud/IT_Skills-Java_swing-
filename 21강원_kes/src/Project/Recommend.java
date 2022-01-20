package Project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Base.Base;
import Joption.Err;

public class Recommend extends JPanel implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout(0, 10)), set(new EmptyBorder(0, 0, 50, 0)));
	JPanel p2 = get(new JPanel(new GridLayout(1, 0, 5, 0)));
	JPanel p3 = get(new JPanel(new BorderLayout(10, 10)), set(0, 300));
	JPanel p4 = get(new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)));
	JPanel p5 = get(new JPanel(new FlowLayout(0)));
	
	JScrollPane scl1 = get(new JScrollPane(p5, 20, 31), set(new LineBorder(Color.white)));
	
	JLabel lab1 = get(new JLabel("추천 어행지 관리"), set(20));
	JLabel lab2 = get(new JLabel("설명 설정"), set(20));
	
	JButton btn1 = get(new JButton("추가"));
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	ArrayList<ArrayList<String>> temp = new ArrayList<>();
	
	JPopupMenu pop1 = new JPopupMenu();
	JPanel in = new JPanel(new GridLayout(0, 1));
	JScrollPane scl2 = new JScrollPane(in , 20 ,31);
	
	JPopupMenu pop2 = new JPopupMenu();
	JMenuItem item1 = new JMenuItem("이미지 설정");
	JMenuItem item2 = new JMenuItem("설명 설정");
	
	JPopupMenu pop3 = new JPopupMenu();
	JMenuItem item3 = new JMenuItem("삭제");
	JMenuItem item4 = new JMenuItem("설명 텍스트 입력");
	
	int index = 0;
	int select = 0;
	String title = "";
	
	public Recommend() {
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(LOgin.back);
		
		design();
		action();
		
	}

	@Override
	public void design() {
		
		pop1.add(scl2);
		pop2.add(item1);
		pop2.add(item2);
		pop3.add(item3);
		pop3.add(item4);
		
		add(p1);
		add(p3, "South");
		
		p1.add(lab1, "North");
		p1.add(p2);
		
		p3.add(p4, "North");
		p3.add(scl1);
		
		p4.add(lab2);
		p4.add(btn1);
		
		Query("SELECT * FROM busticketbooking.location;", location);
		
		for (int i = 0; i < location.size(); i++) {
			
			JButton btn = new JButton(location.get(i).get(1));
			
			int j = i;
			
			btn.addActionListener(e->{
				Updat("update recommend set location_no = ? where no = ?;", location.get(j).get(0), list.get(index).get(4));
				imgs();
			});
			
			in.add(btn);
			
		}
		
		imgs();
		detail();
		
	}
	
	public void imgs() {
		
		p2.removeAll();
		
		Query("select * from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and l.no = r.location_no group by recommend_no order by recommend_no, title;", list);
		
		for (int i = 0; i < list.size(); i++) {
			
			JLabel img = DBimg(list.get(i).get(0), list.get(i).get(1), 135, 130, set(new TitledBorder(new LineBorder(LOgin.fore), list.get(i).get(7), 0, 2, new Font("맑은 고딕", 1, 12), LOgin.fore)));
			
			int j = i;
			
			img.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					
					if (e.getClickCount() == 2) {
						index = j;
						pop1.show(img, e.getX(), e.getY());
					}
					
					if (e.getButton() == 3) {
						index = j;
						pop2.show(img, e.getX(), e.getY());
					}
					
				}
			});
			
			p2.add(img);
			
		}
		
		revalidate();
		repaint();
		
	}
	
	public void detail() {
		
		p5.removeAll();
		
		Query("select * from recommend_info where recommend_no = ? order by title", temp, list.get(select).get(0));
		
		for (int i = 0; i < temp.size(); i++) {
			
			JLabel img = DBimg(temp.get(i).get(0), temp.get(i).get(1), 140, 140, set(new LineBorder(LOgin.fore)));
			
			int j = i;
			
			img.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					
					title = temp.get(j).get(1);
					
					if (e.getButton() == 3) {
						pop3.show(img, e.getX(), e.getY());
					}
					
				}
			});
			
			p5.add(img);
			
		}
		
		p5.setPreferredSize(new Dimension(800, temp.size()/3 * 130 + 125));
		
		revalidate();
		repaint();
		
	}
	
	@Override
	public void action() {
		
		item1.addActionListener(e->{
			
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(","));
			fc.setFileFilter(filter);
			
			int a = fc.showOpenDialog(this);
			
			if (a == 0) {
				
				File file = fc.getSelectedFile();
				
				SaveImg(file.toString(), list.get(index).get(0), list.get(index).get(0));
				imgs();
				detail();
				
			}
			
		});
		
		item2.addActionListener(e->{
			select = index;
			detail();
		});
		
		item3.addActionListener(e->{
			
			Updat("delete from recommend_info where recommend_no = ? and title = ?;", list.get(select).get(0),title);
			imgs();
			detail();	
			
		});
		
		item4.addActionListener(e->{
			
			String a =  JOptionPane.showInputDialog(null, "설명 텍스트를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);
			
			if (a != null && !a.isBlank()) {
				Updat("update recommend_info set descrption = ? where recommend_no = ? and title = ?;", a, list.get(select).get(0), title);
			}
			
		});
		
		btn1.addActionListener(e->{
			
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(","));
			fc.setFileFilter(filter);
			
			int a = fc.showOpenDialog(this);
			
			if (a == 0) {
				
				File file = fc.getSelectedFile();
				String st[] = file.toString().split("\\\\");
				
				Query("select * from recommend_info where title = ?;", temp, st[st.length - 1].replace(".jpg", "").replace(".png", ""));
				
				if (!temp.isEmpty()) {
					new Err("중복된 제목입니다.");
				}else {
					
					Updat("insert into recommend_info values(?,?,?,?);", list.get(select).get(0), st[st.length - 1].replace(".jpg", "").replace(".png", ""), "", "dsfs");
					SaveImg(file.toString(), list.get(select).get(0), st[st.length - 1].replace(".jpg", "").replace(".png", ""));
					
					imgs();
					detail();
					
				}
				
			}
			
		});
		
	}

}
