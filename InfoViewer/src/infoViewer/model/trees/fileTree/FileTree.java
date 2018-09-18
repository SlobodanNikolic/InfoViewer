package infoViewer.model.trees.fileTree;

import java.io.File;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import infoViewer.view.treeCellRenderers.FileTreeCellRenderer;

public class FileTree extends JTree {

	private DefaultTreeModel treeModel;
	private FileSystemView fileSystemView;

	public FileTree() {

		fileSystemView = FileSystemView.getFileSystemView();

		// Korijen
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		// Grananje
		File[] roots = fileSystemView.getRoots();
		for (File fileSystemRoot : roots) {

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);
			File[] files = fileSystemView.getFiles(fileSystemRoot, true);
			for (File file : files)
				if (file.isDirectory())
					node.add(new DefaultMutableTreeNode(file));
				else if (isTextFile(file))
					node.add(new DefaultMutableTreeNode(file));
			showChildren(node);
		}

		setModel(treeModel);
		setRootVisible(false);
		setCellRenderer(new FileTreeCellRenderer());
		expandRow(0);

		addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent event) {

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
				showChildren(node);
			}
		});
	}

	private void showChildren(DefaultMutableTreeNode node) {

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {

			@Override
			public Void doInBackground() {

				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true);
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory() || isTextFile(child)) {
								System.out.println("Child: " + child);
								publish(child);
							}
						}
					}
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {

				for (File child : chunks)
					node.add(new DefaultMutableTreeNode(child));
			}
		};
		worker.execute();
	}

	private boolean isTextFile(File file) {

		if (file.getName().endsWith(".txt"))
			return true;

		return false;
	}
}
