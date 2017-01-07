package bpm2nlg;

import portuguese.realizer.PortugueseLanguageProcessor;
import portuguese.realizer.PortugueseSurfaceRealizer;
import portuguese.realizer.label.analysis.PortugueseLabelDeriver;
import portuguese.realizer.label.analysis.PortugueseLabelHelper;
import portuguese.realizer.label.analysis.PortugueseLabelProperties;
import english.realizer.EnglishSurfaceRealizer;
import english.realizer.label.analysis.EnglishLabelDeriver;
import english.realizer.label.analysis.EnglishLabelHelper;
import english.realizer.label.analysis.EnglishLabelProperties;
import spanish.realizer.label.analysis.SpanishLabelProperties;
import spanish.realizer.label.analysis.SpanishLabelHelper;
import spanish.realizer.label.analysis.SpanishLabelDeriver;
import spanish.realizer.SpanishLanguageProcessor;
import spanish.realizer.SpanishSurfaceRealizer;
import general.language.common.SupportedLanguages;
import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.ILabelProperties;
import general.language.common.localization.Localization;
import general.language.common.localization.LocalizationException;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.ISurfaceRealizer;

public class LanguageConfig {
	private ILabelHelper labelHelper;
	private ILabelDeriver labelDeriver;
	private ISurfaceRealizer surfaceRealizer;
	private INaturalLanguageProcessor languageProcessor;
	private static SupportedLanguages CURRENT_LANGUAGE;
	private static LanguageConfig instance;

	private LanguageConfig() {
	}

	public static LanguageConfig getInstance() {
		if (instance == null)
			instance = new LanguageConfig();

		return instance;
	}

	public ILabelHelper getLabelHelper() {
		return labelHelper;
	}

	public INaturalLanguageProcessor getLanguageProcessor() {
		return languageProcessor;
	}

	public ILabelDeriver getLabelDeriver() {
		return labelDeriver;
	}

	public ISurfaceRealizer getSurfaceRealizer() {
		return surfaceRealizer;
	}

	public SupportedLanguages getCurrentLanguage() {
		return CURRENT_LANGUAGE;
	}

	public ILabelProperties createNewLabelProperties() {
		ILabelProperties labelProperties = null;
		switch (CURRENT_LANGUAGE) {
		case PORTUGUESE:
			labelProperties = new PortugueseLabelProperties();
			break;
		case ENGLISH:
			labelProperties = new EnglishLabelProperties();
                        break;
                case SPANISH:
                        labelProperties = new SpanishLabelProperties();
                        break;
                }

		return labelProperties;
	}

	public void setCurrentLanguage(SupportedLanguages currentLanguage) {
		if (currentLanguage != null
				&& !currentLanguage.equals(CURRENT_LANGUAGE)) {
			try {
				switch (currentLanguage) {
				case ENGLISH:
					labelHelper = new EnglishLabelHelper();
					labelDeriver = new EnglishLabelDeriver(
							(EnglishLabelHelper) labelHelper);
					languageProcessor = null;
					surfaceRealizer = new EnglishSurfaceRealizer(labelHelper,
							languageProcessor);
					break;

				case GERMAN:
					labelHelper = new EnglishLabelHelper();
					labelDeriver = new EnglishLabelDeriver(
							(EnglishLabelHelper) labelHelper);
					languageProcessor = null;
					surfaceRealizer = new EnglishSurfaceRealizer(labelHelper,
							languageProcessor);
					break;

				case PORTUGUESE:
					labelHelper = new PortugueseLabelHelper();
					labelDeriver = new PortugueseLabelDeriver(labelHelper);
					languageProcessor = new PortugueseLanguageProcessor();
					surfaceRealizer = new PortugueseSurfaceRealizer(
							labelHelper, languageProcessor);
					break;
                                        
                                case SPANISH:
					labelHelper = new SpanishLabelHelper();
					labelDeriver = new SpanishLabelDeriver(labelHelper);
					languageProcessor = new SpanishLanguageProcessor();
					surfaceRealizer = new SpanishSurfaceRealizer(
							labelHelper, languageProcessor);
					break; 
				}

				CURRENT_LANGUAGE = currentLanguage;
				Localization.getInstance().setLocalization(currentLanguage);
			} catch (LocalizationException e) {
				System.err
						.println("\n*********************** LOCALIZATION MODULE ERROR - "
								+ new java.util.Date()
								+ "***********************\n");
				System.err.println(e.getMessage());
				System.err
						.println("*********************************************************************************************************\n");
			} catch (Exception e) {
				// TODO: Do the exception treatment...
			}
		}
	}

	// This code was used by the 'TextToIntermediateConverter' class, but the
	// problem was that the language was reffered to by numbers (static int
	// PORTUGUES = 1, for example)
	/*
	 * public static String[] YES = {"yes", "ja", "sim"}; public static String[]
	 * NO = {"no", "nein", "n�o"}; public static String[] OTHERWISE =
	 * {"otherwise", "andernfalls", "sen�o"};
	 */
}
