package view;

import java.awt.*;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AuthenticationPage extends BasePage {

	JComboBox<String> box;
	JTextField txt;

	public AuthenticationPage() {
		super();
		ui();
	}

	void ui() {
		setLayout(new GridBagLayout());
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(lbl("인증할 수단을 선택해 주세요.", JLabel.CENTER, 13), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		cc.add(box = new JComboBox<>());
		cc.add(txt = new JTextField(15));
		c.add(btn("확인", a -> {
			
		}), "East");
		
		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new AuthenticationPage());
		BasePage.mf.addNavigater();
		BasePage.mf.setVisible(true);
	}
}
