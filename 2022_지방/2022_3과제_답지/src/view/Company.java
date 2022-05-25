package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Company extends BaseFrame {

	JTextField txt[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
			new JTextField() };

	JLabel img;
	File f;

	JButton btn;
	Admin a;

	public Company(String cno) {
		super("기업상세정보", 300, 500);
		setLayout(new BorderLayout(10, 10));
		add(c = new JPanel(new BorderLayout(10, 10)));

		c.add(img = new JLabel(), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		sz(img, 200, 200);
		img.setBorder(new LineBorder(Color.BLACK));

		var cap = "기업명,대표자,주소,직종,직원수".split(",");
		var rs = getResults("SELECT * from company where c_no = ?", cno);

		for (var r : rs) {
			r.set(4, String.join(",",
					Arrays.stream(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
		}

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(crt_lbl(cap[i], JLabel.LEFT), 60, 0), "West");
			temp.add(txt[i]);
			txt[i].setText(rs.get(0).get(i + 1) + "");
			txt[i].setEnabled(false);
			cc.add(temp);
		}

		img.setIcon(toIcon(rs.get(0).get(6), 280, 200));

		add(btn = crt_evt_btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				if (txt[1].getText().isEmpty() || txt[2].getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}

				setValues("update company set c_ceo = ? , c_address = ?  where c_no = ? ", txt[1].getText(),
						txt[2].getText(), cno);

				if (f != null) {
					try {
						setValues("update company set c_img = ?  where c_no = ? ", new FileInputStream(f), cno);
						Files.copy(f.toPath(), new File("./datafiles/기업/" + txt[0].getText() + "1.jpg").toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				iMsg("수정이 완료되었습니다");
				this.a.load();
				dispose();
			}
		}), "South");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public Company(String cno, Admin a) {
		this(cno);
		this.a = a;
		btn.setText("수정");
		txt[1].setEnabled(true);
		txt[2].setEnabled(true);
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser jfc = new JFileChooser("./datafiles/기업");
				if (jfc.showOpenDialog(Company.this) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					img.setIcon(getIcon(f.getPath(), 280, 200));
				}
				super.mousePressed(e);
			}
		});

	}

}
