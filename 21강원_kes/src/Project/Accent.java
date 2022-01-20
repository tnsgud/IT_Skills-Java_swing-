package Project;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class Accent extends JDialog implements Base{

	JPanel p1 = get(new JPanel(new BorderLayout(10, 10)), set(new EmptyBorder(10, 10, 10, 10)));
	JPanel p2 = get(new JPanel(new BorderLayout(5, 0)));
	JPanel p3 = get(new JPanel(new GridLayout(0, 1, 0, 15)));
	JPanel p4 = get(new JPanel(new GridLayout(0, 1, 0, 15)));
	JPanel p5 = get(new JPanel(new GridLayout(1, 2, 10, 0)));
	
	JLabel lab1 = get(new JLabel("게정"), set(25));
	JLabel lab;
	
	JButton btn1 = get(new JButton("수정"));
	JButton btn2 = get(new JButton("취소"));
	
	String txt[] = "id, pwd, name, email, point".split(", ");
	
	JTextField txt1 = get(new JTextField());
	JTextField txt2 = get(new JTextField());
	JTextField txt3 = get(new JTextField());
	JTextField txt4 = get(new JTextField());
	JTextField txt5 = get(new JTextField());
	
	public Accent() {
		
		SetDial(this, "계정", DISPOSE_ON_CLOSE, 350, 400);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		txt1 = get(new JTextField() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.setColor(LOgin.fore);
				g.drawString(member.get(0).get(1), this.getInsets().left, this.getInsets().top + g.getFontMetrics().getMaxAscent() + 5);
				
			}
		}, set(false));
		txt5 = get(new JTextField() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.setColor(LOgin.fore);
				g.drawString(member.get(0).get(5), this.getInsets().left, this.getInsets().top + g.getFontMetrics().getMaxAscent() + 5);
				
			}
		}, set(false));
		
		add(p1);
		
		p1.add(lab1, "North");
		p1.add(p2);
		p1.add(p5, "South");
		
		p2.add(p3, "West");
		p2.add(p4);
		
		for (int i = 0; i < txt.length; i++) {
			p3.add(lab = get(new JLabel(txt[i])));
		}
		
		p4.add(txt1);
		p4.add(txt2);
		p4.add(txt3);
		p4.add(txt4);
		p4.add(txt5);
		
		p5.add(btn1);
		p5.add(btn2);
		
		txt2.setText(member.get(0).get(2));
		txt3.setText(member.get(0).get(3));
		txt4.setText(member.get(0).get(4));
		
	}

	@Override
	public void action() {
		
		btn1.addActionListener(e->{
			
			if (txt2.getText().isBlank() || txt3.getText().isBlank() || txt4.getText().isBlank()) {
				new Err("공란을 확인해주세요.");
			}else if (Pattern.matches(".*[~!@#[$]%^&[*]\\(\\)_+<>?\\\\[|]].*", txt2.getText()) == false) {
				new Err("특수문자를 포함해주세요.");
			}else {
				
				new Jop("수정되었습니다.");
				
				Updat("update user set pwd = ?, name = ?, email = ? where no = ?;", txt2.getText(), txt3.getText(), txt4.getText(), member.get(0).get(0));
				Query("select * from user where no = ?", member, member.get(0).get(0));
				
			}
			
		});
		
		btn2.addActionListener(e->{
			dispose();
		});
		
	}

}
