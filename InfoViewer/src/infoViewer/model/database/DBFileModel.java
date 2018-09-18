package infoViewer.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infoViewer.model.json.JSONParser;
import infoViewer.model.json.JSONToolkit;
import infoViewer.view.AppGUI;
import infoViewer.view.Observer;
import infoViewer.view.database.DBFileView;

public class DBFileModel implements JSONParser {

	private int rows, columns;

	private ArrayList<Observer> observers = new ArrayList();
	private final String name;

	private JSONObject dataJSON;

	public DBFileModel(String name) {

		this.name = name;
	}

	private Connection connection;
	private ArrayList<String> columnNames;
	private Object[][] data;

	public void notifyObservers() {

		for (Observer observer : observers)
			observer.update(null, null);
	}

	public void readHeader() throws SQLException {

		connection = AppGUI.getInstance().getConnection();
		columnNames = new ArrayList();

		ResultSet rsColumnNames = connection.getMetaData().getColumns(null, null, name, null);

		while (rsColumnNames.next())
			columnNames.add(rsColumnNames.getString("COLUMN_NAME"));

		rsColumnNames.close();
	}

	public void readData() throws SQLException {

		try {

			dataJSON = JSONToolkit.getInstance().formDataJSON(this);
			parseJSON(dataJSON);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addRecord(ArrayList<String> inputValues) throws SQLException {

		// 1. FORM THE QUERY
		// 1.1 Columns
		String queryColumns = columnNames.get(0);
		for (int i = 1; i < columnNames.size(); i++)
			queryColumns += "," + columnNames.get(i);
		queryColumns = "(" + queryColumns + ")";
		// 1.2 Values
		String queryValues = "?";
		for (int i = 1; i < columnNames.size(); i++)
			queryValues += ",?";
		queryValues = " VALUES (" + queryValues + ")";
		// 1.3 Query
		String query = "INSERT INTO " + name + queryColumns + queryValues;

		try {

			// 2. PREPARE THE STATEMENT
			// 2.1 prepareStatement(QUERY);
			PreparedStatement statement = connection.prepareStatement(query);
			// 2.2 Insert inputValues
			for (int i = 0; i < inputValues.size(); i++)
				statement.setString(i + 1, inputValues.get(i));
			// 2.3 Execute the statement
			statement.execute();

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "A primary key with this value already exists!", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

		// 3. SET THE SELECTION INTERVAL
		// 3.1 Update the viewer
		notifyObservers();
		// 3.2 Set the selection interval
		int row = findRecordPosition(inputValues);
		if (row != -1) {

			JTable table = ((DBFileView) observers.get(0)).getTable();

			table.getSelectionModel().setSelectionInterval(row, row);
			table.scrollRectToVisible(table.getCellRect(row, 0, false));
		}
	}

	public void updateRecord(ArrayList<String> inputValues, ArrayList<String> oldValues) {

		// 1. FORM THE QUERY
		// 1.1 Columns
		String columns = columnNames.get(0) + " = ?";
		for (int i = 1; i < columnNames.size(); i++)
			columns += "," + columnNames.get(i) + " = ?";
		// 1.2 Query
		String query = "UPDATE " + name + " SET " + columns + " WHERE " + columns.replace(",", " AND ");
		try {

			// 2. PREPARE THE STATEMENT
			// 2.1 prepareStatement(QUERY);
			PreparedStatement statement = connection.prepareStatement(query);
			// 2.2 Insert inputValues
			for (int i = 0; i < inputValues.size(); i++) {

				statement.setString(i + 1, inputValues.get(i));
				statement.setString(i + 1 + inputValues.size(), oldValues.get(i));
			}
			// 2.3 Execute the statement
			statement.execute();

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "UpdateRecord action failed!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		// 3. SET THE SELECTION INTERVAL
		// 3.1 Update the viewer
		notifyObservers();
		// 3.2 Set the selection interval
		int row = findRecordPosition(inputValues);
		if (row != -1) {

			JTable table = ((DBFileView) observers.get(0)).getTable();

			table.getSelectionModel().setSelectionInterval(row, row);
			table.scrollRectToVisible(table.getCellRect(row, 0, false));
		}
	}

	private int findRecordPosition(ArrayList<String> inputValues) {

		for (int i = 0; i < rows; i++) {
			if (((String) data[i][0]).trim().equals(inputValues.get(0).trim()))
				return i;
		}
		return -1;
	}

	public void sortMDI(ArrayList<String> checkedColumns, boolean descending) throws SQLException {

		// 1. FORM THE QUERY
		// 1.1 Columns
		String columns = checkedColumns.get(0);

		if (checkedColumns.size() > 1)
			for (int i = 1; i < checkedColumns.size(); i++)
				columns += ", " + checkedColumns.get(i);

		// 1.2 Query
		String query = "SELECT * FROM " + name + " ORDER BY ";

		if (!descending)
			query += columns;
		else
			query += columns + " DESC";

		try {

			// 2. EXECUTE THE STATEMENT
			// 2.1 Create the statement & the resultSet
			Statement statement = connection.createStatement();
			ResultSet rsData = statement.executeQuery(query);
			// 2.2 Update data[][]
			for (int i = 0; i < rows; i++) {

				rsData.next();

				for (int j = 0; j < this.columns; j++)
					data[i][j] = rsData.getString(j + 1);
			}

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "SortMDI action failed!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		// 3. UPDATE THE VIEWER
		((DBFileView) observers.get(0)).getTable().setModel(new TableModel(data, columnNames.toArray()));
	}

	public void deleteRecord(Object recordValue, int row) throws SQLException {

		try {

			String query = "DELETE FROM " + name + " WHERE ";

			query += columnNames.get(0) + " = ? ";

			PreparedStatement preparedStatement = AppGUI.getInstance().getConnection().prepareStatement(query);
			preparedStatement.setString(1, (String) recordValue);
			preparedStatement.execute();

			notifyObservers();

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "DeleteRecord action failed!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public JSONObject getDataJSON() {

		return dataJSON;
	}

	public String getName() {

		return name;
	}

	public void addObserver(Observer observer) {

		observers.add(observer);
	}

	public ArrayList<String> getColumnNames() {

		return columnNames;
	}

	public Connection getConnection() {

		return connection;
	}

	public Object[][] getData() {

		return data;
	}

	@Override
	public void parseJSON(JSONObject jsonObject) throws JSONException {

		JSONArray dataArray = jsonObject.getJSONArray("ARRAY");

		rows = dataArray.length();
		columns = columnNames.size();

		if (rows == 0)
			JOptionPane.showMessageDialog(null, "This table is empty.");

		data = new Object[rows][columns];

		for (int i = 0; i < rows; i++) {

			JSONObject rowObject = (JSONObject) dataArray.get(i);

			for (int j = 0; j < columns; j++) {

				try {
					data[i][j] = rowObject.getString(columnNames.get(j));
				} catch (Exception e) {
				}
			}
		}
	}
}
