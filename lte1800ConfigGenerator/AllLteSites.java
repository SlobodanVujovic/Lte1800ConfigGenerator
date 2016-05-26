package lte1800ConfigGenerator;

import java.util.List;
import java.util.ArrayList;

public class AllLteSites {
	List<LteSite> listOfAllSites = new ArrayList<>();
	InputReader inputReader = new InputReader();

	public void createListOfAllSites() {
		inputReader.readTransmissionFile(listOfAllSites);
		inputReader.readRadioFileForCellInfo(listOfAllSites);
		inputReader.readRadioFileForNeighbours(listOfAllSites);
		for (LteSite lteSite : listOfAllSites) {
			lteSite.createUniqueGsmNeighbours();
			lteSite.createUniqueBcchOfNeighbours();
			lteSite.createHardwareMap();
		}
		inputReader.readConfigFile(listOfAllSites);
	}

}
