package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class EditMovieDialog extends BaseDialog {
	MovieManageFrame frame;
	JLabel lblImg;
	JTextField txt[] = new JTextField[3];
	JRadioButton radio[] = new JRadioButton[3];
	JCheckBox chk[] = getRows("select g_name from genre").stream().map(x -> new JCheckBox(x.get(0).toString()))
			.toArray(JCheckBox[]::new);
	JTextArea area;
	JButton btn1, btn2;
	String path = "", oldPath = "", genre = "", m_open = "";
	ArrayList<Object> rs;

	public EditMovieDialog(MovieManageFrame frame) {
		super("영화추가", 950, 600);

		this.frame = frame;

		ui();
		event();
	}

	public EditMovieDialog(MovieManageFrame frame, ArrayList<Object> rs) {
		this(frame);

		this.rs = rs;

		setTitle("영화수정");

		m_open = rs.get(5).toString();
		
		oldPath = "./지급자료/image/movie/" + rs.get(0) + ".jpg";
		lblImg.setIcon(getIcon(oldPath, 250, 300));

		txt[0].setText(rs.get(1).toString());
		txt[1].setText(rs.get(4).toString());
		txt[2].setText(rs.get(6).toString());

		area.setText(rs.get(2).toString());

		radio[toInt(rs.get(5)) - 1].setSelected(true);

		Stream.of(rs.get(3).toString().split("\\.")).forEach(g -> chk[toInt(g) - 1].doClick());

		btn1.setText("이미지 수정");
		btn2.setText("수정");
	}

	private void event() {
		for (int i = 0; i < radio.length; i++) {
			radio[i].addActionListener(a -> {
				m_open = ((JRadioButton) a.getSource()).getName();
			});
		}

		for (int i = 0; i < chk.length; i++) {
			chk[i].addActionListener(a -> {
				genre = Stream.of(chk).filter(JCheckBox::isSelected).map(x -> x.getName())
						.collect(Collectors.joining("."));
			});
		}
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(w = new JPanel(new BorderLayout(5, 5)), "West");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		w.add(wc = new JPanel(new BorderLayout(5, 5)));
		w.add(ws = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 200, 150), "South");

		wc.add(lblImg = new JLabel());
		wc.add(btn1 = btn("이미지 등록", a -> {
			path = getFilePath();
			lblImg.setIcon(getIcon(path, lblImg.getWidth(), lblImg.getHeight()));
		}), "South");

		var cap = "영화제목,상영시간,감독".split(",");
		var hint = "TItle,Time,Director".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(lbl(cap[i], 2), "North");
			tmp.add(txt[i] = hintField(hint[i], 0));

			ws.add(tmp);
		}

		c.add(cn = new JPanel(new BorderLayout(5, 5)), "North");
		c.add(cc = new JPanel(new BorderLayout(5, 5)));

		{
			var tmp1 = new JPanel(new BorderLayout(5, 5));
			var temp1 = new JPanel(new GridLayout(0, 6));
			var bg = new ButtonGroup();

			tmp1.add(lblB("관람가", 2, 15), "North");
			tmp1.add(temp1);

			for (int j = 0; j < m_age.length - 1; j++) {
				radio[j] = new JRadioButton(m_age[j + 1]);
				radio[j].setName(j + 1 + "");
				bg.add(radio[j]);
				temp1.add(radio[j]);
			}

			var tmp2 = new JPanel(new BorderLayout(5, 5));
			var temp2 = new JPanel(new GridLayout(0, 6));

			tmp2.add(lblB("장르", 2, 15), "North");
			tmp2.add(temp2);

			for (var ch : chk) {
				ch.setName(Arrays.asList(chk).indexOf(ch) + "");
				temp2.add(ch);
			}

			cn.add(tmp1, "North");
			cn.add(tmp2);
		}

		s.add(btn2 = btn("등록", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || area.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					t.requestFocus();
					return;
				}
			}

			if (txt[1].getText().matches(".*[^0-9].*")) {
				eMsg("상영시간은 숫자로 입력해야 합니다.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			if (Stream.of(radio).filter(JRadioButton::isSelected).count() < 1) {
				eMsg("관람 등급을 선택해주세요.");
				return;
			}

			if (Stream.of(chk).filter(JCheckBox::isSelected).count() < 1) {
				eMsg("장르를 선택해주세요.");
				return;
			}

			if (path.isEmpty()) {
				eMsg("이미지를 선택해주세요.");
				return;
			}

			if (a.getActionCommand().equals("등록")) {
				iMsg("등록이 완료되었습니다.");

				execute("insert movie values(0, ?,?,?,?,?,?)", txt[0].getText(), area.getText(), genre,
						txt[1].getText(), m_open, txt[0].getText());
				try {
					Files.copy(
							new File(path).toPath(), new File("./지급자료/image/movie/"
									+ getOne("select m_no from movie order by m_no desc") + ".jpg").toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				iMsg("수정이 완료되었습니다.");
				execute("update movie set m_title = ?, m_synopsis=?, g_no = ?, m_time=?, m_open=?, m_director=? where m_no =?",
						txt[0].getText(), area.getText(), genre, txt[1].getText(), m_open, txt[2].getText(), rs.get(0));
				
				try {
					Files.copy(new File(path).toPath(), new File(oldPath).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			frame.ui();
			dispose();
		}));

		cc.add(lblB("시놉시스", 2, 15), "North");
		cc.add(area = hintArea("Synopsis"));

		area.setLineWrap(true);

		lblImg.setBorder(new LineBorder(Color.lightGray));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		new MovieManageFrame();
	}
}
