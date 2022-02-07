package view;

import java.awt.GridLayout;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import db.DB;
import tool.Tool;

public class Detail extends BaseDialog implements Tool {
	int lno;

	public Detail(int lno) {
		super("", 100, 100);
		this.lno = lno;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(lbl("상세설명", 2, 35), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));

		var rs = DB.rs(
				"select title, img, description from recommend_info ri, recommend r where r.no = ri.recommend_no and r.location_no=? order by title asc",
				lno);
		try {
			while (rs.next()) {
				c.add(new JLabel(img(rs.getBlob(3).getBinaryStream().readAllBytes(), 260, 200)));

				if (rs.getString(1).trim().isEmpty()) {
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
