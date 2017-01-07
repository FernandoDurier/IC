package spanish.realizer;

public class LanguageData
{
	public static final String[] masculineSuffixes = {"ob","rc","d","f","g","h","j","m","n","o","p","q","r","s","t","v","w","ez","on"};
	public static final String[] feminineSuffixes = {"a","iz","x","y","mabel","nabel"};

	public static final String[] verbalPrefixes = {"re","ha","mo","co","ir","se","en","cr","ed","ve","des"};

	public static String[] noArticleprepositions = {" después ",
	 " anti ", " entre ", " de la ", " de ", " en el ", " el ",
	 " en ", " esta ", " este ", " esto ", " por "," salvo ",
	 " detrás "," con ", " contra ", " durante ", " incluso ", " para "};

	public static String[] articlePrepositions = {
		" a "," ante "," bajo "," desde "," hasta "," sobre ", " tras "};

	public static final String[] defArticles = {"el","los","la","las"};
	public static final String[] indefArticles = {"un","unos","una","unas"};

	public  static final int GENDER_MAS = 0;
	public  static final int GENDER_FEM = 2;
}
