package Project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Base.Base;

public class Detail extends JDialog implements Base{
	
	JPanel p1 = get(new JPanel(new BorderLayout(10, 10)), set(new EmptyBorder(10, 10, 10, 10)));
	JPanel p2 = get(new JPanel(new FlowLayout(0, 0, 2)));
	
	JScrollPane scl = new JScrollPane(p2, 20, 31);
	
	JLabel lab1 = get(new JLabel("상세설명"), set(20));
	
	ArrayList<ArrayList<String>> list = new ArrayList<>();
	
	String no;
	
	public Detail(String no) {

		this.no = no;
		
		SetDial(this, "상세설명", DISPOSE_ON_CLOSE, 400, 500);
		design();
		action();
		setVisible(true);
		
	}

	@Override
	public void design() {
		
		add(p1);
		
		p1.add(lab1, "North");
		p1.add(scl);
		
		Query("select * from recommend_info where recommend_no = ? order by title;", list, no);
		
		int height = 0;
		for (int i = 0; i < list.size(); i++) {
			
			JLabel img = DBimg(list.get(i).get(0), list.get(i).get(1), 360, 120);
			p2.add(img);
			height += 120;
			
			if (!list.get(i).get(2).contentEquals("")) {
				JTextArea txt = get(new JTextArea(), set(new LineBorder(Color.gray)));
				txt.setText(list.get(i).get(2));
				txt.setLineWrap(true);
				JScrollPane tscl = get(new JScrollPane(txt, 20, 31), set(350, 120));
				p2.add(tscl);
				height += 120;
			}
			
		}

		p2.setPreferredSize(new Dimension(380, height + 30));
		
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}

}
