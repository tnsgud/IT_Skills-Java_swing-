package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

public class PlayHistoryPage extends BasePage {
	DefaultTableModel m = songModel();
	JTable t = songTable(m);

	public PlayHistoryPage() {
		data();
		ui();
	}

	void ui() {
		setLayout(new BorderLayout(5, 5));

		var n = new JPanel(new BorderLayout());
		add(n, "North");
		n.add(lbl("최근 내가 들은 음악", JLabel.LEFT, Font.BOLD, 15));
		n.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new MatteBorder(0, 0, 1, 0, Color.WHITE)));
		add(t);
		n.setOpaque(false);
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void data() {
		addSongRow(
				"SELECT  if(s.titlesong = 1 , 1, 0) isTitle, s.name, if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = "
						+ u_serial
						+ "), true, false) isFavorite ,time_format(s.length, '%i:%S')  ,s.serial FROM song s, history h WHERE s.serial = h.song and h.user = "
						+ u_serial + " group by s.serial",

				m);


	}

	public static void main(String[] args) {
		u_serial = 1;
		mf.swapView(new PlayHistoryPage());
		mf.setVisible(true);
	}

}
