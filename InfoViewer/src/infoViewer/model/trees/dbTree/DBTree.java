package infoViewer.model.trees.dbTree;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jfree.data.statistics.DefaultMultiValueCategoryDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infoViewer.model.json.JSONParser;
import infoViewer.model.json.JSONToolkit;
import infoViewer.view.AppGUI;
import infoViewer.view.treeCellRenderers.DBTreeCellRenderer;

public class DBTree extends JTree implements JSONParser {

	private DBNode root;

	private DefaultTreeModel treeModel;
	private DatabaseMetaData dbMetaData;

	private JSONObject dbJSON; // Struktura baze podataka

	public DBTree() {

		root = new DBNode("No database", DBNode.DATABASE, "", 0, true);

		treeModel = new DefaultTreeModel(root);
		setModel(treeModel);
	}

	public void formDBTree(Connection connection) throws SQLException {

		DatabaseMetaData dbMetaData = connection.getMetaData();

		try {

			dbJSON = JSONToolkit.getInstance().formDBJSON(dbMetaData);
			parseJSON(dbJSON);

		} catch (JSONException arhimed) {
			arhimed.printStackTrace();
		}

		setCellRenderer(new DBTreeCellRenderer());

		AppGUI.getInstance().getDBTree().expandRow(0);
	}

	public void parseJSON(JSONObject jsonObject) throws JSONException {

		JSONArray tableArray = jsonObject.getJSONArray("ARRAY");

		for (int i = 0; i < tableArray.length(); i++) {

			JSONObject tableObject = (JSONObject) tableArray.get(i);
			JSONArray columnArray = tableObject.getJSONArray("ARRAY");

			String name = tableObject.getString("NAME");
			int type = tableObject.getInt("TYPE");
			String dataType = "";
			int fieldSize = 0;
			boolean isNull = false;

			DBNode tableNode = new DBNode(name, type, dataType, fieldSize, isNull);

			for (int j = 0; j < columnArray.length(); j++) {

				JSONObject columnObject = (JSONObject) columnArray.get(j);

				String columnName = columnObject.getString("NAME");
				int columnType = columnObject.getInt("TYPE");
				String columnDataType = columnObject.getString("DATA_TYPE");
				int columnFieldSize = columnObject.getInt("FIELD_SIZE");
				boolean columnIsNull = columnObject.getBoolean("NULLABLE");

				DBNode columnNode = new DBNode(columnName, columnType, columnDataType, columnFieldSize, columnIsNull);
				tableNode.add(columnNode);
			}
			root.add(tableNode);
		}
	}
	
	public void setRoot(DBNode root) {
		
		this.root = root;
	}
	
	public JSONObject getDBJSON() {
		
		return dbJSON;
	}
}
