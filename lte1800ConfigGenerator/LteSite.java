package lte1800ConfigGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

public class LteSite {
	Map<String, String> generalInfo;
	Map<String, String> transmission;
	Map<String, LteCell> lteCells;

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
}
