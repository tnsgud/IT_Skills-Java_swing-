package Project;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class UserFind extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel(new GridLayout(0, 1, 0, 10)), set(new EmptyBorder(40, 30, 40, 30)));
	
	JButton btn1 = get(new JButton("계속"));
	JButton btn2 = get(new JButton("계속"));
	
	JLabel lab1 = get(new JLabel("아이디 찾기"), set(30));
	JLabel lab2 = get(new JLabel("비밀번호 찾기"), set(30));
	
	JTextField txt1 = gettext("Name");
	JTextField txt2 = gettext("E-mail");
	JTextField txt3 = gettext("Name");
	JTextField txt4 = gettext("Id");
	JTextField txt5 = gettext("E-mail");
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	public UserFind() {

		SetDial(this, "아이디/비밀번호 찾기", DISPOSE_ON_CLOSE, 400, 500);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(p1);
		
		p1.add(lab1);
		p1.add(txt1);
		p1.add(txt2);
		p1.add(btn1);
		
		p1.add(lab2);
		p1.add(txt3);
		p1.add(txt4);
		p1.add(txt5);
		p1.add(btn2);
		
	}

	@Override
	public void action() {
		
		btn1.addActionListener(e->{
			
			if (txt1.getText().isBlank() || txt2.getText().isBlank()) {
				new Err("공란을 확인해주세요.");
			}else {
				
				Query("select * from user where name = ? and email = ?;", list, txt1.getText(), txt2.getText());
				
				if (list.isEmpty()) {
					new Err("존재하지 않는 정보입니다.");
				}else {
					new Jop("귀하의 Id는 " + list.get(0).get(1) + "입니다.");
				}
				
			}
			
		});
		
		btn2.addActionListener(e->{
			
			if (txt3.getText().isBlank() || txt4.getText().isBlank() || txt5.getText().isBlank()) {
				new Err("공란을 확인해주세요.");
			}else {
				
				Query("select * from user where name = ? and id = ? and email = ?;", list, txt3.getText(), txt4.getText(), txt5.getText());
				
				if (list.isEmpty()) {
					new Err("존재하지 않는 정보입니다.");
				}else {
					new Jop("귀하의 Id에 PW는 " + list.get(0).get(2) + "입니다.");
				}
				
			}
			
		});
		
	}

}
