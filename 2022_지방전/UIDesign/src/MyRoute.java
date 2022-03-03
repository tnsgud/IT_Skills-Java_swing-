import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyRoute extends BaseDialog {

	DefaultTableModel m = new DefaultTableModel(null, "시간,위치".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	JTable t = new JTable(m);
	JScrollPane scr = new JScrollPane(t);

	public MyRoute(BaseFrame bf) {
		super(bf, "경로 조회", 550, 450);

		add(scr);
		
		setVisible(true);
	}
}
