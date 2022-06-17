
package view;

import java.util.ArrayList;

public class InfoDialog extends BaseDialog {
	ArrayList<Object> list;

	public InfoDialog(ArrayList<Object> list) {
		super(500, 500);
		this.list = list;
	}
}
