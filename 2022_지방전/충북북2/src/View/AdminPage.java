package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class AdminPage extends BasePage {

	JPanel searchP, songP;
	DefaultTableModel search_m = model("번호,이름".split(",")), album_m = model("번호,앨범명,장르,곡수,발매일".split(",")),
			song_m = model("번호,제목,길이,타이틀곡".split(","));
	JTable search_t = table(search_m), album_t = table(album_m), song_t = table(song_m);
	JLabel album_img, album_name;
	JTextField sField, songTitle, songS, songM;

	JRadioButton rb[] = new JRadioButton[2];
	ButtonGroup bg = new ButtonGroup();

	public AdminPage() {
		ui();
		events();
		search();
	}

	void ui() {

		// based
		var n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var c = new JPanel(new GridLayout(1, 0, 5, 5));
		add(n, "North");
		add(c);

		for (var bcap : "통계,로그아웃".split(",")) {
			n.add(btn(bcap, a -> {

			}));
		}

		// searchP
		c.add(searchP = new JPanel(new BorderLayout(5, 5)));

		var search_n = new JPanel(new BorderLayout(5, 5));

		searchP.add(search_n, "North");

		search_n.add(sField = new JTextField(15));
		search_n.add(btn("검색하기", a -> search()), "East");

		searchP.add(new JScrollPane(search_t));

		// album
		c.add(new JScrollPane(album_t));

		// song
		c.add(songP = new JPanel(new BorderLayout(5, 5)));
		var song_n = new JPanel(new BorderLayout());
		var song_c = new JPanel(new BorderLayout(5, 5));
		var song_c_s = new JPanel(new GridLayout(0, 1));

		songP.add(size(song_n, 1, 150), "North");
		songP.add(song_c);
		song_c.add(song_c_s, "South");

		song_n.add(size(album_img = new JLabel(), 150, 150), "West");
		song_n.add(album_name = lbl("", JLabel.LEFT, 0, 20));

		song_c.add(new JScrollPane(song_t));

		String cap[] = "제목,길이,대표 곡".split(",");

		for (int i = 0; i < 3; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp.add(lbl(cap[i], JLabel.LEFT, 0, 10));

			if (i == 0) {
				tmp.add(songTitle = new JTextField(20));
			} else if (i == 1) {
				tmp.add(songM = new JTextField(5));
				tmp.add(lbl("분", JLabel.CENTER, 0, 10));
				tmp.add(songS = new JTextField(5));
				tmp.add(lbl("초", JLabel.CENTER, 0, 10));
			} else {
				var bcap = "에,아니요".split(",");
				for (int j = 0; j < bcap.length; j++) {
					rb[j] = new JRadioButton(bcap[j]);
					bg.add(rb[j]);
					tmp.add(rb[j]);
					rb[j].setOpaque(false);
					rb[j].setForeground(Color.WHITE);
				}
			}
			song_c_s.add(tmp);
			tmp.setOpaque(false);
		}

		songP.add(btn("수정", a -> {
			if (song_t.getSelectedRow() == -1)
				return;

			String s_serial = song_t.getValueAt(song_t.getSelectedRow(), 0).toString();
			String title = songTitle.getText().replaceAll(".*['].*", "\\\\'");
			String time;

			if (s_serial.isEmpty() || title.isEmpty() || songM.getText().equals("") || songS.getText().equals("")) {
				eMsg("수정할 음악을 선택해야 합니다.");
				return;
			}

			try {
				time = LocalTime.of(0, toInt(songM.getText()), toInt(songS.getText())).toString();
			} catch (Exception e) {
				eMsg("시간을 올바르게 입력해야합니다.");
				return;
			}

			int titlesong = rb[0].isSelected() ? 1 : 0;
			try {
				var rs = stmt
						.executeQuery("select s.name from song s, album al where al.serial = s.album and al.serial = '"
								+ album_t.getValueAt(album_t.getSelectedRow(), 0) + "' and s.name = '" + title
								+ "' and s.serial <> " + s_serial + " group by s.serial");
				if (rs.next()) {
					eMsg("같은 앨범에 동일한 제목의 음악이 존재합니다.");
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			execute("update song set name = '" + title + "', length = '" + time + "', titlesong = '" + titlesong
					+ "' where serial = '" + s_serial + "'");

			iMsg("선택한 음악을 수정했습니다.");
			addrow("select serial, name, right(length,5), if(titlesong =1, '예', '아니요') from song where album = '"
					+ album_t.getValueAt(album_t.getSelectedRow(), 0).toString() + "'", song_m);
		}), "South");

		search_t.getColumnModel().getColumn(0).setMinWidth(120);
		search_t.getColumnModel().getColumn(0).setMaxWidth(120);

		song_t.getColumnModel().getColumn(1).setMinWidth(200);
		song_t.getColumnModel().getColumn(1).setMaxWidth(200);

		c.setOpaque(false);
		n.setOpaque(false);

		searchP.setOpaque(false);
		search_n.setOpaque(false);

		songP.setOpaque(false);
		song_n.setOpaque(false);
		song_c.setOpaque(false);
		song_c_s.setOpaque(false);
		album_img.setBorder(new LineBorder(Color.WHITE));
		setBorder(new EmptyBorder(5, 5, 5, 5));

	}

	void events() {
		search_t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (search_t.getSelectedRow() == -1)
					return;

				clear();

				String ar_serial = search_t.getValueAt(search_t.getSelectedRow(), 0).toString();

				addrow("select al.serial, al.name, c.name, count(s.serial), date_format(al.release, '%Y년 %m월 %d일') from album al, song s, category c where al.category = c.serial and al.serial = s.album and al.artist = '"
						+ ar_serial + "' group by al.serial", album_m);

				super.mouseClicked(e);
			}
		});

		album_t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (album_t.getSelectedRow() == -1)
					return;
				String al_serial = album_t.getValueAt(album_t.getSelectedRow(), 0).toString();
				album_img.setIcon(
						new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/images/album/" + al_serial + ".jpg")
								.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
				album_name.setText(album_t.getValueAt(album_t.getSelectedRow(), 1).toString());
				addrow("select serial, name, right(length,5), if(titlesong =1, '예', '아니요') from song where album = '"
						+ al_serial + "'", song_m);
				super.mouseClicked(e);
			}
		});

		song_t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (song_t.getSelectedRow() == -1)
					return;

				String title = song_t.getValueAt(song_t.getSelectedRow(), 1).toString();
				String time = song_t.getValueAt(song_t.getSelectedRow(), 2).toString();
				int idx = (song_t.getValueAt(song_t.getSelectedRow(), 3).equals("아니요")) ? 1 : 0;
				rb[idx].setSelected(true);
				songTitle.setText(title);
				songM.setText(time.split(":")[0]);
				songS.setText(time.split(":")[1]);
				super.mouseClicked(e);
			}
		});
	}

	void search() {
		addrow("select serial, name from artist where artist.name like '%" + sField.getText() + "%'", search_m);
		clear();
	}

	void clear() {
		album_m.setRowCount(0);
		song_m.setRowCount(0);
		album_img.setIcon(null);
		album_name.setText("");
		songTitle.setText("");
		songS.setText("");
		songM.setText("");
		bg.clearSelection();
	}

	public static void main(String[] args) {
		mf.swapPage(new AdminPage());
	}
}
