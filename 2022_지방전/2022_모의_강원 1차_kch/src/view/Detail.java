package view;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Detail extends BaseDialog {

	JPanel contents;
	JScrollPane jsp;

	public Detail(int rno) {
		super("상세설명", 300, 400);
		add(BaseFrame.lbl("상세설명", JLabel.LEFT, 20), "North");
		add(jsp = new JScrollPane(contents = new JPanel(new GridLayout(0,1))));

		try {
			var rs = BaseFrame.stmt
					.executeQuery("SELECT * FROM busticketbooking.recommend_info where recommend_no = " + rno);
			while (rs.next()) {
				try {
					JLabel img = null;
					contents.add(img = new JLabel(
							new ImageIcon(Toolkit.getDefaultToolkit().createImage(rs.getBinaryStream(4).readAllBytes())
									.getScaledInstance(265, 200, Image.SCALE_SMOOTH))));
					if (!rs.getString(3).trim().equals("")) {
						JTextArea area = new JTextArea();
						area.setText(rs.getString(3));
						area.setLineWrap(true);
						contents.add(area);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
