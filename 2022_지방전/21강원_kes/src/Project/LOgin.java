package Project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class LOgin extends JFrame implements Base{

	public static Color back = Color.white;
	public static Color fore = Color.black;
	
	JPanel pn = get(new JPanel(new BorderLayout()));
	
	JPanel p1 = get(new JPanel(new BorderLayout(5,5)), set(new EmptyBorder(100, 40, 30, 30)));
	JPanel p2 = get(new JPanel(new BorderLayout(5,5)));
	JPanel p3 = get(new JPanel(new BorderLayout(5,5)));
	JPanel p4 = get(new JPanel(new GridLayout(2, 1, 0, 10)));
	JPanel p5 = get(new JPanel(new BorderLayout()), set(new EmptyBorder(30, 40, 60, 0)));
	
	JLabel img = Getimg("login.jpg", 500, 400);
	JLabel lab1 = get(new JLabel("로그인"), set(25));
	JLabel lab2=  get(new JLabel("아이디/비밀번호 찾기"), set(12));
	JLabel lab3 = get(new JLabel("새로운 계정 만들기"), set(12));
	
	JButton btn1 = get(new JButton("다음"));
	JButton btn2 = get(new JButton("테마"), setb(Color.DARK_GRAY), setf(Color.white));
	
	JTextField txt1 = gettext("Id");
	JPasswordField txt2 = getpass("Password");
	
	public LOgin() {

		SetFrame(this, "버스예매", EXIT_ON_CLOSE, 800, 400);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(img, "West");
		add(pn);
		
		pn.add(p1);
		pn.add(p5, "South");
		
		p1.add(lab1, "North");
		p1.add(p2);
		p1.add(p3, "South");
		
		p2.add(p4);
		p2.add(btn1, "East");
		
		p3.add(lab2);
		p3.add(btn2, "East");
		
		p4.add(txt1);
		p4.add(txt2);
		
		p5.add(lab3);
		
	}

	@Override
	public void action() {
		
		btn1.addActionListener(e->{
			
			if (txt1.getText().isBlank()) {
				new Err("아이디를 입력해주세요.");
			}else if (txt2.getText().isBlank()) {
				new Err("비밀번호를 입력해주세요");
			}else if (txt1.getText().contentEquals("admin") && txt2.getText().contentEquals("1234")) {
				new Jop("관리자로 로그인합니다.");
				dispose();
				new Admin();
			}else {
				
				Query("select * from user where id = ? and pwd = ?;", member, txt1.getText(), txt2.getText());
				
				if (member.isEmpty()) {
					new Err("ID 또는 PW를 확인해주세요.");
				}else {
					
					dispose();
					new Main();
					
				}
				
			}
			
		});
		
		btn2.addActionListener(e->{
			
			change(btn2);
			tema(pn);
			tema(p1);
			tema(p2);
			tema(p3);
			tema(p4);
			tema(p5);
			
		});
		
		lab2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				new UserFind();
			}
		});
		
		lab3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				new UserInsert();
			}
		});
		
	}
	public static void main(String[] args) {
		new LOgin();
	}
}
