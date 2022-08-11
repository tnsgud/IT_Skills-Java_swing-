package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Test extends BaseFrame {
	public Test() {
		super("400", 400, 400);

		var pop = new JPopupMenu();
		var i1 = new JMenuItem("asdlf");
		
		pop.add(i1);

		var l = new JLabel("<html><u>asjdklfjlkfj", getIcon("./datafiles/건물사진/1.jpg", 100, 100), 0);

		l.setComponentPopupMenu(pop);
		
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() != 1) return;
				
				iMsg("asdlkfj");
			}
		});

		add(l);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Test();
	}
}
