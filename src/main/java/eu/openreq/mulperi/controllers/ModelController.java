package eu.openreq.mulperi.controllers;

import java.util.Collection;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.openreq.mulperi.models.kumbang.Feature;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.mulson.Requirement;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.reqif.SpecObject;
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.CaasClient;
import eu.openreq.mulperi.services.FormatTransformerService;
import eu.openreq.mulperi.services.KumbangModelGenerator;
import eu.openreq.mulperi.services.ReleaseXMLParser;
import eu.openreq.mulperi.services.ReqifParser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("models")
public class ModelController {

	private FormatTransformerService transform = new FormatTransformerService();
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();

	@Value("${mulperi.caasAddress}")
	private String caasAddress;

	@Autowired
	private ParsedModelRepository parsedModelRepository;

	/**
	 * Get all saved models
	 * @return
	 */
	@ApiOperation(value = "Get saved models",
			notes = "Get all saved models",
			response = ParsedModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success")}) 
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<ParsedModel> modelList() {
		return parsedModelRepository.findAll();
	}

	/**
	 * Get single model as FeatureSelection for selecting features
	 * @param modelName
	 * @return
	 */
	@ApiOperation(value = "Get the structure of a model",
			notes = "Get single model as FeatureSelection for selecting features",
			response = FeatureSelection.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found")}) 
	@CrossOrigin
	@RequestMapping(value = "/{model}", method = RequestMethod.GET)
	public ResponseEntity<?> getModel(@PathVariable("model") String modelName) {

		ParsedModel model = this.parsedModelRepository.findFirstByModelName(modelName);

		if(model == null) {
			return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(transform.parsedModelToFeatureSelection(model), HttpStatus.OK);
	}

	/**
	 * Import a model in MulSON format
	 * @param requirements
	 * @return
	 */
	@ApiOperation(value = "Import MulSON model",
			notes = "Import a model in MulSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "/mulson", method = RequestMethod.POST)
	public ResponseEntity<?> chocoMulson(@RequestBody List<Requirement> requirements) {

		String name = generateName(requirements);

		ParsedModel pm = transform.parseMulson(name, requirements);

		return sendModelToCaasAndSave(pm, caasAddress);
	}

	/**
	 * Import a model in checkForConsistency(Project) XML format for release planning
	 * @param projectXML
	 * @return
	 */
	@ApiOperation(value = " Import a model in checkForConsistency(Project) XML format for release planning",
			notes = " Import a model in checkForConsistency(Project) XML format for release planning",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "/project", method = RequestMethod.POST)
	public ResponseEntity<?> projectToKumbang(@RequestBody String projectXML) {
		ReleasePlan releasePlan= null;
		try {
			releasePlan 
			= ReleaseXMLParser.parseProjectXML(projectXML);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
			ParsedModel pm = releasePlan.getParsedModel();
			System.out.println(pm);
			return  sendModelToCaasAndSave(releasePlan.getParsedModel(), caasAddress);
//			FormatTransformerService formatTransformerService = new FormatTransformerService(); 
//			FeatureSelection featetsel = formatTransformerService.parsedModelToFeatureSelection(pm);
//			System.out.println(featetsel.toString());
//			//return sendModelToCaasAndSave(releasePlan.getParsedModel(), caasAddress);
//			return retVal;

		}
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}
	}


	/**
	 * Import a model in ReqIF format
	 * @param reqifXML
	 * @return
	 */
	@ApiOperation(value = "Import ReqIF model",
			notes = "Import a model in ReqIF format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "/reqif", method = RequestMethod.POST, consumes="application/xml")
	public ResponseEntity<?> reqif(@RequestBody String reqifXML) {
		ReqifParser parser = new ReqifParser();
		String name = generateName(reqifXML);

		try {
			Collection<SpecObject> specObjects = parser.parse(reqifXML).values();
			ParsedModel pm = transform.parseReqif(name, specObjects);
			return sendModelToCaasAndSave(pm, caasAddress);
		} catch (Exception e) { //ReqifParser error
			return new ResponseEntity<>("Syntax error in ReqIF\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	private String generateName(Object object) {
		int hashCode = object.hashCode();
		return "ID" + (hashCode > 0 ? hashCode : "_" + Math.abs(hashCode)); 
		//replace - with _, since Kumbang doesn't like hyphens
	}

	private ResponseEntity<String> sendModelToCaasAndSave(ParsedModel pm, String caasAddress) {

		pm.rolesForConstraints();

		String kumbangModel = kumbangModelGenerator.generateKumbangModelString(pm);
		CaasClient client = new CaasClient();

		String result = new String();

		try {
			result = client.uploadConfigurationModel(pm.getModelName(), kumbangModel, caasAddress);
		} catch(IntrospectionException e) {
			return new ResponseEntity<>("Impossible model\n\n" + e.getMessage(), HttpStatus.CONFLICT);
		} catch(DataFormatException e) {
			return new ResponseEntity<>("Syntax error\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			return new ResponseEntity<>("Configuration model upload failed.\n\n" + e.toString(), HttpStatus.BAD_REQUEST);
		} 

		//Save model to database if send successful
		for (Feature f: pm.getFeatures()) {
			System.out.println(f.getType()+ "," +  f.getParent());
		}
		parsedModelRepository.save(pm);

		//return new ResponseEntity<>("Configuration model upload successful.\n\n - - - \n\n" + result, HttpStatus.CREATED);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

}