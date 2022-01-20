package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Detail extends BaseDialog {
	public Detail(int rno) {
		super("상세설명", 340, 400);
		add(BaseFrame.lbl("상세설명", JLabel.LEFT, 20), "North");
		add(new JScrollPane((c = new JPanel(new FlowLayout(10, 10, FlowLayout.LEFT)))));
		int h = 0;
		try {
			var rs = BaseFrame.stmt.executeQuery(
					"SELECT description, img FROM recommend_info where recommend_no = " + rno + " order by title asc");
			while (rs.next()) {
				JLabel img;
				c.add(img = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(rs.getBytes(2))
						.getScaledInstance(300, 200, Image.SCALE_SMOOTH))));
				c.add(img);
				h += 205;
				if (!rs.getString(1).trim().isEmpty()) {
					JTextArea area = new JTextArea(rs.getString(1));
					area.setLineWrap(true);
					area.setPreferredSize(new Dimension(300, 150));
					c.add(area);
					h += 155;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.setPreferredSize(new Dimension(300, h));
	}
}
