package Joption;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import Base.Base;

public class Err extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel());
	
	JLabel lab;
	
	Icon icon = UIManager.getIcon("OptionPane.errorIcon");
	
	JButton btn1 = get(new JButton("확인"));
	
	String txt;
	
	public Err(String txt) {
		
		this.txt = txt;
		
		SetDial(this, "오류", DISPOSE_ON_CLOSE, 350, 150);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(lab = get(new JLabel(txt, icon, JLabel.CENTER), set(12)));
		add(p1, "South");
		
		p1.add(btn1);
		
	}

	@Override
	public void action() {
		
		btn1.addActionListener(e->{
			dispose();
		});
		
	}

}
