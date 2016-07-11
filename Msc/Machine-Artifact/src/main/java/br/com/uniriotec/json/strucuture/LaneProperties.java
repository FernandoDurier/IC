package br.com.uniriotec.json.strucuture;

public class LaneProperties {
	
	String name;
	String documentation;
	String auditing;
	String monitoring;
	String flat;
	String parentpool;
	String parentlane;
	String showcaption;
	String bgcolor;
	String bordercolor;
	
	public String getFlat() {
		return flat;
	}
	public void setFlat(String flat) {
		this.flat = flat;
	}
	public String getShowcaption() {
		return showcaption;
	}
	public void setShowcaption(String showcaption) {
		this.showcaption = showcaption;
	}
	public String getBordercolor() {
		return bordercolor;
	}
	public void setBordercolor(String bordercolor) {
		this.bordercolor = bordercolor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentation() {
		return documentation;
	}
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	public String getAuditing() {
		return auditing;
	}
	public void setAuditing(String auditing) {
		this.auditing = auditing;
	}
	public String getMonitoring() {
		return monitoring;
	}
	public void setMonitoring(String monitoring) {
		this.monitoring = monitoring;
	}
	public String getParentpool() {
		return parentpool;
	}
	public void setParentpool(String parentpool) {
		this.parentpool = parentpool;
	}
	public String getParentlane() {
		return parentlane;
	}
	public void setParentlane(String parentlane) {
		this.parentlane = parentlane;
	}
	public boolean isShowcaption() {
		return showcaption.equalsIgnoreCase("true");
	}
	public String getBgcolor() {
		return bgcolor;
	}
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	
	
}
