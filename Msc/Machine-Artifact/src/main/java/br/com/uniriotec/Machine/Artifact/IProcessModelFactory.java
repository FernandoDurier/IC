package br.com.uniriotec.Machine.Artifact;

import br.com.uniriotec.process.model.ProcessModel;

/***
 * Interface (contract) for factories of {@linkplain ProcessModel}. <br> 
 * Any factory for process models must implement the operations defined in this contract.
 * 
 * @author Raphael Rodrigues
 * @version 1.0.20140903
 */
public interface IProcessModelFactory {
	/***
	 * Creates a empty {@linkplain ProcessModel}.
	 * @return new {@linkplain ProcessModel}
	 */
	ProcessModel createProcessModel(String processModelName);
	
	/***
	 * Creates a {@linkplain ProcessModel} from the file within the given path.
	 * @param filePath path for the JSON file
	 * @return new {@linkplain ProcessModel} with the informations gathered from the JSON file.
	 */
	ProcessModel createProcessModelFromJson(String filePath) throws CreateProcessModelException;
	
	/***
	 * Creates a {@linkplain ProcessModel} from the file within the given path.
	 * @param filePath path for the XML file
	 * @return new {@linkplain ProcessModel} with the informations gathered from the XML file.
	 */
	ProcessModel createProcessModelFromXml(String filePath) throws CreateProcessModelException;
}
