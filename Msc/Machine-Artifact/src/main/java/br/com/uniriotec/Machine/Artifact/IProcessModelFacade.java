package br.com.uniriotec.Machine.Artifact;

import br.com.uniriotec.process.model.ProcessModel;

/***
 * Interface (contract) that define the public methods acessible from the facade. <br> 
 * Facades that manipulates ProcessModels must override the given methods 
 * defined by this contract. 
 * 
 * @author Raphael Rodrigues
 * @version 1.0.140903
 */
public interface IProcessModelFacade {
	/***
	 * Reads a .json file from within the path received as the parameter.
	 * @param filePath path for the JSON file
	 * @return 
	 * @throws CreateProcessModelException 
	 */
	ProcessModel readProcessModelFromJson(String filePath) throws CreateProcessModelException;
	
	/***
	 * Reads a .json file from within the path received as the parameter.
	 * @param filePath path for the JSON file
	 */
	void readProcessModelFromXml(String filePath) throws CreateProcessModelException;
}
