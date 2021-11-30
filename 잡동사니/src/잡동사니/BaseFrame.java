package 잡동사니;

import javax.swing.JFrame;

public abstract class BaseFrame extends JFrame {

	int val(Object o) {
		var s = o.toString().replaceAll("[^0-09]", "");
		return s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	public BaseFrame() {
	}

	abstract void ui();
}
