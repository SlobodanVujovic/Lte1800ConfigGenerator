package lte1800ConfigGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InputReader {
	private File radioInput = new File("C:\\CG input\\Radio.xlsx");
	private File transmissionInput = new File("C:\\CG input\\Transmission.xlsx");
	AllLteSites allSites = new AllLteSites();

	public void setRadioInput(String radioFilePath) {
		this.radioInput = new File(radioFilePath);
	}

	public void setTransmissionInput(String transmissionFilePath) {
		this.transmissionInput = new File(transmissionFilePath);
	}

	public File getRadioInput() {
		return radioInput;
	}

	public File getTransmissionInput() {
		return transmissionInput;
	}

	public void readTransmissionFile() {
		try {
			OPCPackage opcPackage = OPCPackage.open(transmissionInput);
			XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
			try {
				XSSFSheet sheet1 = workbook.getSheetAt(0);
				int numberOfRows = sheet1.getLastRowNum();
				LteSite tempLteSite;
				Row row;
				Cell cell;
				DataFormatter dataFormatter = new DataFormatter();
				for (int i = 2; i <= numberOfRows; i++) {
					row = sheet1.getRow(i);
					tempLteSite = createTempLteSite();
					int cellColumn = 0;
					for (Map.Entry<String, String> entry : tempLteSite.generalInfo.entrySet()) {
						cell = row.getCell(cellColumn);
						cellColumn++;
						entry.setValue(dataFormatter.formatCellValue(cell));
					}
					for (Map.Entry<String, String> entry : tempLteSite.transmission.entrySet()) {
						cell = row.getCell(cellColumn);
						cellColumn++;
						entry.setValue(dataFormatter.formatCellValue(cell));
					}
					allSites.listOfSites.add(tempLteSite);
				}
			} finally {
				workbook.close();
			}
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public LteSite createTempLteSite() {
		LteSite tempLteSite = new LteSite();
		tempLteSite.createInitialGeneralInfoMap();
		tempLteSite.createInitialTransmissionMap();
		return tempLteSite;
	}

}
