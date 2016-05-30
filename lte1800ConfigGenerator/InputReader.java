package lte1800ConfigGenerator;

import java.util.List;
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
	private File configInput = new File("C:\\CG input\\Config input.xlsx");

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

	public File getConfigInput() {
		return configInput;
	}

	public void setConfigInput(String configInputPath) {
		this.configInput = new File(configInputPath);
	}

	public void readTransmissionFile(List<LteSite> listOfSites) {
		XSSFWorkbook workbook = null;
		try {
			workbook = createExcelWorkbook(transmissionInput);
			XSSFSheet sheet1 = workbook.getSheetAt(0);
			int numberOfRows = sheet1.getLastRowNum();
			for (int i = 2; i <= numberOfRows; i++) {
				Row fromRow = sheet1.getRow(i);
				LteSite tempLteSite = createTempLteSite();
				populateDataIntoMap(tempLteSite.generalInfo, fromRow, 0);
				populateDataIntoMap(tempLteSite.transmission, fromRow, 3);
				listOfSites.add(tempLteSite);
			}
			// This is idiom how to close a File.
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private XSSFWorkbook createExcelWorkbook(File fromFile) {
		OPCPackage opcPackage;
		XSSFWorkbook workbook = null;
		try {
			opcPackage = OPCPackage.open(fromFile);
			workbook = new XSSFWorkbook(opcPackage);
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	private LteSite createTempLteSite() {
		LteSite tempLteSite = new LteSite();
		tempLteSite.createInitialGeneralInfoMap();
		tempLteSite.createInitialTransmissionMap();
		return tempLteSite;
	}

	private void populateDataIntoMap(Map<String, String> mapToPopulate, Row row, int cellColumn) {
		DataFormatter dataFormatter = new DataFormatter();
		Cell cell;
		for (Map.Entry<String, String> entry : mapToPopulate.entrySet()) {
			cell = row.getCell(cellColumn);
			cellColumn++;
			entry.setValue(dataFormatter.formatCellValue(cell));
		}
	}

	public void readRadioFileForCellInfo(List<LteSite> listOfSites) {
		XSSFWorkbook workbook = null;
		try {
			workbook = createExcelWorkbook(radioInput);
			XSSFSheet sheetCellInfo = workbook.getSheetAt(0);
			int numberOfRows = sheetCellInfo.getLastRowNum();
			for (int i = 1; i <= numberOfRows; i++) {
				Row row = sheetCellInfo.getRow(i);
				Cell readedCell = row.getCell(2);
				DataFormatter dataFormatter = new DataFormatter();
				String valueOfCell = dataFormatter.formatCellValue(readedCell);
				for (int j = 0; j < listOfSites.size(); j++) {
					LteSite tempLteSite = listOfSites.get(j);
					if (valueOfCell.equals(tempLteSite.generalInfo.get("eNodeBId"))) {
						LteCell tempLteCell = createTempLteCell();
						populateDataIntoMap(tempLteCell.cellInfo, row, 3);
						tempLteSite.lteCells.put(tempLteCell.cellInfo.get("localCellId"), tempLteCell);
					}
				}
			}
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private LteCell createTempLteCell() {
		LteCell tempLteCell = new LteCell();
		tempLteCell.createInitialCellInfoMap();
		return tempLteCell;
	}

	public void readRadioFileForNeighbours(List<LteSite> listOfSites) {
		XSSFWorkbook workbook = null;
		try {
			workbook = createExcelWorkbook(radioInput);
			XSSFSheet sheetNeighbours = workbook.getSheetAt(1);
			int numberOfRows = sheetNeighbours.getLastRowNum();
			for (int i = 1; i <= numberOfRows; i++) {
				Row row = sheetNeighbours.getRow(i);
				int cellColumn = 5;
				Cell readedCell = row.getCell(cellColumn);
				DataFormatter dataFormatter = new DataFormatter();
				String valueOfCell = dataFormatter.formatCellValue(readedCell);
				for (int j = 0; j < listOfSites.size(); j++) {
					LteSite tempLteSite = listOfSites.get(j);
					for (int k = 0; k < tempLteSite.lteCells.size(); k++) {
						LteCell tempLteCell = tempLteSite.lteCells.get(String.valueOf(k + 1));
						String lnCellId = tempLteCell.cellInfo.get("lnCellId");
						if (valueOfCell.equals(lnCellId)) {
							GsmNeighbour tempGsmNeighbour = new GsmNeighbour();
							Cell cell = row.getCell(++cellColumn);
							tempGsmNeighbour.cellName = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.cellId = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.bcc = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.ncc = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.lac = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.bcch = dataFormatter.formatCellValue(cell);
							cell = row.getCell(++cellColumn);
							tempGsmNeighbour.rac = dataFormatter.formatCellValue(cell);
							tempLteCell.gsmNeighbours.put(tempGsmNeighbour.cellId, tempGsmNeighbour);
						}
					}
				}
			}
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void readConfigFile(List<LteSite> listOfSites) {
		XSSFWorkbook workbook = null;
		try {
			workbook = createExcelWorkbook(configInput);
			XSSFSheet sheetNeighbours = workbook.getSheetAt(0);
			int numberOfRows = sheetNeighbours.getLastRowNum();
			for (int i = 1; i <= numberOfRows; i++) {
				Row row = sheetNeighbours.getRow(i);
				int cellColumn = 1;
				Cell readedCell = row.getCell(cellColumn);
				DataFormatter dataFormatter = new DataFormatter();
				String valueOfCell = dataFormatter.formatCellValue(readedCell);
				for (int j = 0; j < listOfSites.size(); j++) {
					LteSite tempLteSite = listOfSites.get(j);
					if (valueOfCell.equals(tempLteSite.generalInfo.get("LocationId"))) {
						populateDataIntoMap(tempLteSite.hardware, row, 2);
					}
				}
			}
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
