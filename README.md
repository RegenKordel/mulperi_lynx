# mulperi

This service was created as a result of the OpenReq project funded by the European Union Horizon 2020 Research and Innovation programme under grant agreement No 732463.



## The following technologies are used:
  Spring Boot
	Maven 
  

	
## Public APIs

The API is documented by using Swagger2: http://217.172.12.199:9202/swagger-ui.html

## Functionalities 

Mulperi is a service that is used in the OpenReq infrastructure to generate appropriate knowledge reprensentation for inference in KeljuCaaS. 
 For further details, see the swagger documentation http://217.172.12.199:9202/swagger-ui.html.

## How to Install

Run the compiled jar file, e.g., nohup java -jar Mulperi-1.9.jar.

Mulpeeri uses port 9202 that needs to be open to in order that the Swagger page can be accessed. Mulperi also connects to KeljuCaaS services of OpenReq in port 9205.

## How to Contribute
See the OpenReq Contribution Guidelines [here](https://github.com/OpenReqEU/OpenReq/blob/master/CONTRIBUTING.md).

## License

Free use of this software is granted under the terms of the EPL version 2 (EPL2.0).
