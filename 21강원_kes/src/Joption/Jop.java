package Joption;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import Base.Base;

public class Jop extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel());
	
	JLabel lab;
	
	Icon icon = UIManager.getIcon("OptionPane.informationIcon");
	
	JButton btn1 = get(new JButton("확인"));
	
	String txt;
	
	public Jop(String txt) {
		
		this.txt = txt;
		
		SetDial(this, "안내", DISPOSE_ON_CLOSE, 350, 150);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(lab = get(new JLabel(txt, icon, JLabel.CENTER),set(12)));
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
