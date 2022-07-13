package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MovieManageFrame extends BaseFrame {
	public MovieManageFrame() {
		super("관리자", 800, 500);

		add(scroll(c = new JPanel(new BorderLayout())));

		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(cc = new JPanel(new GridLayout(0, 5, 10, 10)));

		cn.add(sz(btnRound("추가 +", a -> {
			new EditMovie(this).setVisible(true);
		}), 120, 30));

		ui();

		opaque(c, false);
		c.setBackground(Color.white);

		setVisible(true);
	}

	void ui() {
		cc.removeAll();

		for (var rs : getRows("select * from movie")) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(new JLabel(getIcon("./지급자료/image/movie/" + rs.get(0) + ".jpg", 120, 150)), "North");
			tmp.add(lbl(rs.get(1).toString(), 0));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new EditMovie(MovieManageFrame.this, rs).setVisible(true);
				}
			});

			cc.add(sz(tmp, 70, 200));
		}

		cc.repaint();
		cc.revalidate();
	}
}
