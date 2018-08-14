package eu.openreq.mulperi.graveyard;
//package eu.openreq.mulperi.graveyard;
//

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.openreq.mulperi.models.kumbang.Feature;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.services.CaasClient;

//public class MulperiController {

/*@Autowired
private ParsedModelRepository parsedModelRepository;
*/

/**
 * Get all saved models
 * @return
 */
/*@ApiOperation(value = "Get saved models",
		notes = "Get all saved models",
	4	response = ParsedModel.class)
@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Success")}) 
@RequestMapping(value = "", method = RequestMethod.GET)
public List<ParsedModel> modelList() {
	return parsedModelRepository.findAll();
}
*/

/**
 * Get single model as FeatureSelection for selecting features
 * @param modelName
 * @return
 */
/*@ApiOperation(value = "Get the structure of a model",
		notes = "Get single model as FeatureSelection for selecting features",
		response = FeatureSelection.class)
@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "Success"),
		@ApiResponse(code = 400, message = "Failure, ex. model not found")}) 
@CrossOrigin
@RequestMapping(value = "{model}", method = RequestMethod.GET)
public ResponseEntity<?> getModel(@PathVariable("model") String modelName) {

	ParsedModel model = this.parsedModelRepository.findFirstByModelName(modelName);

	if(model == null) {
		return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
	}

	return new ResponseEntity<>(transform.parsedModelToFeatureSelection(model), HttpStatus.OK);
}
*/

////	/**
////	 * Import a model in MulSON format
////	 * @param requirements
////	 * @return
////	 * @throws JSONException 
////	 * @throws ReleasePlanException 
////	 * @throws ParserConfigurationException 
////	 * @throws IOException 
////	 */
////	@ApiOperation(value = "Import MulSON model",
////			notes = "Import a model in MulSON format",
////			response = String.class)
////	@ApiResponses(value = { 
////			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
////			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
////			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
////	@RequestMapping(value = "mulson", method = RequestMethod.POST)
////	public ResponseEntity<?> chocoMulson(@RequestBody String requirements) throws ReleasePlanException, JSONException, IOException, ParserConfigurationException {
////		
////		//String name = generateName(requirements);
////		System.out.println("Requirements received from Milla");
////		
////		//ParsedModel pm = transform.parseMulson(name, requirements);
////		ParsedModel pm = new ParsedModel();
////
////		
////	//	return sendModelToCaasAndSave(pm, caasAddress);
////		try {
////			return new ResponseEntity<>("Requirements received: " + requirements, HttpStatus.ACCEPTED);
////		}
////		catch (Exception e) {
////			return new ResponseEntity<>("Error", HttpStatus.EXPECTATION_FAILED); //change to something else?
////		}
////	}

//public ResponseEntity<String> sendModelToCaasAndSave(ParsedModel pm, String caasAddress) {
//
//	pm.rolesForConstraints();
//
//	//String kumbangModel = kumbangModelGenerator.generateKumbangModelString(pm);
//	CaasClient client = new CaasClient();
//
//	String result = new String();
//
////	try {
////		//result = client.uploadConfigurationModel(pm.getModelName(), kumbangModel, caasAddress+"/generateModel");
////	} catch(IntrospectionException e) {
////		return new ResponseEntity<>("Impossible model\n\n" + e.getMessage(), HttpStatus.CONFLICT);
////	} catch(DataFormatException e) {
////		return new ResponseEntity<>("Syntax error\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
////	} catch(Exception e) {
////		return new ResponseEntity<>("Configuration model upload failed.\n\n" + e.toString(), HttpStatus.BAD_REQUEST);
////	} 
//
//	//Save model to database if send successful
//	for (Feature f: pm.getFeatures()) {
//		System.out.println(f.getType()+ "," +  f.getParent());
//	}
//	parsedModelRepository.save(pm);
//
//	//return new ResponseEntity<>("Configuration model upload successful.\n\n - - - \n\n" + result, HttpStatus.CREATED);
//	return new ResponseEntity<>(result, HttpStatus.CREATED);
//}
//
//}
