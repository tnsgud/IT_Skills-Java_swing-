package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Company extends BaseFrame {
	JLabel img;
	JButton btn;
	JTextField txt[] = new JTextField[5];
	File f;

	public Company() {
		super("기업상세정보", 280, 500);

		var rs = rs("select c_name, c_ceo, c_address, c_category, c_employee from company where c_no=?", cno).get(0);

		setLayout(new BorderLayout(5, 5));

		add(sz(img = new JLabel(), 250, 250), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(btn = btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				if (txt[1].getText().isEmpty() || txt[2].getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}

				iMsg("수정이 완료되었습니다.");
				try {
					execute("update company set c_ceo=?, c_address=?, c_img=? where c_no=?", txt[1].getText(),
							txt[2].getText(), new FileInputStream(f), cno);
					Files.copy(f.toPath(), new File("./datafiles/기업/" + rs.get(0) + "1.jpg").toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}), "South");

		var cap = "기업명,대표자,주소,직종,직원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(10));
			txt[i].setEnabled(false);
			c.add(p);
		}

		for (int i = 0; i < rs.size(); i++) {
			if (i == 3) {
				txt[i].setText(String.join(",", Stream.of(rs.get(i).toString().split(",")).map(c -> category[toInt(c)])
						.toArray(String[]::new)));
			} else {
				txt[i].setText(rs.get(i) + "");
			}
		}

		img.setIcon(img("기업/" + rs.get(0) + "1.jpg", 250, 250));

		img.setBorder(new LineBorder(Color.black));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public Company(String string) {
		this();
		btn.setText(string);
		setTitle("기업정보수정");

		txt[1].setEnabled(true);
		txt[2].setEnabled(true);

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var jfc = new JFileChooser("./Datafiles/기업");
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
				}
			}
		});
	}
}
