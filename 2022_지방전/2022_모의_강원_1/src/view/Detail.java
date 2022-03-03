package view;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import db.DB;

public class Detail extends BaseDialog {
	int lNo;

	public Detail(int lNo) {
		super("상세설명", 300, 400);
		this.lNo = lNo;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(BaseFrame.lbl("상세설명", 2, 35), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));

		try {
			var rs = DB.rs(
					"select title, img, descrption from recommend_info ri, recommend r where r.no = ri.recommend_no and r.location_no=? order by title asc",
					lNo);
			while (rs.next()) {
				c.add(new JLabel(new ImageIcon(
						Toolkit.getDefaultToolkit().createImage(rs.getBlob(2).getBinaryStream().readAllBytes())
								.getScaledInstance(260, 200, Image.SCALE_SMOOTH))));

				if (!rs.getString(1).trim().isEmpty()) {
					var area = new JTextArea();
					area.setText(rs.getString(3));
					area.setLineWrap(true);
					area.setEditable(false);
					c.add(area);
				}
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
