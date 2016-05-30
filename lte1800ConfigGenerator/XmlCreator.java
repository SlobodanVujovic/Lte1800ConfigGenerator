package lte1800ConfigGenerator;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlCreator {
	String templateFilePath = "C:\\CG output\\Commissioning_KKLLL_YYYYMMDD.xml", outputFilePath;
	File templateFile = new File(templateFilePath), outputFile, ftifFile = new File("C:\\CG output\\FTIF_Config.xml");
	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	DateTimeFormatter dateAndTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	Document xmlDocument;

	public void copyTemplateXmlFile(String siteCode) {
		createOutputFilePath(siteCode);
		outputFile = new File(outputFilePath);
		try {
			Files.copy(templateFile.toPath(), outputFile.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createOutputFilePath(String siteCode) {
		String date = LocalDate.now().format(dateFormat);
		outputFilePath = templateFilePath.replace("KKLLL", siteCode);
		outputFilePath = outputFilePath.replace("YYYYMMDD", date);
	}

	/*
	 * Create "builderFactory" that we use to create "builder" to parse XML file. Parsing XML we create "Document" object that represent XML file and
	 * have methods to manipulate with it.
	 */
	public void createXmlDocument() {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setIgnoringComments(true);
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			xmlDocument = builder.parse(outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editXmlDateAndTime() {
		String dateAndTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(dateAndTimeFormat);
		NodeList logList = xmlDocument.getElementsByTagName("log");
		Node logNode = logList.item(0);
		setAttributeValueOfNode(logNode, "dateTime", dateAndTime);
	}

	private void setAttributeValueOfNode(Node node, String attributeName, String newValue) {
		NamedNodeMap nodeAttributes = node.getAttributes();
		Node specificAttribute = nodeAttributes.getNamedItem(attributeName);
		specificAttribute.setNodeValue(newValue);
	}

	public void writeToXml() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			// When write new Node to xml file, and want new line after every element, we use set:
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result = new StreamResult(outputFile);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public void editMrbts_eNodeBId(String eNodeBId) {
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[contains(@distName, 'MRBTS')]");
		for (int i = 0; i < managedObjectList.getLength(); i++) {
			Node managedObjectNode = managedObjectList.item(i);
			String distNameOldValue = getAttributeValueFromNode(managedObjectNode, "distName");
			int startIndex = distNameOldValue.indexOf("-");
			int endIndex = distNameOldValue.indexOf("/");
			if (endIndex != -1) {
				String distNameNewValue = distNameOldValue.substring(0, startIndex + 1) + eNodeBId
						+ distNameOldValue.substring(endIndex);
				setAttributeValueOfNode(managedObjectNode, "distName", distNameNewValue);
			} else {
				String distNameNewValue = distNameOldValue.substring(0, startIndex + 1) + eNodeBId;
				setAttributeValueOfNode(managedObjectNode, "distName", distNameNewValue);
			}
		}
	}

	private Object getNodeSetObjectFromXmlDocument(String stringExpression) {
		// XPathFactory is used to create XPath.
		XPathFactory xPathFactory = XPathFactory.newInstance();
		// XPath enable access to evaluation environment.
		XPath xPath = xPathFactory.newXPath();
		// XPathExpression provides access to XPath expressions.
		XPathExpression expression;
		Object result = null;
		try {
			/*
			 * First we define expression that say what we want to find in document. Double forward slashes ("//") represent root node in xml. Then we
			 * drill to node we need separating different nodes with forward slash ("/"). When we get to the node we need, if we want node to have
			 * specific attribute with some value then we put that attribute in square braces ("[ ]") and precede it with "@" sign.
			 */
			expression = xPath.compile(stringExpression);
			// Result is evaluation of document with defined expression and as output we can demand NodeSet.
			result = expression.evaluate(xmlDocument, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String getAttributeValueFromNode(Node node, String attributeName) {
		NamedNodeMap nodeAttributes = node.getAttributes();
		Node specificAttribute = nodeAttributes.getNamedItem(attributeName);
		String valueOfAttribute = specificAttribute.getTextContent();
		return valueOfAttribute;
	}

	public void editLnbts_eNodeBId(String eNodeBId) {
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[contains(@distName, 'LNBTS')]");
		for (int i = 0; i < managedObjectList.getLength(); i++) {
			Node managedObjectNode = managedObjectList.item(i);
			String distNameOldValue = getAttributeValueFromNode(managedObjectNode, "distName");
			int startIndex = distNameOldValue.indexOf("LNBTS") + 6;
			int endIndex = distNameOldValue.indexOf("/", startIndex);
			if (endIndex != -1) {
				String distNameNewValue = distNameOldValue.substring(0, startIndex) + eNodeBId
						+ distNameOldValue.substring(endIndex);
				setAttributeValueOfNode(managedObjectNode, "distName", distNameNewValue);
			} else {
				String distNameNewValue = distNameOldValue.substring(0, startIndex) + eNodeBId;
				setAttributeValueOfNode(managedObjectNode, "distName", distNameNewValue);
			}
		}
	}

	public void editLncellId(LteSite lteSite) {
		/*
		 * Pay attention to expression string, with contains(x, y) we search for y inside x. This string say to search all managedObjects that in
		 * distName attribute have "LNCEL-" anywhere.
		 */
		NodeList lncelNodeList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[contains(@distName,'LNCEL-')]");
		for (int i = 0; i < lncelNodeList.getLength(); i++) {
			Node lncelNode = lncelNodeList.item(i);
			String distNameOldValue = getAttributeValueFromNode(lncelNode, "distName");
			int startIndex = distNameOldValue.indexOf("LNCEL") + 6;
			int endIndex = distNameOldValue.indexOf("/", startIndex);
			if (endIndex != -1) {
				String oldCellId = distNameOldValue.substring(startIndex, endIndex);
				if (oldCellId.charAt(oldCellId.length() - 1) == '1') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("1"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId
							+ distNameOldValue.substring(endIndex);
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else if (oldCellId.charAt(oldCellId.length() - 1) == '2') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("2"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId
							+ distNameOldValue.substring(endIndex);
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else if (oldCellId.charAt(oldCellId.length() - 1) == '3') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("3"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId
							+ distNameOldValue.substring(endIndex);
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("4"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId
							+ distNameOldValue.substring(endIndex);
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				}
			} else {
				String oldCellId = distNameOldValue.substring(startIndex);
				if (oldCellId.charAt(oldCellId.length() - 1) == '1') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("1"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId;
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else if (oldCellId.charAt(oldCellId.length() - 1) == '2') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("2"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId;
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else if (oldCellId.charAt(oldCellId.length() - 1) == '3') {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("3"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId;
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				} else {
					LteCell lteCell = lteSite.lteCells.get(String.valueOf("4"));
					String lncellId = lteCell.cellInfo.get("lnCellId");
					String distNameNewValue = distNameOldValue.substring(0, startIndex) + lncellId;
					setAttributeValueOfNode(lncelNode, "distName", distNameNewValue);
				}
			}

		}
	}

	public void editBtsscl_BtsId_BtsName(String eNodeBId, String siteCode, boolean isSharing) {
		NodeList pNodeList = (NodeList) getNodeSetObjectFromXmlDocument("//cmData/managedObject[@class=\"BTSSCL\"]/p");
		for (int i = 0; i < pNodeList.getLength(); i++) {
			Node pNode = pNodeList.item(i);
			String nameNodeValue = getAttributeValueFromNode(pNode, "name");
			if (nameNodeValue.equals("btsId")) {
				pNode.setTextContent(eNodeBId);
			} else if (nameNodeValue.equals("btsName")) {
				pNode.setTextContent(siteCode);
			} else if (nameNodeValue.equals("rfSharingEnabled")) {
				if (isSharing) {
					pNode.setTextContent("true");
				} else {
					pNode.setTextContent("false");
				}
			}
		}
	}

	public void editLnbts_EnbName(String siteCode) {
		Node pNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"LNBTS\"]/p[@name=\"enbName\"]");
		pNode.setTextContent(siteCode);
	}

	private Object getNodeObjectFromXmlDocument(String stringExpression) {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		XPathExpression expression;
		Object result = null;
		try {
			expression = xPath.compile(stringExpression);
			// Result is evaluation of document with defined expression and as output we can demand Node.
			result = expression.evaluate(xmlDocument, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void editLnadjg_cellParameters(GsmNeighbour gsmNeighbour, String eNodeBId, String counter) {
		Element managedObject = xmlDocument.createElement("managedObject");
		managedObject.setAttribute("class", "LNADJG");
		String distName = "MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId + "/LNADJG-" + counter;
		managedObject.setAttribute("distName", distName);
		managedObject.setAttribute("operation", "create");
		managedObject.setAttribute("version", "LN7.0");
		Element p1 = xmlDocument.createElement("p");
		Element p2 = xmlDocument.createElement("p");
		Element p3 = xmlDocument.createElement("p");
		Element p4 = xmlDocument.createElement("p");
		Element p5 = xmlDocument.createElement("p");
		Element p6 = xmlDocument.createElement("p");
		Element p7 = xmlDocument.createElement("p");
		Element p8 = xmlDocument.createElement("p");
		Element p9 = xmlDocument.createElement("p");
		Element p10 = xmlDocument.createElement("p");
		Element p11 = xmlDocument.createElement("p");
		Element p12 = xmlDocument.createElement("p");
		Element p13 = xmlDocument.createElement("p");
		p1.setAttribute("name", "arfcnValueGeran");
		p2.setAttribute("name", "bandIndicatorGeran");
		p3.setAttribute("name", "basestationColourCode");
		p4.setAttribute("name", "gTargetCi");
		p5.setAttribute("name", "dtm");
		p6.setAttribute("name", "gTargetLac");
		p7.setAttribute("name", "mcc");
		p8.setAttribute("name", "mnc");
		p9.setAttribute("name", "mncLength");
		p10.setAttribute("name", "networkColourCode");
		p11.setAttribute("name", "gTargetRac");
		p12.setAttribute("name", "networkControlOrder");
		p13.setAttribute("name", "systemInfoType");
		p1.setTextContent(gsmNeighbour.bcch);
		p2.setTextContent("dcs1800");
		p3.setTextContent(gsmNeighbour.bcc);
		p4.setTextContent(gsmNeighbour.cellId);
		p5.setTextContent("false");
		p6.setTextContent(gsmNeighbour.lac);
		p7.setTextContent("220");
		p8.setTextContent("5");
		p9.setTextContent("2");
		p10.setTextContent(gsmNeighbour.ncc);
		p11.setTextContent(gsmNeighbour.rac);
		p12.setTextContent("RESET");
		p13.setTextContent("none");
		managedObject.appendChild(p1);
		managedObject.appendChild(p2);
		managedObject.appendChild(p3);
		managedObject.appendChild(p4);
		managedObject.appendChild(p5);
		managedObject.appendChild(p6);
		managedObject.appendChild(p7);
		managedObject.appendChild(p8);
		managedObject.appendChild(p9);
		managedObject.appendChild(p10);
		managedObject.appendChild(p11);
		managedObject.appendChild(p12);
		managedObject.appendChild(p13);
		Node firstManagedObject = (Node) getNodeObjectFromXmlDocument("//cmData/managedObject[@class=\"LNCEL\"]");
		firstManagedObject.getParentNode().insertBefore(managedObject, firstManagedObject);
	}

	public void editLncel_cellParameters(LteCell lteCell, String eNodeBId) {
		// This is case when expression represent search for node with 2 specific attributes.
		NodeList pList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"LNCEL\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/LNCEL-" + lteCell.cellInfo.get("lnCellId") + "\"]/p");
		for (int i = 0; i < pList.getLength(); i++) {
			Node pNode = pList.item(i);
			String nameNodeValue = getAttributeValueFromNode(pNode, "name");
			if (nameNodeValue.equals("dlChBw")) {
				pNode.setTextContent(lteCell.cellInfo.get("channelBw"));
			} else if (nameNodeValue.equals("earfcnDL")) {
				pNode.setTextContent(lteCell.cellInfo.get("dlEarfcn"));
			} else if (nameNodeValue.equals("earfcnUL")) {
				String dlEarfcn = lteCell.cellInfo.get("dlEarfcn");
				int ulEarfcn = Integer.valueOf(dlEarfcn) + 18000;
				pNode.setTextContent(String.valueOf(ulEarfcn));
			} else if (nameNodeValue.equals("pMax")) {
				pNode.setTextContent(lteCell.cellInfo.get("maxPower"));
			} else if (nameNodeValue.equals("phyCellId")) {
				pNode.setTextContent(lteCell.cellInfo.get("pci"));
			} else if (nameNodeValue.equals("rootSeqIndex")) {
				pNode.setTextContent(lteCell.cellInfo.get("rootSeqIndex"));
			} else if (nameNodeValue.equals("tac")) {
				pNode.setTextContent(lteCell.cellInfo.get("tac"));
			} else if (nameNodeValue.equals("ulChBw")) {
				pNode.setTextContent(lteCell.cellInfo.get("channelBw"));
			} else if (nameNodeValue.equals("cellName")) {
				pNode.setTextContent(lteCell.cellInfo.get("cellName"));
			}
		}
	}

	public void editGnfl_BcchUnique(String eNodeBId, String lnCellId, Set<String> uniqueBcchOfNeighbours) {
		Node listNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"GNFL\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/LNCEL-" + lnCellId + "/GFIM-1/GNFL-1\"]/list[@name=\"gerArfcnVal\"]");
		removeChildsFrom(listNode);
		for (String bcch : uniqueBcchOfNeighbours) {
			Element p = xmlDocument.createElement("p");
			p.setTextContent(bcch);
			listNode.appendChild(p);
		}
	}

	// To remove existing child nodes from specific node.
	private void removeChildsFrom(Node node) {
		while (node.hasChildNodes())
			node.removeChild(node.getFirstChild());
	}

	public void editLnhog_BcchUnique(String eNodeBId, String lnCellId, Set<String> uniqueBcchOfNeighbours) {
		Node listNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"LNHOG\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/LNCEL-" + lnCellId + "/LNHOG-0\"]/list[@name=\"arfcnValueListGERAN\"]");
		removeChildsFrom(listNode);
		for (String bcch : uniqueBcchOfNeighbours) {
			Element p = xmlDocument.createElement("p");
			p.setTextContent(bcch);
			listNode.appendChild(p);
		}
	}

	public void editLnrelg_CellIdUniquePerCell(String eNodeBId, String lnCellId, String counter,
			GsmNeighbour gsmNeighbour) {
		Element managedObject = xmlDocument.createElement("managedObject");
		managedObject.setAttribute("class", "LNRELG");
		String distName = "MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId + "/LNCEL-" + lnCellId + "/LNRELG-" + counter;
		managedObject.setAttribute("distName", distName);
		managedObject.setAttribute("operation", "create");
		managedObject.setAttribute("version", "LN7.0");
		Element p1 = xmlDocument.createElement("p");
		Element p2 = xmlDocument.createElement("p");
		Element p3 = xmlDocument.createElement("p");
		Element p4 = xmlDocument.createElement("p");
		Element p5 = xmlDocument.createElement("p");
		Element p6 = xmlDocument.createElement("p");
		Element list = xmlDocument.createElement("list");
		Element item = xmlDocument.createElement("item");
		Element p7 = xmlDocument.createElement("p");
		Element p8 = xmlDocument.createElement("p");
		Element p9 = xmlDocument.createElement("p");
		p1.setAttribute("name", "ci");
		p2.setAttribute("name", "eNACCAllowed");
		p3.setAttribute("name", "lac");
		p4.setAttribute("name", "nrControl");
		p5.setAttribute("name", "redirWithSysInfoAllowed");
		p6.setAttribute("name", "srvccAllowed");
		list.setAttribute("name", "plmnId");
		p7.setAttribute("name", "mcc");
		p8.setAttribute("name", "mnc");
		p9.setAttribute("name", "mncLength");
		p1.setTextContent(gsmNeighbour.cellId);
		p2.setTextContent("allowed");
		p3.setTextContent(gsmNeighbour.lac);
		p4.setTextContent("automatic");
		p5.setTextContent("forbidden");
		p6.setTextContent("allowed");
		p7.setTextContent("220");
		p8.setTextContent("5");
		p9.setTextContent("2");
		item.appendChild(p7);
		item.appendChild(p8);
		item.appendChild(p9);
		list.appendChild(item);
		managedObject.appendChild(p1);
		managedObject.appendChild(p2);
		managedObject.appendChild(p3);
		managedObject.appendChild(p4);
		managedObject.appendChild(p5);
		managedObject.appendChild(p6);
		managedObject.appendChild(list);
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"REDRT\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/LNCEL-" + lnCellId + "/REDRT-0\"]");
		Node firstManagedObject = null;
		for (int i = 0; i < managedObjectList.getLength(); i++) {
			firstManagedObject = managedObjectList.item(i);
			if (firstManagedObject.getNodeName().equals("managedObject")) {
				break;
			}
		}
		firstManagedObject.getParentNode().insertBefore(managedObject, firstManagedObject);
	}

	public void editRedrt_BcchUnique(String eNodeBId, String lnCellId, Set<String> uniqueBcchOfNeighbours) {
		Node listNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"REDRT\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/LNCEL-" + lnCellId + "/REDRT-1\"]/list[@name=\"redirGeranArfcnValueL\"]");
		removeChildsFrom(listNode);
		for (String bcch : uniqueBcchOfNeighbours) {
			Element p = xmlDocument.createElement("p");
			p.setTextContent(bcch);
			listNode.appendChild(p);
		}
	}

	public void editRmod_SiteName(String eNodeBId, String counter, String siteName, boolean isSharing) {
		Element managedObject = xmlDocument.createElement("managedObject");
		managedObject.setAttribute("class", "RMOD");
		String distName = "MRBTS-" + eNodeBId + "/RMOD-" + counter;
		managedObject.setAttribute("distName", distName);
		managedObject.setAttribute("operation", "create");
		managedObject.setAttribute("version", "LN7.0");
		Element p1 = xmlDocument.createElement("p");
		Element p2 = xmlDocument.createElement("p");
		Element p3 = xmlDocument.createElement("p");
		Element p4 = xmlDocument.createElement("p");
		Element list = xmlDocument.createElement("list");
		Element item1 = xmlDocument.createElement("item");
		Element p5 = xmlDocument.createElement("p");
		Element p6 = xmlDocument.createElement("p");
		Element p7 = xmlDocument.createElement("p");
		Element item2 = null;
		if (isSharing) {
			item2 = xmlDocument.createElement("item");
			Element p8 = xmlDocument.createElement("p");
			Element p9 = xmlDocument.createElement("p");
			Element p10 = xmlDocument.createElement("p");
			p8.setAttribute("name", "linkId");
			p9.setAttribute("name", "positionInChain");
			p10.setAttribute("name", "sModId");
			p8.setTextContent(counter);
			p9.setTextContent("1");
			p10.setTextContent("2");
			item2.appendChild(p8);
			item2.appendChild(p9);
			item2.appendChild(p10);
		}
		p1.setAttribute("name", "linkSpeed");
		p2.setAttribute("name", "prodCodePlanned");
		p3.setAttribute("name", "moduleLocation");
		p4.setAttribute("name", "climateControlProfiling");
		list.setAttribute("name", "connectionList");
		p5.setAttribute("name", "linkId");
		p6.setAttribute("name", "positionInChain");
		p7.setAttribute("name", "sModId");
		p1.setTextContent("Auto");
		p2.setTextContent("472501A.103");
		p3.setTextContent(siteName);
		p4.setTextContent("Optimized Cooling");
		p5.setTextContent(counter);
		p6.setTextContent("1");
		p7.setTextContent("1");
		item1.appendChild(p5);
		item1.appendChild(p6);
		item1.appendChild(p7);
		list.appendChild(item1);
		if (isSharing) {
			list.appendChild(item2);
		}
		managedObject.appendChild(p1);
		managedObject.appendChild(p2);
		managedObject.appendChild(p3);
		managedObject.appendChild(p4);
		managedObject.appendChild(list);
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"SMOD\" and @distName=\"MRBTS-" + eNodeBId + "/SMOD-1\"]");
		Node firstManagedObject = managedObjectList.item(0);
		firstManagedObject.getParentNode().insertBefore(managedObject, firstManagedObject);
	}

	public void editLteSmod_SiteName(String eNodeBId, String siteName) {
		Node pNode = (Node) getNodeObjectFromXmlDocument("//cmData/managedObject[@class=\"SMOD\" and @distName=\"MRBTS-"
				+ eNodeBId + "/SMOD-1\"]/p[@name=\"moduleLocation\"]");
		pNode.setTextContent(siteName);
	}

	public void editGsmSmod_SiteName(String eNodeBId) {
		Element managedObject = xmlDocument.createElement("managedObject");
		managedObject.setAttribute("class", "SMOD");
		String distName = "MRBTS-" + eNodeBId + "/SMOD-2";
		managedObject.setAttribute("distName", distName);
		managedObject.setAttribute("operation", "create");
		managedObject.setAttribute("version", "LN7.0");
		Element p1 = xmlDocument.createElement("p");
		Element p2 = xmlDocument.createElement("p");
		Element list = xmlDocument.createElement("list");
		Element item1 = xmlDocument.createElement("item");
		Element p3 = xmlDocument.createElement("p");
		Element p4 = xmlDocument.createElement("p");
		Element p5 = xmlDocument.createElement("p");
		Element item2 = xmlDocument.createElement("item");
		Element p6 = xmlDocument.createElement("p");
		Element p7 = xmlDocument.createElement("p");
		Element p8 = xmlDocument.createElement("p");
		Element item3 = xmlDocument.createElement("item");
		Element p9 = xmlDocument.createElement("p");
		Element p10 = xmlDocument.createElement("p");
		Element p11 = xmlDocument.createElement("p");
		p1.setAttribute("name", "syncMaster");
		p2.setAttribute("name", "technology");
		list.setAttribute("name", "linkList");
		p3.setAttribute("name", "linkId");
		p4.setAttribute("name", "linkSpeed");
		p5.setAttribute("name", "radioMaster");
		p6.setAttribute("name", "linkId");
		p7.setAttribute("name", "linkSpeed");
		p8.setAttribute("name", "radioMaster");
		p9.setAttribute("name", "linkId");
		p10.setAttribute("name", "linkSpeed");
		p11.setAttribute("name", "radioMaster");
		p1.setTextContent("false");
		p2.setTextContent("GERAN");
		p3.setTextContent("1");
		p4.setTextContent("Auto");
		p5.setTextContent("false");
		p6.setTextContent("2");
		p7.setTextContent("Auto");
		p8.setTextContent("false");
		p9.setTextContent("3");
		p10.setTextContent("Auto");
		p11.setTextContent("false");
		item1.appendChild(p3);
		item1.appendChild(p4);
		item1.appendChild(p5);
		item2.appendChild(p6);
		item2.appendChild(p7);
		item2.appendChild(p8);
		item3.appendChild(p9);
		item3.appendChild(p10);
		item3.appendChild(p11);
		list.appendChild(item1);
		list.appendChild(item2);
		list.appendChild(item3);
		managedObject.appendChild(p1);
		managedObject.appendChild(p2);
		managedObject.appendChild(list);
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"TRBLCADM\" and @distName=\"MRBTS-" + eNodeBId + "/TRBLCADM-1\"]");
		Node firstManagedObject = managedObjectList.item(0);
		firstManagedObject.getParentNode().insertBefore(managedObject, firstManagedObject);
	}

	public void editFtm_SiteCode(String eNodeBId, String siteCode, String siteName) {
		NodeList pNodeList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"FTM\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/FTM-1\"]/p");
		for (int i = 0; i < pNodeList.getLength(); i++) {
			Node pNode = pNodeList.item(i);
			String nameNodeValue = getAttributeValueFromNode(pNode, "name");
			if (nameNodeValue.equals("systemTitle")) {
				pNode.setTextContent(siteCode);
			} else if (nameNodeValue.equals("locationName")) {
				pNode.setTextContent(siteName);
			}
		}
	}

	public void editIpno(LteSite lteSite) {
		String eNodeBId = lteSite.generalInfo.get("eNodeBId");
		NodeList pList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"IPNO\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/FTM-1/IPNO-1\"]/p");
		for (int i = 0; i < pList.getLength(); i++) {
			Node childNode = pList.item(i);
			String nameNodeValue = getAttributeValueFromNode(childNode, "name");
			if (nameNodeValue.equals("mPlaneIpAddress")) {
				childNode.setTextContent(lteSite.transmission.get("mIp"));
			} else if (nameNodeValue.equals("uPlaneIpAddress") | nameNodeValue.equals("cPlaneIpAddress")) {
				childNode.setTextContent(lteSite.transmission.get("cuDestIp"));
			} else if (nameNodeValue.equals("sPlaneIpAddress")) {
				childNode.setTextContent(lteSite.transmission.get("sIp"));
			} else if (nameNodeValue.equals("btsId")) {
				childNode.setTextContent(eNodeBId);
			}
		}
	}

	public void editTwamp(String cuDestIp) {
		Node pNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"TWAMP\"]/p[@name=\"sourceIpAddress\"]");
		pNode.setTextContent(cuDestIp);
	}

	// TODO Srediti metod uvodjenjem detaljnijih XPath izraza.
	public void editIprt(LteSite lteSite) {
		NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"IPRT\"]");
		Node managedObjectNode = managedObjectList.item(0);
		NodeList childNodeList = managedObjectNode.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			if (childNode.getNodeName().equals("list")) {
				String nameNodeValue = getAttributeValueFromNode(childNode, "name");
				if (nameNodeValue.equals("staticRoutes")) {
					NodeList itemNodeList = childNode.getChildNodes();
					for (int m = 0; m < itemNodeList.getLength(); m++) {
						Node itemNode = itemNodeList.item(m);
						if (itemNode.getNodeName().equals("item")) {
							NodeList pNodeList = itemNode.getChildNodes();
							for (int j = 0; j < pNodeList.getLength(); j++) {
								Node pNode = pNodeList.item(j);
								if (pNode.getNodeName().equals("p")) {
									String pNameNodeValue = getAttributeValueFromNode(pNode, "name");
									if (pNameNodeValue.equals("destIpAddr")) {
										if (pNode.getTextContent().equals("0.0.0.0")) {
											for (int k = 0; k < pNodeList.getLength(); k++) {
												Node pNode2 = pNodeList.item(k);
												if (pNode2.getNodeName().equals("p")) {
													String pNameNodeValue2 = getAttributeValueFromNode(pNode2, "name");
													if (pNameNodeValue2.equals("gateway")) {
														pNode2.setTextContent(lteSite.transmission.get("cuGwIp"));
													}
												}
											}
										} else {
											pNode.setTextContent(lteSite.transmission.get("topIp"));
											for (int k = 0; k < pNodeList.getLength(); k++) {
												Node pNode3 = pNodeList.item(k);
												if (pNode3.getNodeName().equals("p")) {
													String pNameNodeValue3 = getAttributeValueFromNode(pNode3, "name");
													if (pNameNodeValue3.equals("gateway")) {
														pNode3.setTextContent(lteSite.transmission.get("sGwIp"));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void editIvif1(LteSite lteSite) {
		String eNodeBId = lteSite.generalInfo.get("eNodeBId");
		NodeList pNodeList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"IVIF\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/FTM-1/IPNO-1/IEIF-1/IVIF-1\"]/p");
		for (int i = 0; i < pNodeList.getLength(); i++) {
			Node pNode = pNodeList.item(i);
			String nameNodeValue = getAttributeValueFromNode(pNode, "name");
			if (nameNodeValue.equals("vlanId")) {
				pNode.setTextContent(lteSite.transmission.get("sVlanId"));
			} else if (nameNodeValue.equals("localIpAddr")) {
				pNode.setTextContent(lteSite.transmission.get("sIp"));
			} else if (nameNodeValue.equals("netmask")) {
				pNode.setTextContent(lteSite.transmission.get("sSubnet"));
			}
		}
	}

	public void editIvif2(LteSite lteSite) {
		String eNodeBId = lteSite.generalInfo.get("eNodeBId");
		NodeList pNodeList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"IVIF\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/FTM-1/IPNO-1/IEIF-1/IVIF-2\"]/p");
		for (int i = 0; i < pNodeList.getLength(); i++) {
			Node pNode = pNodeList.item(i);
			String nameNodeValue = getAttributeValueFromNode(pNode, "name");
			if (nameNodeValue.equals("vlanId")) {
				pNode.setTextContent(lteSite.transmission.get("cuVlanId"));
			} else if (nameNodeValue.equals("localIpAddr")) {
				pNode.setTextContent(lteSite.transmission.get("cuDestIp"));
			} else if (nameNodeValue.equals("netmask")) {
				pNode.setTextContent(lteSite.transmission.get("cuSubnet"));
			}
		}
	}

	public void editTopf(String eNodeBId, String topIp) {
		Node pNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"TOPF\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
						+ "/FTM-1/TOPB-1/TOPF-1\"]/list[@name=\"topMasters\"]/item/p[@name=\"masterIpAddr\"]");
		pNode.setTextContent(topIp);
	}

	public void editLcell_AnttenaPorts(String cellPorts) {
		String[] antPort = new String[] { "1", "3", "7", "9", "13", "15" };
		if (cellPorts.equals("1-1")) {
			antPort[0] = "1";
			antPort[1] = "7";
			antPort[2] = "3";
			antPort[3] = "9";
			antPort[4] = "5";
			antPort[5] = "11";
		}
		NodeList itemList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"LCELL\"]/list/item");
		for (int i = 0; i < itemList.getLength(); i++) {
			Node itemNode = itemList.item(i);
			NodeList childNodeList = itemNode.getChildNodes();
			for (int j = 0; j < childNodeList.getLength(); j++) {
				Node childNode = childNodeList.item(j);
				if (childNode.getNodeName().equals("p")) {
					String pNameNodeValue = getAttributeValueFromNode(childNode, "name");
					if (pNameNodeValue.equals("antlId")) {
						childNode.setTextContent(antPort[i]);
					}
				}
			}
		}
	}

	public void isFtifUsed(boolean ftifIsUsed) {
		if (ftifIsUsed) {
			Document ftifDocument = createXmlDocumentFromFile(ftifFile);
			Node referenceNode = (Node) getNodeObjectFromXmlDocument(
					"//cmData/managedObject[@class=\"ETHLK\" and contains(@distName,'ETHLK-0-1')]");
			NodeList managedObjectNodeList = (NodeList) getNodeSetObjectFromSpecifiedDocument(ftifDocument,
					"//cmData/managedObject");
			for (int i = 0; i < managedObjectNodeList.getLength(); i++) {
				Element managedObjectElement = (Element) managedObjectNodeList.item(i);
				/*
				 * Create a duplicate node and transfer ownership of the new node into the destination document. Second parameter in importNode()
				 * method, if set to true mean that we copy all children of the node.
				 */
				Node managedObjectCopy = xmlDocument.importNode(managedObjectElement, true);
				/*
				 * Make the new node an actual item in the target document. Pay attention how to insert node at specific level of xml. First we find
				 * some node at level that we want to put into new nodes and then get parent of that reference node and in that parent we add nodes as
				 * children. This will put all nodes to the bottom of parent node children list so in next method we reorder nodes in same parent node
				 * to fit as we want.
				 */
				Node parentNode = referenceNode.getParentNode();
				parentNode.appendChild(managedObjectCopy);
			}
			moveFtifNodesToSpecificPosition();
			editUnit();
		}
	}

	public Document createXmlDocumentFromFile(File file) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setIgnoringComments(true);
		Document xmlDocument = null;
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			xmlDocument = builder.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlDocument;
	}

	private Object getNodeSetObjectFromSpecifiedDocument(Document document, String stringExpression) {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		XPathExpression expression;
		Object result = null;
		try {
			expression = xPath.compile(stringExpression);
			result = expression.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void moveFtifNodesToSpecificPosition() {
		Node referenceNode = (Node) getNodeObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"ETHLK\" and contains(@distName,'ETHLK-0-1')]");
		NodeList ppttNodeList = (NodeList) getNodeSetObjectFromXmlDocument("//cmData/managedObject[@class=\"PPTT\"]");
		NodeList ethlkNodeList = (NodeList) getNodeSetObjectFromXmlDocument(
				"//cmData/managedObject[@class=\"ETHLK\" and contains(@distName,'ETHLK-1-')]");
		for (int i = 0; i < ppttNodeList.getLength(); i++) {
			Node ppttNode = ppttNodeList.item(i);
			referenceNode.getParentNode().insertBefore(ppttNode, referenceNode);
		}
		for (int i = 0; i < ethlkNodeList.getLength(); i++) {
			Element ethlkNode = (Element) ethlkNodeList.item(i);
			referenceNode.getParentNode().insertBefore(ethlkNode, referenceNode);
		}
	}

	private void editUnit() {
		NodeList unitNodeList = (NodeList) getNodeSetObjectFromXmlDocument("//cmData/managedObject[@class=\"UNIT\"]");
		Node unitNode = unitNodeList.item(0);
		NodeList pNodeList = unitNode.getChildNodes();
		for (int i = 0; i < pNodeList.getLength(); i++) {
			Node pNode = pNodeList.item(i);
			if (pNode.getNodeName().equals("p")) {
				String pNameValue = getAttributeValueFromNode(pNode, "name");
				if (pNameValue.equals("unitTypeExpected")) {
					pNode.setTextContent("472311A");
				}
			}
		}
	}

	public void editNumberOfAntenna(int numberOfRfModules) {
		NodeList antlNodeList = (NodeList) getNodeSetObjectFromXmlDocument("//cmData/managedObject[@class=\"ANTL\"]");
		int numberOfAntenna = numberOfRfModules * 6;
		for (int i = numberOfAntenna; i < antlNodeList.getLength(); i++) {
			Node antlNode = antlNodeList.item(i);
			Node parentNode = antlNode.getParentNode();
			/*
			 * When you read an XML document from a file, the whitespaces between tags actually constitute valid DOM nodes, according to the DOM
			 * specification. Therefore, the XML parser treats each such sequence of whitespaces as DOM nodes (of type "TEXT"). The "indent" before
			 * the element and the "carriage return" (and following indent) after it are text nodes. If you remove an element and there's a text node
			 * before or after it, naturally those nodes are not removed. If you want to remove the element, then also remove the text node in front
			 * of it (provided it consists entirely of whitespace).
			 */
			Node prevNode = antlNode.getPreviousSibling();
			if (prevNode != null && prevNode.getNodeType() == Node.TEXT_NODE
					&& prevNode.getNodeValue().trim().length() == 0) {
				parentNode.removeChild(prevNode);
			}
			parentNode.removeChild(antlNode);
		}
	}

	// TODO Napisati test i uraditi refactoring.
	// Nema test u "TestAllConfigFiles-u" jer je samo za BG regiju. Provereno kroz koriscenje programa.
	public void editIpno_Twamp(LteSite lteSite) {
		String siteName = lteSite.generalInfo.get("LocationId");
		boolean isBgArea = false;
		if (siteName.contains("BG")) {
			isBgArea = true;
		}
		if (isBgArea) {
			String eNodeBId = lteSite.generalInfo.get("eNodeBId");
			NodeList managedObjectList = (NodeList) getNodeSetObjectFromXmlDocument(
					"//cmData/managedObject[@class=\"IPNO\" and @distName=\"MRBTS-" + eNodeBId + "/LNBTS-" + eNodeBId
							+ "/FTM-1/IPNO-1\"]");
			Node managedObjectNode = managedObjectList.item(0);
			NodeList childNodeList = managedObjectNode.getChildNodes();
			for (int i = 0; i < childNodeList.getLength(); i++) {
				Node childNode = childNodeList.item(i);
				if (childNode.getNodeName().equals("list")) {
					String childNameValue = getAttributeValueFromNode(childNode, "name");
					if (childNameValue.equals("twampFlag")) {
						NodeList itemNodeList = childNode.getChildNodes();
						for (int j = 0; j < itemNodeList.getLength(); j++) {
							Node itemNode = itemNodeList.item(j);
							if (itemNode.getNodeName().equals("item")) {
								NodeList pNodeList = itemNode.getChildNodes();
								for (int k = 0; k < pNodeList.getLength(); k++) {
									Node pNode = pNodeList.item(k);
									if (pNode.getNodeName().equals("p")) {
										String pNameValue = getAttributeValueFromNode(pNode, "name");
										if (pNameValue.equals("twampIpAddress")) {
											pNode.setTextContent(lteSite.transmission.get("cuDestIp"));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
