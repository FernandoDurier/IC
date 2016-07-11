package portuguese.realizer.util;
/*package util;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import data.*;

public class XMLHandler {

	private final String path = "DSynTTemplates/";
	private static XMLHandler xmlHandler = null;
	private XStream xstream;
	
	private XMLHandler() {
		xstream = new XStream(new DomDriver());
		xstream.alias("ExecutableFragment", ExecutableFragment.class);
		xstream.alias("ModifierRecord", ModifierRecord.class);
		//xstream.alias("Pair", com.sun.tools.javac.util.Pair.class);
		xstream.alias("ConditionFragment", ConditionFragment.class);
	}
	
	public static XMLHandler getInstance() {
		if (xmlHandler == null) {
			xmlHandler = new XMLHandler();
		}
		return xmlHandler;
	}
	
	public String convertToXML(Object o) {
		String xml = xstream.toXML(o);
		return xml;
	}
	
	public Object getFromXML(String xml) {
		Object o = null;
		try {
			o = (Object) xstream.fromXML(new FileReader(path + xml));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}
	
}
*/