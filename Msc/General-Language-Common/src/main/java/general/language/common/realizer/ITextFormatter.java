package general.language.common.realizer;

public interface ITextFormatter {
	String removeFormatting(String text);
	String removeBullets(String text);
	String removeStopWords(String text);
	String replaceReferringExpression(String text);
	String replaceSenteceAggregator(String text);
}
