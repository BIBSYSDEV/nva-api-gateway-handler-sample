package no.unit.examplepackage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.logutils.LogUtils;
import nva.commons.logutils.TestAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class HelloHandlerTest {
    
    public static final String ALLOW_ALL_ORIGIN = "*";
    public static final String DEFAULT_NAME = "Orestis";
    private Environment environment;
    private Context context;
    private ByteArrayOutputStream output;
    
    @BeforeEach
    public void init() {
        environment = mock(Environment.class);
        when(environment.readEnv(anyString())).thenReturn(ALLOW_ALL_ORIGIN);
        context = mock(Context.class);
        output = new ByteArrayOutputStream();
    }
    
    @Test
    public void handlerReturnOkWhenPathParameterAndBodyParameterHaveSameValue() throws IOException {
        HelloHandler helloHandler = new HelloHandler(environment);
        InputStream input = defaultQuery(DEFAULT_NAME);
        helloHandler.handleRequest(input, output, context);
        GatewayResponse<Response> response = GatewayResponse.fromOutputStream(output);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
    }
    
    @Test
    public void handlerReturnsBadRequestWhenPathParameterIsNotEqualToRequestName() throws IOException {
        TestAppender appender = LogUtils.getTestingAppender(HelloHandler.class);
        HelloHandler helloHandler = new HelloHandler(environment);
        InputStream input = defaultQuery("somename");
        helloHandler.handleRequest(input, output, context);
        GatewayResponse<Problem> response = GatewayResponse.fromOutputStream(output);
       
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_BAD_REQUEST)));
        assertThat(appender.getMessages(),containsString(HelloHandler.THIS_IS_THE_LOG_MESSAGE));
    }
    
    private InputStream defaultQuery(String bodyName) throws com.fasterxml.jackson.core.JsonProcessingException {
        Request body = new Request();
        body.setName(bodyName);
        return new HandlerRequestBuilder<Request>(JsonUtils.objectMapper)
                   .withBody(body)
                   .withPathParameters(Map.of("name", DEFAULT_NAME))
                   .build();
    }
}
