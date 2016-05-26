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
		try {
			OPCPackage opcPackage = OPCPackage.open(transmissionInput);
			XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
			try {
				XSSFSheet sheet1 = workbook.getSheetAt(0);
				int numberOfRows = sheet1.getLastRowNum();
				DataFormatter dataFormatter = new DataFormatter();
				for (int i = 2; i <= numberOfRows; i++) {
					Row row = sheet1.getRow(i);
					LteSite tempLteSite = createTempLteSite();
					int cellColumn = 0;
					Cell cell;
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
					listOfSites.add(tempLteSite);
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

	public void readRadioFileForCellInfo(List<LteSite> listOfSites) {
		try {
			OPCPackage opcPackage = OPCPackage.open(radioInput);
			XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
			try {
				XSSFSheet sheetCellInfo = workbook.getSheetAt(0);
				int numberOfRows = sheetCellInfo.getLastRowNum();
				DataFormatter dataFormatter = new DataFormatter();
				for (int i = 1; i <= numberOfRows; i++) {
					Row row = sheetCellInfo.getRow(i);
					int cellColumn = 2;
					Cell readedCell = row.getCell(cellColumn);
					String valueOfCell = dataFormatter.formatCellValue(readedCell);
					for (int j = 0; j < listOfSites.size(); j++) {
						LteSite tempLteSite = listOfSites.get(j);
						if (valueOfCell.equals(tempLteSite.generalInfo.get("eNodeBId"))) {
							LteCell tempLteCell = createTempLteCell();
							for (Map.Entry<String, String> entry : tempLteCell.cellInfo.entrySet()) {
								cellColumn++;
								Cell cell = row.getCell(cellColumn);
								entry.setValue(dataFormatter.formatCellValue(cell));
							}
							tempLteSite.lteCells.put(tempLteCell.cellInfo.get("localCellId"), tempLteCell);
						}
					}
				}
			} finally {
				workbook.close();
			}
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	private LteCell createTempLteCell() {
		LteCell tempLteCell = new LteCell();
		tempLteCell.createInitialCellInfoMap();
		return tempLteCell;
	}

	public void readRadioFileForNeighbours(List<LteSite> listOfSites) {
		try {
			OPCPackage opcPackage = OPCPackage.open(radioInput);
			XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
			try {
				XSSFSheet sheetNeighbours = workbook.getSheetAt(1);
				int numberOfRows = sheetNeighbours.getLastRowNum();
				DataFormatter dataFormatter = new DataFormatter();
				for (int i = 1; i <= numberOfRows; i++) {
					Row row = sheetNeighbours.getRow(i);
					int cellColumn = 5;
					Cell readedCell = row.getCell(cellColumn);
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
				workbook.close();
			}
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public void readConfigFile(List<LteSite> listOfSites) {
		try {
			OPCPackage opcPackage = OPCPackage.open(configInput);
			XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
			try {
				XSSFSheet sheetNeighbours = workbook.getSheetAt(0);
				int numberOfRows = sheetNeighbours.getLastRowNum();
				DataFormatter dataFormatter = new DataFormatter();
				for (int i = 1; i <= numberOfRows; i++) {
					Row row = sheetNeighbours.getRow(i);
					int cellColumn = 1;
					Cell readedCell = row.getCell(cellColumn);
					String valueOfCell = dataFormatter.formatCellValue(readedCell);
					for (int j = 0; j < listOfSites.size(); j++) {
						LteSite tempLteSite = listOfSites.get(j);
						if (valueOfCell.equals(tempLteSite.generalInfo.get("LocationId"))) {
							for (Map.Entry<String, String> entry : tempLteSite.hardware.entrySet()) {
								cellColumn++;
								Cell cell = row.getCell(cellColumn);
								entry.setValue(dataFormatter.formatCellValue(cell));
							}
						}
					}
				}
			} finally {
				workbook.close();
			}
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
		}
	}
}
