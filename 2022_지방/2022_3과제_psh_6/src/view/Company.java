package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

public class Company extends BaseFrame {
	JLabel img;
	JTextField txt[] = new JTextField[5];
	JButton btn;
	File f = new File("./datafiles/기업/" + cno + "1.jpg");

	public Company() {
		super("기업정보상세", 300, 450);

		var info = rs("select c_name, c_ceo, c_address, c_category, c_employee from company where c_no=?", cno).get(0);

		setLayout(new BorderLayout(5, 5));

		add(sz(img = new JLabel(img("기업/" + info.get(0) + "1.jpg", 300, 200)), 300, 200), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		add(btn = btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				iMsg("수정이 완료되었습니다.");
				try {
					execute("update company set c_img=?, c_ceo=?, c_address=?", new FileInputStream(f),
							txt[1].getText(), txt[2].getText());
					Files.copy(f.toPath(), new File("./datafiles/기업/" + cno + "1.jpg").toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				dispose();
			}
		}), "South");

		var cap = "기업명,대표자,주소,직종,직원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(cap[i], 2, 12), 60, 20), "West");
			p.add(txt[i] = new JTextField(15));
			txt[i].setEnabled(false);
			txt[i].setText(i == 3 ? String.join(",",
					Stream.of(info.get(i).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new))
					: info.get(i) + "");
			c.add(p);
		}

		img.setBorder(new LineBorder(Color.black));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public Company(String string) {
		this();

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("./datafiles/회원사진");
					jfc.setFileFilter(new FileFilter() {
						@Override
						public String getDescription() {
							return "JPG Images";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith("jpg");
						}
					});
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						f = jfc.getSelectedFile();
						img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath())
								.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
					}
				}
			}
		});
		txt[3].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new JobSelection(txt[3]).addWindowListener(new Before(Company.this));
			}
		});
		for (int i = 0; i < txt.length; i++) {
			txt[i].setEnabled(i == 1 || i == 2);
		}

		btn.setText(string);
	}
}
