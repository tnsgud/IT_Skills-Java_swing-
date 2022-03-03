import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableChart extends BaseFrame{
	
	DefaultTableModel m = new DefaultTableModel(null, "지역,신규 확진자,총 확진자".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	JTable t = new JTable(m);
	JScrollPane scr = new JScrollPane(t);
	
	public TableChart() {
		add(scr);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new TableChart();
	}
}
