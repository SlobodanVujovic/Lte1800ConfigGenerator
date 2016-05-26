package lte1800ConfigGenerator;

public class GsmNeighbour {
	String cellName = "dummyData", cellId = "dummyData", bcc = "dummyData", ncc = "dummyData", lac = "dummyData",
			bcch = "dummyData", rac = "dummyData";

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bcc == null) ? 0 : bcc.hashCode());
		result = prime * result + ((bcch == null) ? 0 : bcch.hashCode());
		result = prime * result + ((cellId == null) ? 0 : cellId.hashCode());
		result = prime * result + ((lac == null) ? 0 : lac.hashCode());
		result = prime * result + ((ncc == null) ? 0 : ncc.hashCode());
		result = prime * result + ((rac == null) ? 0 : rac.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GsmNeighbour))
			return false;
		GsmNeighbour other = (GsmNeighbour) obj;
		if (bcc == null) {
			if (other.bcc != null)
				return false;
		} else if (!bcc.equals(other.bcc))
			return false;
		if (bcch == null) {
			if (other.bcch != null)
				return false;
		} else if (!bcch.equals(other.bcch))
			return false;
		if (cellId == null) {
			if (other.cellId != null)
				return false;
		} else if (!cellId.equals(other.cellId))
			return false;
		if (lac == null) {
			if (other.lac != null)
				return false;
		} else if (!lac.equals(other.lac))
			return false;
		if (ncc == null) {
			if (other.ncc != null)
				return false;
		} else if (!ncc.equals(other.ncc))
			return false;
		if (rac == null) {
			if (other.rac != null)
				return false;
		} else if (!rac.equals(other.rac))
			return false;
		return true;
	}

}
