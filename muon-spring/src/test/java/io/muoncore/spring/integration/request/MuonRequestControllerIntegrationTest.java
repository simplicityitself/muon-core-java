package io.muoncore.spring.integration.request;

import io.muoncore.Muon;
import io.muoncore.future.MuonFuture;
import io.muoncore.protocol.requestresponse.RequestMetaData;
import io.muoncore.protocol.requestresponse.Response;
import io.muoncore.protocol.requestresponse.server.HandlerPredicate;
import io.muoncore.protocol.requestresponse.server.RequestResponseServerHandlerApi;
import io.muoncore.protocol.requestresponse.server.RequestWrapper;
import io.muoncore.spring.Person;
import io.muoncore.spring.annotations.EnableMuonControllers;
import io.muoncore.spring.integration.MockedMuonConfiguration;
import io.muoncore.spring.model.request.TestRequestController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.muoncore.spring.MuonTestUtils.getSampleMuonRequestWrapper;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MuonRequestControllerIntegrationTest.class, loader = AnnotationConfigContextLoader.class)
@Configuration
@EnableMuonControllers(streamKeepAliveTimeout = 500)
@Import(MockedMuonConfiguration.class)
@ComponentScan(basePackages = "io.muoncore.spring.model.request")
public class MuonRequestControllerIntegrationTest {

    public static final Person PETER = new Person(123l, "Peter", 23);
    public static final Person MIKE = new Person(234l, "Mike", 30);
    public static final String QUERY_EXPECTED_PERSON_NAME = "personName";
    @Autowired
    private Muon muon;
    @Autowired
    private TestRequestController testController;

    @Mock
    TestRequestController mockedTestRequestController = mock(TestRequestController.class);

    private ArgumentCaptor<String> resourceNameCaptor;
    private ArgumentCaptor<Class> typeCaptor;
    private ArgumentCaptor<HandlerPredicate> handlerPredicateCaptor;


    private ArgumentCaptor<RequestResponseServerHandlerApi.Handler> handlerCaptor;

    private ArgumentCaptor<Response> responseCaptor;


    @Before
    public void setUp() throws Exception {
        reset(mockedTestRequestController);
        handlerPredicateCaptor = ArgumentCaptor.forClass(HandlerPredicate.class);
        typeCaptor = ArgumentCaptor.forClass(Class.class);
        handlerCaptor = ArgumentCaptor.forClass(RequestResponseServerHandlerApi.Handler.class);
        responseCaptor = ArgumentCaptor.forClass(Response.class);
        testController.setDelegatingMock(mockedTestRequestController);
    }

    @Test
    public void processesMuonQueries() throws Exception {

        verifyMuonQuerySetupProcess();
        int i = findMappingIndex(handlerPredicateCaptor, "/getPerson");

        RequestMetaData requestMetaData = new RequestMetaData("/getPerson", "source", "target");

        assertThat(handlerPredicateCaptor.getAllValues().get(i).matcher().test(requestMetaData), is(true));

        assertThat(typeCaptor.getAllValues().get(i), equalTo(Object.class));
        RequestResponseServerHandlerApi.Handler handler = handlerCaptor.getAllValues().get(i);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", 13);

        when(mockedTestRequestController.getPerson(13)).thenReturn(PETER);

        final RequestWrapper<Map<String, Object>> sampleMuonRequestWrapper = spy(getSampleMuonRequestWrapper(payload));

        handler.handle(sampleMuonRequestWrapper);


        verify(sampleMuonRequestWrapper, times(1)).answer(responseCaptor.capture());

        Response response = responseCaptor.getValue();
        assertThat(response.getPayload(), is(PETER));
        assertThat(response.getStatus(), is(200));
    }

    private int findMappingIndex(ArgumentCaptor<HandlerPredicate> handlerPredicateCaptor, String path) {
        final List<HandlerPredicate> handlerPredicates = handlerPredicateCaptor.getAllValues();
        for (int i = 0; i < handlerPredicates.size(); i++) {
            if (handlerPredicates.get(i).resourceString().equals(path)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Query " + path + " is not registered in muon");
    }

    private void verifyMuonQuerySetupProcess() {
        verify(muon, times(3)).handleRequest(handlerPredicateCaptor.capture(), typeCaptor.capture(), handlerCaptor.capture());
    }

/*
    private MuonResourceEvent<Map> sampleQueryEvent() {
        MuonResourceEvent<Map> queryEvent = new MuonResourceEvent<Map>(URI.create("muon://sample-service/point"));
        queryEvent.setDecodedContent(sampleParameterMap());
        return queryEvent;
    }

    private MuonResourceEvent<Person> sampleCommandEvent() {
        MuonResourceEvent<Person> queryEvent = new MuonResourceEvent<Person>(URI.create("muon://sample-service/point"));
        queryEvent.setDecodedContent(MIKE);
        return queryEvent;
    }

    private HashMap<Object, Object> sampleParameterMap() {
        HashMap<Object, Object> sampleParametersMap = new HashMap<>();
        sampleParametersMap.put("personName", QUERY_EXPECTED_PERSON_NAME);
        return sampleParametersMap;
    }
*/

}
