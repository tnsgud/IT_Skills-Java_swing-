package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MovieEdit extends BaseFrame {
	JScrollPane scr;

	public MovieEdit() {
		super("영화수정", 600, 400);

		add(scr = new JScrollPane(c = new JPanel(new GridLayout(0, 1))));

		setList();

		setVisible(true);
	}

	private void setList() {
		c.removeAll();

		for (var rs : getRows("select * from movie")) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			var img = new JLabel(getIcon("./datafile/영화/" + rs.get(5) + ".jpg", 150, 270));
			var c = new JPanel(new BorderLayout(5, 5));
			var cn = new JPanel(new BorderLayout(5, 5));
			var cnw = new JPanel(new FlowLayout(0, 0, 0));
			var cne = new JPanel(new FlowLayout(0, 5, 0));
			var area = new JTextArea(rs.get(6).toString());
			var scr = new JScrollPane(area);
			var txt = new JTextField(rs.get(5).toString());
			var com = new JComboBox<>(
					getRows("select g_name from genre").stream().map(a -> a.get(0)).toArray(String[]::new));

			tmp.add(img, "West");
			tmp.add(c);

			c.add(cn, "North");
			c.add(scr);

			cn.add(txt, "North");
			cn.add(cnw, "West");
			cn.add(cne, "East");

			cnw.add(com);

			cne.add(btnBlack("수정하기", a -> {
				if (txt.getText().isEmpty() || area.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}

				iMsg("수정이 완료되었습니다.");
				execute("update movie set g_no = ?, m_name = ?, m_content = ? where m_no =?",
						com.getSelectedIndex() + 1, txt.getText(), area.getText(), rs.get(0));
			}));
			cne.add(btn("삭제하기", a -> {
				var ans = JOptionPane.showConfirmDialog(null, "정말 삭제 하시겠습니까?", "경고", JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE);
				if (ans == JOptionPane.YES_OPTION) {
					execute("delete from movie where m_no = ?", rs.get(0));
					setList();
				}
			}));

			c.setBorder(new EmptyBorder(5, 5, 5, 5));
			scr.setBorder(new TitledBorder(new LineBorder(Color.black), "설명", 1, 0, lbl("", 0, 15).getFont()));
			com.setSelectedIndex(toInt(rs.get(1)) - 1);
			area.setLineWrap(true);

			this.c.add(tmp);
		}
		
		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new MovieEdit();
	}
}
