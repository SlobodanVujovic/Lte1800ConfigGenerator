package lte1800ConfigGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllConfigFiles {
	XmlCreator xmlCreator = new XmlCreator();
	AllLteSites allLteSites = new AllLteSites();
	List<LteSite> listOfAllSites = allLteSites.listOfAllSites;
	List<File> listOfAllConfigFiles = new ArrayList<>();

	public void createConfigFile() {
		allLteSites.createListOfAllSites();
		for (LteSite lteSite : listOfAllSites) {
			String siteCode = lteSite.generalInfo.get("LocationId");
			String eNodeBId = lteSite.generalInfo.get("eNodeBId");
			String siteName = lteSite.generalInfo.get("eNodeBName");
			xmlCreator.copyTemplateXmlFile(siteCode);
			xmlCreator.createXmlDocument();
			xmlCreator.editXmlDateAndTime();
			boolean ftifIsUsed = false;
			String ftifIsUsedStr = lteSite.hardware.get("ftif");
			if (ftifIsUsedStr.equals("DA")) {
				ftifIsUsed = true;
			}
			xmlCreator.isFtifUsed(ftifIsUsed);
			xmlCreator.editMrbts_eNodeBId(eNodeBId);
			xmlCreator.editLnbts_eNodeBId(eNodeBId);
			xmlCreator.editLncellId(lteSite);
			String numberOfSharedRfModules = lteSite.hardware.get("numberOfSharedRfModules");
			boolean isSharing = false;
			if (!numberOfSharedRfModules.equals("0") && !numberOfSharedRfModules.equals("")) {
				isSharing = true;
			}
			xmlCreator.editBtsscl_BtsId_BtsName(eNodeBId, siteCode, isSharing);
			xmlCreator.editLnbts_EnbName(siteCode);
			int counter = 0;
			for (GsmNeighbour gsmNeighbour : lteSite.uniqueGsmNeighbours) {
				xmlCreator.editLnadjg_cellParameters(gsmNeighbour, eNodeBId, String.valueOf(counter++));
			}
			counter = 0;
			for (Map.Entry<String, LteCell> entry : lteSite.lteCells.entrySet()) {
				LteCell lteCell = entry.getValue();
				xmlCreator.editLncel_cellParameters(lteCell, eNodeBId);
				String lnCellId = lteCell.cellInfo.get("lnCellId");
				Set<String> uniqueBcchOfNeighbours = lteSite.uniqueBcchOfNeighbours;
				xmlCreator.editGnfl_BcchUnique(eNodeBId, lnCellId, uniqueBcchOfNeighbours);
				xmlCreator.editLnhog_BcchUnique(eNodeBId, lnCellId, uniqueBcchOfNeighbours);
				xmlCreator.editRedrt_BcchUnique(eNodeBId, lnCellId, uniqueBcchOfNeighbours);
				for (Map.Entry<String, GsmNeighbour> neighbourEntry : lteCell.gsmNeighbours.entrySet()) {
					GsmNeighbour gsmNeighbour = neighbourEntry.getValue();
					xmlCreator.editLnrelg_CellIdUniquePerCell(eNodeBId, lnCellId, String.valueOf(counter++),
							gsmNeighbour);
				}
				counter = 0;
			}
			xmlCreator.editLcell_AnttenaPorts(lteSite.hardware.get("cell1Ports"));
			String numberOfRfModulesStr = lteSite.hardware.get("numberOfRfModules");
			int numberOfRfModules = Integer.valueOf(numberOfRfModulesStr);
			xmlCreator.editNumberOfAntenna(numberOfRfModules);
			for (counter = 1; counter <= numberOfRfModules; counter++) {
				String rfModuleNumber = "rf" + counter + "IsShared";
				boolean isModuleShared = false;
				if (lteSite.hardware.get(rfModuleNumber).equals("DA")) {
					isModuleShared = true;
				}
				xmlCreator.editRmod_SiteName(eNodeBId, String.valueOf(counter), siteName, isModuleShared);
			}
			if (!numberOfSharedRfModules.equals("0") && !numberOfSharedRfModules.equals("")) {
				xmlCreator.editGsmSmod_SiteName(eNodeBId);
			}
			xmlCreator.editLteSmod_SiteName(eNodeBId, siteName);
			xmlCreator.editFtm_SiteCode(eNodeBId, siteCode, siteName);
			xmlCreator.editIpno(lteSite);
			xmlCreator.editIpno_Twamp(lteSite);
			xmlCreator.editTwamp(lteSite.transmission.get("cuDestIp"));
			xmlCreator.editIprt(lteSite);
			xmlCreator.editIvif1(lteSite);
			xmlCreator.editIvif2(lteSite);
			xmlCreator.editTopf(eNodeBId, lteSite.transmission.get("topIp"));
			xmlCreator.writeToXml();
			listOfAllConfigFiles.add(xmlCreator.outputFile);
		}
	}
}
