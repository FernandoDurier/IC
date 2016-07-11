package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;

public class ElementProperties {
	String processid;
	String name;
	String documentation;
	String auditing;
	String monitoring;
	String flat;
	String categories;
	int startquantity;
	int completionquantity;
	String isforcompensation;
	String assignments;
	String callacitivity;
	String tasktypeNone;
	String implementationwebService;
	String resources;
	String messageref;
	String operationref;
	String script;
	String scriptformat;
	String bgcolor;
	String looptypeNone;
	String testbefore;
	String loopcondition;
	String loopmaximum;
	String loopcardinality;
	String loopdatainput;
	String loopdataoutput;
	String inputdataitem;
	String outputdataitem;
	String behaviorall;
	String complexbehaviordefinition;
	String completioncondition;
	String onebehavioreventrefsignal;
	String nonebehavioreventrefsignal;
	String datainputs;
	String dataoutputassociations_catchevents;
	String dataoutputs;
	String outputsets;
	String dataoutputassociations;
	String dataoutput;
	String outputset;
	String bordercolor;
	String properties;
	String properties2;
	String trigger;
	String frequency;
	String inputsets;
	String datainputset;
	String dataoutputset;
	String datainputassociations;
	String operationname;
	String inmessagename;
	String inmsgitemkindInformation;
	String inmsgstructure;
	String inmsgimport;
	String inmsgiscollection;
	String outmessagename;
	String outmsgitemkindInformation;
	String outmsgstructure;
	String outmsgimport;
	String outmsgiscollection;
	String calledelement;
	String time;
	String costs;
	String costcenter;
	ArrayList<String> externaldocuments;
	boolean applyincalc;
	
	String tasktype;
	
	public ElementProperties(){}
	
	public ElementProperties(ElementProperties source){
		this.processid = source.processid;
		this.name = source.name;
		this.documentation = source.documentation;
		this.auditing = source.auditing;
		this.monitoring = source.monitoring;
		this.flat = source.flat;
		this.categories = source.categories;
		this.startquantity = source.startquantity;
		this.completionquantity = source.completionquantity;
		this.isforcompensation = source.isforcompensation;
		this.assignments = source.assignments;
		this.callacitivity = source.callacitivity;
		this.tasktypeNone = source.tasktypeNone;
		this.implementationwebService = source.implementationwebService;
		this.resources = source.resources;
		this.messageref = source.messageref;
		this.operationref = source.operationref;
		this.script = source.script;
		this.scriptformat = source.scriptformat;
		this.bgcolor = source.bgcolor;
		this.looptypeNone = source.looptypeNone;
		this.testbefore = source.testbefore;
		this.loopcondition = source.loopcondition;
		this.loopmaximum = source.loopmaximum;
		this.loopcardinality = source.loopcardinality;
		this.loopdatainput = source.loopdatainput;
		this.loopdataoutput = source.loopdataoutput;
		this.inputdataitem = source.inputdataitem;
		this.outputdataitem = source.outputdataitem;
		this.behaviorall = source.behaviorall;
		this.complexbehaviordefinition = source.complexbehaviordefinition;
		this.completioncondition = source.completioncondition;
		this.onebehavioreventrefsignal = source.onebehavioreventrefsignal;
		this.nonebehavioreventrefsignal = source.nonebehavioreventrefsignal;
		this.datainputs = source.datainputs;
		this.dataoutputassociations_catchevents = source.dataoutputassociations_catchevents;
		this.dataoutputs = source.dataoutputs;
		this.outputsets = source.outputsets;
		this.dataoutputassociations = source.dataoutputassociations;
		this.dataoutput = source.dataoutput;
		this.outputset = source.outputset;
		this.bordercolor = source.bordercolor;
		this.properties = source.properties;
		this.properties2 = source.properties2;
		this.trigger = source.trigger;
		this.frequency = source.frequency;
		this.inputsets = source.inputsets;
		this.datainputset = source.datainputset;
		this.dataoutputset = source.dataoutputset;
		this.datainputassociations = source.datainputassociations;
		this.operationname = source.operationname;
		this.inmessagename = source.inmessagename;
		this.inmsgitemkindInformation = source.inmsgitemkindInformation;
		this.inmsgstructure = source.inmsgstructure;
		this.inmsgimport = source.inmsgimport;
		this.inmsgiscollection = source.inmsgiscollection;
		this.outmessagename = source.outmessagename;
		this.outmsgitemkindInformation = source.outmsgitemkindInformation;
		this.outmsgstructure = source.outmsgstructure;
		this.outmsgimport = source.outmsgimport;
		this.outmsgiscollection = source.outmsgiscollection;
		this.calledelement = source.calledelement;
		this.time = source.time;
		this.costs = source.costs;
		this.costcenter = source.costcenter;
		this.applyincalc = source.applyincalc;
		
		if(source.externaldocuments != null){
			this.externaldocuments = new ArrayList<String>(source.externaldocuments.size());
			for(String document : source.externaldocuments){
				String newDocument = new String(document);
				this.externaldocuments.add(newDocument);
			}
		}
	}
	
	public String getFlat() {
		return flat;
	}
	public void setFlat(String flat) {
		this.flat = flat;
	}
//	public String getInputset() {
//		return inputset;
//	}
//	public void setInputset(String inputset) {
//		this.inputset = inputset;
//	}
	public String getBordercolor() {
		return bordercolor;
	}
	public void setBordercolor(String bordercolor) {
		this.bordercolor = bordercolor;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getProperties2() {
		return properties2;
	}
	public void setProperties2(String properties2) {
		this.properties2 = properties2;
	}
	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
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
//	public String getEventdefinitionref() {
//		return eventdefinitionref;
//	}
//	public void setEventdefinitionref(String eventdefinitionref) {
//		this.eventdefinitionref = eventdefinitionref;
//	}
//	public String getEventdefinitions() {
//		return eventdefinitions;
//	}
//	public void setEventdefinitions(String eventdefinitions) {
//		this.eventdefinitions = eventdefinitions;
//	}
//	public String getDataoutputassociations() {
//		return dataoutputassociations;
//	}
//	public void setDataoutputassociations(String dataoutputassociations) {
//		this.dataoutputassociations = dataoutputassociations;
//	}
	public String getDataoutput() {
		return dataoutput;
	}
	public void setDataoutput(String dataoutput) {
		this.dataoutput = dataoutput;
	}
	public String getOutputset() {
		return outputset;
	}
	public void setOutputset(String outputset) {
		this.outputset = outputset;
	}
	public String getBgcolor() {
		return bgcolor;
	}
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public boolean isApplyincalc() {
		return applyincalc;
	}
	public void setApplyincalc(boolean applyincalc) {
		this.applyincalc = applyincalc;
	}

}
