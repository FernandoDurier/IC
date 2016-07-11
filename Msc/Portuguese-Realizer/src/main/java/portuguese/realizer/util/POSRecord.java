package portuguese.realizer.util;

public class POSRecord {
	
	private int nounTags;
	private int verbTags;
	private int adjTags;
	private int advTags;
	
	public POSRecord(int nounTags, int verbTags, int adjTags, int advTags) {
		this.nounTags = nounTags;
		this.verbTags = verbTags;
		this.adjTags = adjTags;
		this.advTags = advTags;
	}
	
	public int getNounTags() {
		return nounTags;
	}

	public int getVerbTags() {
		return verbTags;
	}

	public int getAdjTags() {
		return adjTags;
	}

	public int getAdvTags() {
		return advTags;
	}
	
	public int getMaxTagType() {
		if (nounTags == Math.max(nounTags, Math.max(verbTags, Math.max(adjTags, advTags)))) {
			return 0;
		}
		if (verbTags == Math.max(nounTags, Math.max(verbTags, Math.max(adjTags, advTags)))) {
			return 1;
		}
		if (adjTags == Math.max(nounTags, Math.max(verbTags, Math.max(adjTags, advTags)))) {
			return 2;
		}
		if (advTags == Math.max(nounTags, Math.max(verbTags, Math.max(adjTags, advTags)))) {
			return 3;
		}
		return -1;
	}
	
	public boolean hasOnlyTagType() {
		if (nounTags > 0 && verbTags == 0 && adjTags == 0 && advTags == 0) {
			return true;
		}
		if (nounTags == 0 && verbTags > 0 && adjTags == 0 && advTags == 0) {
			return true;
		}
		if (nounTags == 0 && verbTags == 0 && adjTags > 0 && advTags == 0) {
			return true;
		}
		if (nounTags == 0 && verbTags == 0 && adjTags == 0 && advTags > 0) {
			return true;
		}
		return false;
	}
	
	public void addNounTag() {
		nounTags++;
	}
	
	public void addVerbTag() {
		verbTags++;
	}
	
	public void addAdjTag() {
		adjTags++;
	}

	public void addAdvTag() {
		advTags++;
	}
	
	public String toString() {
		return nounTags + "\t" + verbTags + "\t" + adjTags + "\t" + advTags;
	}
	
	
}
