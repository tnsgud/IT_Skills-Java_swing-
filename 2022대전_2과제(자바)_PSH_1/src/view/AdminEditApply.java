package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class AdminEditApply extends BaseFrame {
	JLabel img;
	ButtonGroup bg = new ButtonGroup();
	JRadioButton rad[] = Stream.of("야채,과일".split(",")).map(c -> {
		var r = new JRadioButton(c);
		bg.add(r);
		r.setOpaque(false);
		return r;
	}).toArray(JRadioButton[]::new);
	JTextField txt[] = new JTextField[2];
	JTextArea area = new JTextArea();
	JButton btn, btnSearch;
	File f;
	int bNo;

	public AdminEditApply() {
		super("관리자 수정/등록", 400, 500);

		setLayout(new BorderLayout(5, 5));

		add(img = sz(new JLabel(), 250, 200), "North");
		add(c = new JPanel());
		add(s = new JPanel(), "South");
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		var cap = "구분,품명,온도,설명".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2, 15), 60, 30));

			if (i == 0) {
				for (int j = 0; j < rad.length; j++) {
					tmp.add(rad[j]);
					rad[j].setSelected(j == 0);
				}
			} else {
				tmp.add(i == 3 ? sz(area, 200, 80) : sz(txt[i - 1] = new JTextField(), 200, 30));
			}

			((JComponent) tmp.getComponent(1)).setBorder(new LineBorder(Color.black));

			if (i == 1) {
				tmp.add(btnSearch = btn("검색", a -> {
					if (txt[0].getText().isEmpty()) {
						eMsg("검색할 제품명을 입력하세요.");
						return;
					}

					var me = (JButton) a.getSource();
					if (me.getText().equals("재검색")) {
						Stream.of(rad).forEach(r -> r.setEnabled(true));
						Stream.of(txt).forEach(t -> {
							t.setText("");
							t.setEnabled(true);
						});
						area.setText("");
						img.setIcon(null);
						s.setVisible(false);
						f = null;
						me.setText("검색");
						return;
					} else {
						me.setText("재검색");
					}

					var rs = getRows(
							"select b_temperature, b_note, b_img, b_no from base where division = ? and b_name = ?",
							rad[0].isSelected() ? 1 : 2, txt[0].getText());

					if (rs.isEmpty()) {
						iMsg("등록 가능한 상품입니다.");
						btn.setText("등록");
					} else {
						iMsg("수정 가능한 상품입니다.");
						btn.setText("수정");
						setData(rs.get(0));
					}
				}));
			}

			c.add(tmp);
		}

		s.add(btn = btn("수정", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty() || area.getText().isEmpty() || f == null) {
					eMsg("공백이 존재합니다.");
					return;
				}
			}

			if (txt[1].getText().matches(".*[^0-9].*")) {
				eMsg("온도는 숫자로 입력해주세요.");
				return;
			}

			iMsg(a.getActionCommand() + "이 완료되었습니다.");

			try {
				if (a.getActionCommand().equals("수정")) {

					execute("update base set b_temperrture=?, b_note=?, b_img=? where b_no = ?", txt[1].getText(),
							area.getText(), new FileInputStream(f), bNo);

				} else {
					execute("insert base values(0, ?, ?, ?, ?, ?)", rad[0].isSelected() ? 0 : 1, txt[0].getText(),
							txt[1].getText(), area.getText(), new FileInputStream(f));
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			dispose();
		}));

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser();
					if (jfc.showOpenDialog(null) == 0) {
						f = jfc.getSelectedFile();
						img.setIcon(getIcon(f.getAbsolutePath(), 250, 200));

						repaint();
						revalidate();
					}
				}
			}
		});

		s.setVisible(false);
		img.setBorder(new LineBorder(Color.black));
		area.setLineWrap(true);

		setVisible(true);
	}

	void setData(ArrayList<Object> rs) {
		txt[0].setEnabled(false);
		Stream.of(rad).forEach(r -> r.setEnabled(false));

		txt[1].setText(rs.get(0).toString());
		area.setText(rs.get(1).toString());

		img.setIcon(getIcon(rs.get(2), 250, 200));

		bNo = toInt(rs.get(3));

		s.setVisible(true);

		repaint();
		revalidate();
	}
}
