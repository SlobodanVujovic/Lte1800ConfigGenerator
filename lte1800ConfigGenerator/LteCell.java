package lte1800ConfigGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

public class LteCell {
	Map<String, String> cellInfo;
	Map<String, GsmNeighbour> gsmNeighbours;

	public void createInitialCellInfoMap() {
		cellInfo = new LinkedHashMap<>();
		cellInfo.put("lnCellId", "dummyData");
		cellInfo.put("localCellId", "dummyData");
		cellInfo.put("pci", "dummyData");
		cellInfo.put("tac", "dummyData");
		cellInfo.put("cellName", "dummyData");
		cellInfo.put("rootSeqIndex", "dummyData");
		cellInfo.put("maxPower", "dummyData");
		cellInfo.put("channelBw", "dummyData");
		cellInfo.put("dlEarfcn", "dummyData");
		cellInfo.put("longitude", "dummyData");
		cellInfo.put("latitude", "dummyData");
		cellInfo.put("azimut", "dummyData");
		cellInfo.put("height", "dummyData");
		cellInfo.put("netActCellId", "dummyData");
		cellInfo.put("eci", "dummyData");
	}
}
