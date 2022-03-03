package Project;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Base.Base;

public class Admin extends JFrame implements Base{

	JTabbedPane tp = get(new JTabbedPane(JTabbedPane.LEFT));
	
	int index;
	
	public Admin() {
		
		SetFrame(this, "관리자", DISPOSE_ON_CLOSE, 900, 600);
		design();
		action();
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				new LOgin();
			}
		});
		
	}

	@Override
	public void design() {
		
		add(tp);
		
		change();
		
	}
	
	public void change() {
		
		tp.removeAll();
		
		tp.add(new UserManager(), "사용자 관리");
		tp.add(new Recommend(), "추천 여행지 관리");
		tp.add(new Schedule(), "일정 관리");
		tp.add(new ReserveManager(), "예매 관리");
		tp.add(null, "테마");
		tp.add(null, "로그아웃");
		
		tp.setBackgroundAt(4, LOgin.back.equals(Color.white) ? Color.DARK_GRAY : Color.white);
		tp.setForegroundAt(4, LOgin.fore.equals(Color.black) ? Color.white : Color.black); 
		
		for (int i = 0; i < 6; i++) {
			if (i != 4) {
				tp.setBackgroundAt(i, LOgin.back);
				tp.setForegroundAt(i, LOgin.fore);
			}
		}
		
		revalidate();
		repaint();
		
	}
	
	@Override
	public void action() {
		
		tp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
				int r = tp.getSelectedIndex();
				
				if (r == 4) {
					change(tp);
					getContentPane().setBackground(LOgin.back);
					change();
					tp.setSelectedIndex(index);
				}else if (r == 5) {
					dispose();
					new LOgin();
				}
				
			}
		});
		
		tp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				index = tp.getSelectedIndex();
			}
		});
		
	}

}
