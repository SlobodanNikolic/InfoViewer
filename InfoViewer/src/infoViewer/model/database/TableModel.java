package infoViewer.model.database;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {

	public TableModel(Object[][] data, Object[] columnNames) {
		
		try {
			
			setDataVector(data, columnNames);

		} catch (Exception e) {
		}
	}
}
