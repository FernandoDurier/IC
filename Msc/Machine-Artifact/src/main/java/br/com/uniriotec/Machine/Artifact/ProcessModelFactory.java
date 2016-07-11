package br.com.uniriotec.Machine.Artifact;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import br.com.uniriotec.json.JsonReader;
import br.com.uniriotec.json.strucuture.Doc;
import br.com.uniriotec.process.model.ProcessModel;

import com.google.gson.Gson;

public class ProcessModelFactory implements IProcessModelFactory {
	private static final Logger logger = Logger.getLogger(
			ProcessModelFactory.class.getSimpleName());
	
	public ProcessModel createProcessModelFromJson(String filePath)
	throws CreateProcessModelException {
		logger.info(String.format("Creating the process model from the .json file " +
				" located at '%s' ...", filePath));
		return createProcessModelFromJsonFile(filePath);
	}

	public ProcessModel createProcessModel(String processModelName) {
		logger.info("Creating an empty process model...");
		Random randomNumber = new Random();
		return new ProcessModel(randomNumber.nextInt(), processModelName);
	}

	public ProcessModel createProcessModelFromXml(String filePath) 
	throws CreateProcessModelException {
		throw new CreateProcessModelException(
				"METHOD 'createProcessModelFromXml' NOT IMPLEMENTED!");
	}
	
	/**
	 * Loads the JSON file located within the given path.
	 * 
	 * @throws CreateProcessModelException if some problem occur while trying 
	 * to create the model from the file.
	 */
	private ProcessModel createProcessModelFromJsonFile(String filePath) 
	throws CreateProcessModelException {
		ProcessModel processModel = null;
		
		Gson gson = new Gson();
		try{
			logger.info("...starting to parse the file: Converting to " +
					"String through Gson API...");
			Doc modelDoc = gson.fromJson(JsonReader.getJsonStringFromFile(filePath), 
					Doc.class);
			if (modelDoc.getChildShapes() != null) {
				logger.info("...parsing completed! Now assembling the " +
						"ProcessModel instance from the String...");
				JsonReader reader = new JsonReader(modelDoc);
				reader.getIntermediateProcessFromFile();
				processModel = reader.getProcessModelFromIntermediate(
						"Process Model");
				
				processModel.normalize();
				processModel.normalizeEndEvents();
				processModel.setLanguage(modelDoc.getProps().getLanguage().trim());
				logger.info("...assemblying completed! " +
						"New ProcessModel instance created succesuful!");
			}
		} catch(IOException ioException){
			String error = "There was a problem while trying to parse the " +
					"JSON file to a String using the Gson API and the process " +
					"model could not be created.";
			logger.error(error, ioException);
			throw new CreateProcessModelException(error, ioException);
		} catch (TransformerException e) {
			String error = "There was a problem while trying to assemble the " +
					"process model from the String and the process model " +
					"could not be created.";
			logger.error(error, e);
			throw new CreateProcessModelException(error, e);
		} catch (ParserConfigurationException e) {
			String error = "There was a problem while trying to assemble the " +
			"process model from the String and the process model " +
			"could not be created.";
			logger.error(error, e);
			throw new CreateProcessModelException(error, e);
		}
		
		return processModel;
	}
}
