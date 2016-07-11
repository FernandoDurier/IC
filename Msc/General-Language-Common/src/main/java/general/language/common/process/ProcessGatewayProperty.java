package general.language.common.process;

import java.util.List;

import general.language.common.realizer.RealizedText;

public class ProcessGatewayProperty extends ProcessProperty {

	private String gatewayDescription;
	private ProcessElementType gatewayType;
	private List<RealizedText> mainBranchSenteces;
	
	public String getGatewayDescription() {
		return gatewayDescription;
	}

	public void setGatewayDescription(String gatewayDescription) {
		this.gatewayDescription = gatewayDescription;
	}

	public List<RealizedText> getMainBranchSenteces() {
		return mainBranchSenteces;
	}

	public void setMainBranchSenteces(List<RealizedText> mainBranchSenteces) {
		this.mainBranchSenteces = mainBranchSenteces;
	}
	
	public ProcessGatewayProperty(ProcessElementType gatewayType){
		this.gatewayType = gatewayType;
	}
	
	@Override
	public ProcessElementType getElementType() {
		return gatewayType;
	}
}
