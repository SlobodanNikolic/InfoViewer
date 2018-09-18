package infoViewer.model.trees.dbTree;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infoViewer.model.json.JSONToolkit;
import infoViewer.view.AppGUI;
import infoViewer.view.treeCellRenderers.DBTreeCellRenderer;

public class ReportTree extends JTree {

	private DBNode root;

	private DefaultTreeModel treeModel;
	private DatabaseMetaData dbMetaData;

	private JSONObject reportJSON; 

	public ReportTree() {

		root = new DBNode("No database", DBNode.DATABASE, "", 0, true);

		treeModel = new DefaultTreeModel(root);
		setModel(treeModel);
	}

	public void formReportTree(Connection connection) throws SQLException {

		DatabaseMetaData dbMetaData = connection.getMetaData();

		try {

			reportJSON = JSONToolkit.getInstance().formReportJSON(dbMetaData);
			parseJSON(reportJSON);

		} catch (JSONException arhimed2) {
			arhimed2.printStackTrace();
		}

		AppGUI.getInstance().getReportTree().expandRow(0);
	}

	public void parseJSON(JSONObject jsonObject) throws JSONException {

		JSONArray groupArray = jsonObject.getJSONArray("ARRAY");

		for (int i = 0; i < groupArray.length(); i++) {

			JSONObject groupObject = (JSONObject) groupArray.get(i);
			JSONArray subGroupArray = groupObject.getJSONArray("ARRAY");

			String name = groupObject.getString("NAME");
			int type = DBNode.REPORT;
			String dataType = "";
			int fieldSize = 0;
			boolean isNull = false;

			DBNode groupNode = new DBNode(name, type, dataType, fieldSize, isNull);

			for (int j = 0; j < subGroupArray.length(); j++) {

				JSONObject subGroupObject = (JSONObject) subGroupArray.get(j);

				String subGroupName = subGroupObject.getString("NAME");
				int subGroupType = DBNode.REPORT;
				String subGroupDataType = "";
				int subGroupFieldSize = 0;
				boolean subGroupIsNull = false;

				DBNode subGroupNode = new DBNode(subGroupName, subGroupType, subGroupDataType, subGroupFieldSize, subGroupIsNull);
				groupNode.add(subGroupNode);
			}
			root.add(groupNode);
		}
	}
	
	public void setRoot(DBNode root) {
		
		this.root = root;
	}
	
	public JSONObject getReportJSON() {
		
		return reportJSON;
	}
}
