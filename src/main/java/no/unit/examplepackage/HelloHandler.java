package no.unit.examplepackage;

import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.util.Map;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.HttpHeaders;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloHandler extends ApiGatewayHandler<Request, Void> {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(HelloHandler.class);
    public static final String NAME_PATH_PARAMETER = "name";
    public static final String THIS_IS_THE_LOG_MESSAGE = "This is the log message";
    
    public HelloHandler(Environment environment) {
        super(Request.class, environment, LOGGER);
    }
    
    @Override
    protected Void processInput(Request input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        String name = input.getName();
        
        String pathParameter = requestInfo.getPathParameter(NAME_PATH_PARAMETER);
        if(!name.equals(pathParameter)){
            logger.error(THIS_IS_THE_LOG_MESSAGE);
            throw new BadRequestException("This is the error message");
        }
        
        addAdditionalHeaders(()->addOtherHeaders(input));
        return null;
    }
    
    private Map<String, String> addOtherHeaders(Request input) {
        return  Map.of(HttpHeaders.LOCATION,"https://some.example.org");
    }
    
    @Override
    protected Integer getSuccessStatusCode(Request input, Void output) {
        return HttpURLConnection.HTTP_OK;
    }
}
