package infoViewer.view.treeCellRenderers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import infoViewer.model.trees.dbTree.DBNode;

public class DBTreeCellRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		DBNode dbNode = (DBNode) value;

		if (dbNode.getType() == DBNode.PRIMARY_KEY || dbNode.getType() == DBNode.BOTH_KEYS)
			setIcon(new ImageIcon("src/resources/icons/primaryKey.png"));

		else if (dbNode.getType() == DBNode.FOREIGN_KEY)
			setIcon(new ImageIcon("src/resources/icons/foreignKey.png"));

		else if (dbNode.getType() == DBNode.COLUMN)
			setIcon(new ImageIcon("src/resources/icons/noKey.png"));

		else if (dbNode.getType() == DBNode.DATABASE)
			setIcon(new ImageIcon("src/resources/icons/database.png"));

		else if (dbNode.getType() == DBNode.TABLE)
			setIcon(new ImageIcon("src/resources/icons/table.png"));

		return this;
	}
}
