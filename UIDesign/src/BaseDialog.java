import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class BaseDialog extends JDialog{
	public BaseDialog(BaseFrame bf, String t, int w, int h) {
		super(bf);
		setTitle(t);
		setSize(w, h);
		setLocationRelativeTo(null);
	}
	
	JLabel lbl(String c, int a) {
		return BaseFrame.lbl(c, a);
	}
	
	JLabel lbl(String c, int a, int sz) {
		return BaseFrame.lbl(c, a, sz);
	}
	
	JLabel lbl(String c, int a, int st, int sz) {
		return BaseFrame.lbl(c, a, st, sz);
	}
	
	JButton btn(String c, ActionListener a) {
		return BaseFrame.btn(c, a);
	}
	
	<T extends JComponent> T sz(T c, int w, int h) {
		return BaseFrame.sz(c, w, h);
	}
	
	void iMsg(String msg) {
		BaseFrame.iMsg(msg);
	}
	
	void eMsg(String msg) {
		BaseFrame.eMsg(msg);
	}
}
