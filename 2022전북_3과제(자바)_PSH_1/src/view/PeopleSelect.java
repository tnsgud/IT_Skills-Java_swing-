package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import model.People;

public class PeopleSelect extends BaseFrame {
	JTextField txt;
	JSpinner spin[] = new JSpinner[3];

	public PeopleSelect(JTextField txt) {
		super("탑승객 선택", 300, 200);
		this.txt = txt;

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "성인(12세~),소아(만 2세~ 12세 미만),유아(만 2세 미만)".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(sz(lbl(cap[i], 2, 15), 180, 20));
			tmp.add(sz(spin[i] = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1)), 80, 20));
			spin[i].setName(cap[i].split("\\(")[0]);

			c.add(tmp);
		}

		s.add(btn("확인", a -> {
			int sum = Stream.of(spin).mapToInt(s -> toInt(s.getValue())).sum();

			if (sum == 0) {
				eMsg("탐승객인원을 선택해주세요.");
				return;
			} else if (sum > 5) {
				eMsg("5명 이하로 선택해주세요.");
				return;
			}

			int ans = JOptionPane
					.showConfirmDialog(null,
							String.format("성인 %d명, 소아 %d명, 유아 %d명을 선택하셨습니다.\n예약을 계속 하시겠습니까?", toInt(spin[0].getValue()),
									toInt(spin[1].getValue()), toInt(spin[2].getValue())),
							"안내", JOptionPane.YES_NO_OPTION);
			if (ans == JOptionPane.YES_OPTION) {
				txt.setText(String.join(",",
						Stream.of(spin).map(s -> s.getName() + "-" + s.getValue()).toArray(String[]::new)));

				for (int i = 0; i < toInt(spin[0].getValue()); i++) {
					peoples.add(new People(1, "", ""));
				}

				for (int i = 0; i < toInt(spin[1].getValue()); i++) {
					peoples.add(new People(2, "", ""));
				}

				for (int i = 0; i < toInt(spin[2].getValue()); i++) {
					peoples.add(new People(3, "", ""));
				}

				dispose();
			}
		}));

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if(e.getOppositeWindow() instanceof JDialog) {
					return;
				}
				
				dispose();
			}
		});
	}
}
