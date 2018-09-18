package infoViewer.model.json;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infoViewer.model.database.DBFileModel;
import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;

public class JSONToolkit {

	public static final int DATABASE_NAME = 0;
	public static final int TABLE_NAME = 3;
	public static final int COLUMN_NAME = 4;
	public static final int DATA_TYPE = 6;
	public static final int FIELD_SIZE = 7;
	public static final int NULLABLE = 11;

	private static JSONToolkit instance;

	private ResultSet rsPrimaryKeys, rsForeignKeys;

	private ArrayList<String> bothKeys, primaryKeys, foreignKeys;

	private JSONToolkit() {

	}

	public JSONObject formDBJSON(DatabaseMetaData dbMetaData) throws SQLException, JSONException {

		// 1. Napravim JSON za bazu
		JSONObject databaseObject = new JSONObject();
		databaseObject.put("TYPE", DBNode.DATABASE);
		// 1.1 Ovdje ce da idu tableObjekti
		JSONArray tableArray = new JSONArray();

		ResultSet tables = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });

		String tableName = "";

		bothKeys = new ArrayList();
		primaryKeys = new ArrayList();
		foreignKeys = new ArrayList();

		while (tables.next()) {

			tableName = tables.getString(TABLE_NAME);
			if (!tableName.contains("trace")) {
				// 2. Napravim JSON objekat za trenutnu tabelu
				JSONObject tableObject = new JSONObject();
				tableObject.put("NAME", tableName);
				tableObject.put("TYPE", DBNode.TABLE);
				// 2.1 Ovdje ce da idu columnObjekti
				JSONArray columnArray = new JSONArray();

				ResultSet columns = dbMetaData.getColumns(null, null, tableName, null);

				formPrimaryKeys(tableName);
				formForeignKeys(tableName);

				while (columns.next()) {

					// 3. Napravim JSON objekat za kolonu
					JSONObject columnObject = new JSONObject();

					String columnName = columns.getString(COLUMN_NAME);
					String dataType = columns.getString(DATA_TYPE);
					boolean isNull;
					if (columns.getInt(NULLABLE) == 0)
						isNull = false;
					else
						isNull = true;
					int fieldSize = columns.getInt(FIELD_SIZE);

					columnObject.put("NAME", columnName);
					columnObject.put("DATA_TYPE", dataType);
					columnObject.put("NULLABLE", isNull);
					columnObject.put("FIELD_SIZE", fieldSize);

					checkColumnType(columnName, columnObject);
					// 3.1 Stavim columnObjekat u listu kolona
					columnArray.put(columnObject);
				}
				// 2.2 Listu kolona stavim u tableObjekat
				tableObject.put("ARRAY", columnArray);
				// 1.2 Stavim tableObjekat u listu tabela
				tableArray.put(tableObject);
				columns.close();
			}
		}
		// 1.3 Listu tabela stavim u databaseObjekat
		databaseObject.put("ARRAY", tableArray);
		tables.close();

		/*
		 * System.out.println(databaseObject); Ctrl + C iz konzole pa:
		 * http://json.parser.online.fr/
		 */

		return databaseObject;
	}

	public JSONObject formReportJSON(DatabaseMetaData dbMetaData) throws SQLException, JSONException {

		JSONObject rootObject = new JSONObject();
		rootObject.put("NAME", "Sport");
		JSONArray groups = new JSONArray();

		final String group = "GRUPA_IZVESTAJA";
		final String individual = "POJEDINACNI_IZVESTAJI";

		ResultSet tables = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });

		String tableName;

		while (tables.next()) {

			tableName = tables.getString(TABLE_NAME);

			switch (tableName) {

			case group:
				// 1. Formiranje GRUPA
				Statement statement = AppGUI.getInstance().getConnection().createStatement();
				ResultSet rsData = statement.executeQuery("SELECT * FROM " + tableName);

				while (rsData.next()) {

					JSONObject groupObject = new JSONObject();
					groupObject.put("NAME", rsData.getString(3));
					JSONArray subGroups = new JSONArray();

					// 2. Formiranje podgrupa
					if (tableName.equals("Timski izvestaj")) {

						JSONObject subGroup = new JSONObject();
						subGroup.put("NAME", "Timski izvestaj");
						// 2.1a podgrupa -> podgrupe
						subGroups.put(subGroup);

					} else {

						String[] subGroupNames = new String[] { "Jedan na jedan", "Parent-child veza" };

						for (int i = 0; i < 2; i++) {

							JSONObject subGroup = new JSONObject();
							subGroup.put("NAME", subGroupNames[i]);
							// 2.1b podgrupa -> podgrupe
							subGroups.put(subGroup);
						}
					}
					// 2.2 Ubacivanje podgrupa u grupu
					groupObject.put("ARRAY", subGroups);
					// 1.1 grupa -> grupe
					groups.put(groupObject);
				}
				rootObject.put("ARRAY", groups);
				rsData.close();
				break;
			}
		}
		tables.close();

		return rootObject;
	}

	public JSONObject formDataJSON(DBFileModel dbFileModel) throws SQLException, JSONException {

		ArrayList<String> columnNames = dbFileModel.getColumnNames();
		String tableName = dbFileModel.getName();
		Connection connection = dbFileModel.getConnection();

		// 1. Napravim JSON za podatke iz tabele
		JSONObject dataObject = new JSONObject();
		dataObject.put("NAME", tableName);
		// 1.1 Ovdje ce da idu rowObjekti
		JSONArray dataArray = new JSONArray();

		Statement statement = connection.createStatement();
		ResultSet rsData = statement.executeQuery("SELECT * FROM " + dbFileModel.getName());

		// 1.2 Ucitam podatke iz resultSet-a i formiram rowObject
		while (rsData.next()) {

			JSONObject rowObject = new JSONObject();

			for (int i = 0; i < columnNames.size(); i++)
				rowObject.put(columnNames.get(i), rsData.getString(i + 1));

			dataArray.put(rowObject);
		}
		// 1.3 Listu redova stavim u dataObjekat
		dataObject.put("ARRAY", dataArray);
		rsData.close();

		/*
		 * System.out.println(dataObject); Ctrl + C iz konzole pa:
		 * http://json.parser.online.fr/
		 */

		return dataObject;
	}

	private void formPrimaryKeys(String tableName) throws SQLException {

		String query = "SELECT B.COLUMN_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS A, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE B WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND B.TABLE_NAME = '"
				+ tableName + "' AND A.CONSTRAINT_TYPE = 'PRIMARY KEY'ORDER BY B.TABLE_NAME";

		rsPrimaryKeys = AppGUI.getInstance().getConnection().createStatement().executeQuery(query);

		while (rsPrimaryKeys.next())
			if (!primaryKeys.contains(rsPrimaryKeys.getString("COLUMN_NAME")))
				primaryKeys.add(rsPrimaryKeys.getString("COLUMN_NAME"));
	}

	private void formForeignKeys(String tableName) throws SQLException {

		String query = "SELECT B.COLUMN_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS A, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE B WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND B.TABLE_NAME = '"
				+ tableName + "' AND A.CONSTRAINT_TYPE = 'FOREIGN KEY'ORDER BY B.TABLE_NAME";

		rsForeignKeys = AppGUI.getInstance().getConnection().createStatement().executeQuery(query);
	}

	private void checkColumnType(String columnName, JSONObject jsonObject) throws SQLException, JSONException {

		updateKeys();

		if (primaryKeys.contains(columnName))
			jsonObject.put("TYPE", DBNode.PRIMARY_KEY);

		else if (foreignKeys.contains(columnName))
			jsonObject.put("TYPE", DBNode.FOREIGN_KEY);

		else if (bothKeys.contains(columnName))
			jsonObject.put("TYPE", DBNode.BOTH_KEYS);

		else
			jsonObject.put("TYPE", DBNode.COLUMN);
	}

	public void updateKeys() throws SQLException {

		// Update primaryKeys for this column...
		while (rsPrimaryKeys.next())
			if (!primaryKeys.contains(rsPrimaryKeys.getString("COLUMN_NAME")))
				primaryKeys.add(rsPrimaryKeys.getString("COLUMN_NAME"));

		// Update foreignKeys for this column...
		while (rsForeignKeys.next())
			if (!foreignKeys.contains(rsForeignKeys.getString("COLUMN_NAME")))
				foreignKeys.add(rsForeignKeys.getString("COLUMN_NAME"));

		// Update bothKeys for this column...
		for (int i = 0; i < foreignKeys.size(); i++)
			if (primaryKeys.contains(foreignKeys.get(i))) {

				if (!bothKeys.contains(foreignKeys.get(i)))
					bothKeys.add(foreignKeys.get(i));
				foreignKeys.remove(foreignKeys.get(i));
			}
		// Remove (bothKey) elements form primaryKeys...
		for (int i = 0; i < primaryKeys.size(); i++)
			if (bothKeys.contains(primaryKeys.get(i)))
				primaryKeys.remove(primaryKeys.get(i));
	}

	public static JSONToolkit getInstance() {

		if (instance == null)
			instance = new JSONToolkit();

		return instance;
	}
}
