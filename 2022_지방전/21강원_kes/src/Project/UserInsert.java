package Project;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Base.Base;
import Joption.Err;
import Joption.Jop;

public class UserInsert extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel(new GridLayout(0, 1, 0, 10)), set(new EmptyBorder(40, 30, 30, 30)));
	
	JLabel lab1 = get(new JLabel("게정 정보"), set(25));
	JButton btn1 = get(new JButton("회원가입"));
	
	JTextField txt1 = gettext("Name");
	JTextField txt2 = gettext("Id");
	JPasswordField txt3 = getpass("Password");
	JPasswordField txt4 = getpass("Password 확인");
	JTextField txt5 = gettext("E-mail");
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	public UserInsert() {
		
		SetDial(this, "계정 등록하기", DISPOSE_ON_CLOSE, 350, 400);
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
		p1.add(txt3);
		p1.add(txt4);
		p1.add(txt5);
		p1.add(btn1);
		
	}

	@Override
	public void action() {
		
		btn1.addActionListener(e->{
			
			for (Component c : p1.getComponents()) {
				if (c instanceof JTextField && ((JTextField) c).getText().isBlank()) {
					new Err("공란을 확인해주세요.");
					return;
				}
				if (c instanceof JPasswordField && ((JPasswordField) c).getText().isBlank()) {
					new Err("공란을 확인해주세요.");
					return;
				}
			}
			
			if (!txt3.getText().contentEquals(txt4.getText())) {
				new Err("PW확인이 일치하지 않습니다.");
			}else if (Pattern.matches(".*[~!@#[$]%^&[*]\\(\\)_+<>?\\\\[|]].*", txt3.getText()) == false) {
				new Err("특수문자를 포함해주세요.");
			}else {
				
				Query("select * from user where id = ?;", list, txt2.getText());
				
				if (!list.isEmpty()) {
					new Err("Id가 중복되었습니다.");
					return;
				}
				
				Query("select * from user where email = ?;", list, txt5.getText());
				
				if (!list.isEmpty()) {
					new Err("E-mail이 중복되었습니다.");
					return;
				}
				
				new Jop("회원가입이 완료되었습니다.");
				
				Updat("insert into user values(null, ?,?,?,?,?);", txt2.getText(), txt3.getText(), txt1.getText(), txt5.getText(), "1000");
				
				dispose();
				
			}
			
		});
		
	}

}
