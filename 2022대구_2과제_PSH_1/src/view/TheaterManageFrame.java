package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class TheaterManageFrame extends BaseFrame {
	JTabbedPane tab = new JTabbedPane(2);

	public TheaterManageFrame() {
		super("관리자", 700, 300);

		ui();

		setVisible(true);
	}

	void ui() {
		tab.removeAll();

		var rs = getRows("select * from area");
		for (var r : rs) {
			tab.add(new Area(toInt(r.get(0))));
			tab.setTitleAt(rs.indexOf(r), r.get(1).toString());
		}

		add(tab);

		repaint();
		revalidate();
	}

	class Area extends JPanel {
		JPanel c;
		JScrollPane scr;

		public Area(int a_no) {
			super(new BorderLayout());

			add(scr = scroll(c = new JPanel(new GridLayout(0, 5, 0, 30))));

			var rs = getRows("select * from theater where a_no = ?", a_no);
			for (var r : rs) {
				var lbl = lbl(r.get(1).toString(), 2, 13);
				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						new EditTheaterDialog(TheaterManageFrame.this, r).setVisible(true);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						lbl.setForeground(Color.red);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						lbl.setForeground(Color.black);
					}
				});
				c.add(lbl);
			}

			setBackground(Color.white);
			scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
	}
	
	public static void main(String[] args) {
		new TheaterManageFrame();
	}
}
