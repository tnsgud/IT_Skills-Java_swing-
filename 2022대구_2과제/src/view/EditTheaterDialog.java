package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class EditTheaterDialog extends BaseDialog {
	TheaterManageFrame frame;
	ArrayList<Object> rs;
	JTextField txt;
	JScrollPane scr;
	JPanel jpMovie[] = new JPanel[toInt(getOne("select count(*) from movie"))];
	String movie = "";
	int selMovie;

	public EditTheaterDialog(TheaterManageFrame frame, ArrayList<Object> rs) {
		super("극장 편집", 550, 500);

		this.frame = frame;
		this.rs = rs;

		ui();
		event();
	}

	private void event() {
		for (var jp : jpMovie) {
			jp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JPanel) e.getSource();

					me.setBorder(me.getBorder() == null ? new LineBorder(red, 4) : null);

					movie = Stream.of(jpMovie).filter(x -> x.getBorder() != null).map(x -> x.getName())
							.collect(Collectors.joining("."));

					selMovie = (int) Stream.of(jpMovie).filter(x -> x.getBorder() != null).count();
				}
			});
		}
	}

	private void ui() {
		add(txt = new JTextField(), "North");
		add(scr = scroll(c = new JPanel(new GridLayout(0, 4))));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var rs = getRows("select * from movie");
		for (var r : rs) {
			var i = rs.indexOf(r);

			jpMovie[i] = new JPanel(new BorderLayout());
			jpMovie[i].add(new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 100, 150)));
			jpMovie[i].add(lbl(r.get(1).toString(), 0, 13), "South");
			jpMovie[i].setName(r.get(0).toString());

			c.add(sz(jpMovie[i], 120, 200));
		}

		s.add(btn("수정", a -> {
			if (txt.getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				txt.requestFocus();
				return;
			}

			if (selMovie != 10) {
				eMsg("상영 영화는 10개여야 합니다.");
				return;
			}

			iMsg("수정이 완료되었습니다.");
			execute("update theater set t_name=?, m_no = ? where t_no = ?", txt.getText(), movie, rs.get(0));
			dispose();
			frame.ui();
		}));

		Stream.of(rs.get(3).toString().split("\\."))
				.forEach(x -> jpMovie[toInt(x) - 1].setBorder(new LineBorder(Color.red)));

		movie = Stream.of(jpMovie).filter(x -> x.getBorder() != null).map(x -> x.getName())
				.collect(Collectors.joining("."));

		selMovie = (int) Stream.of(jpMovie).filter(x -> x.getBorder() != null).count();

		txt.setText(rs.get(1).toString());
		scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
}
