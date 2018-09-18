package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import infoViewer.view.AppGUI;
import infoViewer.view.database.ReportView;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class ReadReport extends AbstractAction {

	private String tableName;

	public ReadReport() {

		putValue(NAME, "Read report");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/read.png"));
		putValue(SHORT_DESCRIPTION, "Open a report.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			if (AppGUI.getInstance().getConnection() == null) {

				JOptionPane.showMessageDialog(null, "You need to connect to a database first!");
				return;
			}
			
			if(tableName == null)
				tableName = "Default";

			String reportSourceFile = "src/resources/reports/" + tableName + ".jrxml";
			// Compile jrxml file
			JasperReport jasperReport = JasperCompileManager.compileReport(reportSourceFile);
			// Parameters for report
			Map<String, Object> parameters = new HashMap<String, Object>();

			Connection connection = AppGUI.getInstance().getConnection();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			// PDF Exporter
			JRPdfExporter exporter = new JRPdfExporter();
			ExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
			// ExporterInput
			exporter.setExporterInput(exporterInput);

			String exportTo = "src/resources/reports/" + tableName + ".pdf";
			// ExporterOutput
			OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(exportTo);
			// Output
			exporter.setExporterOutput(exporterOutput);
			// EXPORT!!!
			SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			exporter.setConfiguration(configuration);
			exporter.exportReport();

			new ReportView(tableName, jasperPrint);

		} catch (Exception e1) {

			JOptionPane.showMessageDialog(null, "ReadReport action failed!", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	public void setTableName(String tableName) {

		this.tableName = tableName;
	}
}
