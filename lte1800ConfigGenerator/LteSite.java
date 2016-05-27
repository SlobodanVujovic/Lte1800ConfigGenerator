package lte1800ConfigGenerator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class LteSite {
	Map<String, String> generalInfo;
	Map<String, String> transmission;
	Map<String, String> hardware;
	Map<String, LteCell> lteCells = new LinkedHashMap<>();
	Set<GsmNeighbour> uniqueGsmNeighbours = new LinkedHashSet<>();
	Set<String> uniqueBcchOfNeighbours = new LinkedHashSet<>();

	public void createInitialGeneralInfoMap() {
		generalInfo = new LinkedHashMap<>();
		generalInfo.put("LocationId", "dummyData");
		generalInfo.put("eNodeBId", "dummyData");
		generalInfo.put("eNodeBName", "dummyData");
	}

	public void createInitialTransmissionMap() {
		transmission = new LinkedHashMap<>();
		transmission.put("cuVlanId", "dummyData");
		transmission.put("cuDestIp", "dummyData");
		transmission.put("cuSubnet", "dummyData");
		transmission.put("cuSubnetSize", "dummyData");
		transmission.put("cuGwIp", "dummyData");
		transmission.put("mIp", "dummyData");
		transmission.put("sVlanId", "dummyData");
		transmission.put("sIp", "dummyData");
		transmission.put("sSubnet", "dummyData");
		transmission.put("sSubnetSize", "dummyData");
		transmission.put("sGwIp", "dummyData");
		transmission.put("topIp", "dummyData");
	}

	public void createHardwareMap() {
		hardware = new LinkedHashMap<>();
		hardware.put("numberOfRfModules", "dummyData");
		hardware.put("numberOfSharedRfModules", "dummyData");
		hardware.put("cell1Ports", "dummyData");
		hardware.put("cell2Ports", "dummyData");
		hardware.put("cell3Ports", "dummyData");
		hardware.put("cell4Ports", "dummyData");
		hardware.put("rf1IsShared", "dummyData");
		hardware.put("rf2IsShared", "dummyData");
		hardware.put("rf3IsShared", "dummyData");
		hardware.put("rf4IsShared", "dummyData");
		hardware.put("ftif", "dummyData");
		hardware.put("gsmPort", "dummyData");
		hardware.put("umtsPort", "dummyData");
	}

	public void createUniqueGsmNeighbours() {
		for (Map.Entry<String, LteCell> cellEntry : lteCells.entrySet()) {
			LteCell tempLteCell = cellEntry.getValue();
			for (Map.Entry<String, GsmNeighbour> neighbourEntry : tempLteCell.gsmNeighbours.entrySet()) {
				GsmNeighbour tempGsmNeighbour = neighbourEntry.getValue();
				uniqueGsmNeighbours.add(tempGsmNeighbour);
			}
		}
	}

	public void createUniqueBcchOfNeighbours() {
		for (Iterator<GsmNeighbour> iterator = uniqueGsmNeighbours.iterator(); iterator.hasNext();) {
			GsmNeighbour gsmNeighbour = iterator.next();
			uniqueBcchOfNeighbours.add(gsmNeighbour.bcch);
		}
	}
}
