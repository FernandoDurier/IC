package general.language.common.realizer;

import general.language.common.label.analysis.ILabelHelper;

public class TemplateTextFormatter implements ITextFormatter {
	
	private ILabelHelper lHelper;
	
	public TemplateTextFormatter(ILabelHelper label){
		this.lHelper = label;	
	}

	public String removeFormatting(String text) {
		String unformattedText = text.replaceAll(Identation.LINE_BREAK.toString(), "");
		unformattedText = unformattedText.replaceAll(Identation.TABULATION.toString(), "");
		
		return unformattedText.trim();
	}

	public String removeStopWords(String text) {
		return lHelper.removeStopWords(text);
	}

	public String replaceReferringExpression(String text) {
		return text;
	}

	public String replaceSenteceAggregator(String text) {
		return text;
	}

	public String removeBullets(String text) {
		return text.replaceAll(Identation.BULLET.toString(), "");
	}
}
