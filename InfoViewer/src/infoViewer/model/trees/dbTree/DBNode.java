package infoViewer.model.trees.dbTree;

import java.awt.Font;

import javax.swing.tree.DefaultMutableTreeNode;

public class DBNode extends DefaultMutableTreeNode {

	public static final int DATABASE = 0;
	public static final int TABLE = 1;
	public static final int COLUMN = 2;
	public static final int PRIMARY_KEY = 3;
	public static final int FOREIGN_KEY = 4;
	public static final int BOTH_KEYS = 5;
	public static final int REPORT = 6;

	private String name;
	private int type;
	private String dataType;
	private int fieldSize;
	private boolean isNull;

	public DBNode(String name, int type, String dataType, int fieldSize, boolean isNull) {

		super();
		this.name = name;
		this.type = type;
		this.dataType = dataType;
		this.fieldSize = fieldSize;
		this.isNull = isNull;
	}

	@Override
	public String toString() {

		String keyType;

		switch (type) {

		case BOTH_KEYS:
			keyType = "PK, FK, ";
			break;
		case PRIMARY_KEY:
			keyType = "PK, ";
			break;
		case FOREIGN_KEY:
			keyType = "FK, ";
			break;
		default:
			keyType = "";
		}

		String field_Size;

		if (!dataType.equals("datetime"))
			field_Size = "(" + fieldSize + ")";
		else
			field_Size = "";

		if (type == DATABASE || type == TABLE || type == REPORT || type == -1)
			return name;
		else
			return name + " (" + keyType + dataType + field_Size + ", " + ((isNull) ? "null" : "not null") + ") ";
	}

	public void setType(int type) {

		this.type = type;
	}

	public int getType() {

		return type;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}
}
