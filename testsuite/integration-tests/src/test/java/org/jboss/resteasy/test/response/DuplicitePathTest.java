package org.jboss.resteasy.test.response;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteApplicationOne;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteApplicationTwo;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteResourceOne;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteResourceTwo;
import org.jboss.resteasy.test.response.resource.DuplicitePathMethodResource;
import org.jboss.resteasy.test.response.resource.DuplicitePathNoDupliciteApplication;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-3459
 * @tpSince RESTEasy 3.0.17
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Tag("NotForBootableJar")
public class DuplicitePathTest {
    static ResteasyClient client;

    /**
     * Init servlet warning count ( WFLYUT0101: Duplicate servlet mapping /a/* found )
     */
    private static int initServletWarningsCount;

    /**
     * Get RESTEasy warning count
     */
    private static int getWarningCount() {
        return TestUtil.getWarningCount("RESTEASY002142", false, DEFAULT_CONTAINER_QUALIFIER);
    }

    /**
     * Gets servlet warning count
     * Warning comes from server (outside of the resteasy)
     * Example: WFLYUT0101: Duplicate servlet mapping /a/* found
     */
    private static int getServletMappingWarningCount() {
        return TestUtil.getWarningCount("WFLYUT0101", false, DEFAULT_CONTAINER_QUALIFIER);
    }

    @Deployment
    public static Archive<?> deploySimpleResource() {
        initServletWarningsCount = getServletMappingWarningCount();
        WebArchive war = ShrinkWrap.create(WebArchive.class, DuplicitePathTest.class.getSimpleName() + ".war");
        war.addClass(DuplicitePathDupliciteApplicationOne.class);
        war.addClass(DuplicitePathDupliciteApplicationTwo.class);
        war.addClass(DuplicitePathDupliciteResourceOne.class);
        war.addClass(DuplicitePathDupliciteResourceTwo.class);
        war.addClass(DuplicitePathMethodResource.class);
        war.addClass(DuplicitePathNoDupliciteApplication.class);
        return war;
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DuplicitePathTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check that warning message was logged, if client makes request to path,
     *                that is handled by two methods in two end-point in two application classes
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicationTwoAppTwoResourceSameMethodPath() throws Exception {
        WebTarget base = client.target(generateURL("/a/b/c"));
        Response response = null;
        try {
            response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String strResponse = response.readEntity(String.class);
            Assertions.assertTrue(strResponse.equals(DuplicitePathDupliciteResourceOne.DUPLICITE_RESPONSE)
                    || strResponse.equals(DuplicitePathDupliciteResourceTwo.DUPLICITE_RESPONSE),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(
                1, getServletMappingWarningCount() - initServletWarningsCount,
                TestUtil.getErrorMessageForKnownIssue("RESTEASY-1445", "Wrong count of warnings in server log"));
    }

    /**
     * @tpTestDetails Check that warning message was logged, if client makes request to path,
     *                that is handled by two methods in two end-point in two application classes
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicationMoreAccepts() throws Exception {
        int initWarningsCount = getWarningCount();
        WebTarget base = client.target(generateURL("/f/g/i"));
        Response response = null;
        try {
            response = base.request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String strResponse = response.readEntity(String.class);
            Assertions.assertTrue(strResponse.equals(DuplicitePathMethodResource.NO_DUPLICITE_RESPONSE),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(0, getWarningCount() - initWarningsCount,
                TestUtil.getErrorMessageForKnownIssue("JBEAP-3459", "Wrong count of warnings in server log"));
    }

    /**
     * @tpTestDetails Check that warning message was logged, if client makes request to path,
     *                that is handled by two methods in two end-point in two application classes
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicationMoretypes() throws Exception {
        int initWarningsCount = getWarningCount();
        WebTarget base = client.target(generateURL("/f/g/j"));
        Response response = null;
        try {
            response = base.request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String strResponse = response.readEntity(String.class);
            Assertions.assertTrue(strResponse.equals(DuplicitePathMethodResource.DUPLICITE_TYPE_GET),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(0, getWarningCount() - initWarningsCount,
                TestUtil.getErrorMessageForKnownIssue("JBEAP-3459", "Wrong count of warnings in server log"));
    }

    /**
     * @tpTestDetails Check that warning message was logged, if client makes request to path,
     *                that is handled by two methods in two end-point in one application classes
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicationOneAppTwoResourcesWithSamePath() throws Exception {
        int initWarningsCount = getWarningCount();
        WebTarget base = client.target(generateURL("/f/b/c"));
        Response response = null;
        try {
            response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String strResponse = response.readEntity(String.class);
            Assertions.assertTrue(strResponse.equals(DuplicitePathDupliciteResourceOne.DUPLICITE_RESPONSE)
                    || strResponse.equals(DuplicitePathDupliciteResourceTwo.DUPLICITE_RESPONSE),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(1, getWarningCount() - initWarningsCount,
                "Wrong count of warnings in server log");
    }

    /**
     * @tpTestDetails Check that warning message was logged, if client makes request to path, that is handled by two methods
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicationPathInMethod() throws Exception {
        int initWarningsCount = getWarningCount();
        WebTarget base = client.target(generateURL("/f/g/h"));
        Response response = null;
        try {
            response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String str = response.readEntity(String.class);
            Assertions.assertTrue(str.equals(DuplicitePathMethodResource.DUPLICITE_RESPONSE_1)
                    || str.equals(DuplicitePathMethodResource.DUPLICITE_RESPONSE_2),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(1, getWarningCount() - initWarningsCount,
                "Wrong count of warnings in server log");
    }

    /**
     * @tpTestDetails Check that warning message was not logged, if client makes request to path, that is handled by one method
     *                (correct behaviour)
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNoDuplicationPathInMethod() throws Exception {
        int initWarningsCount = getWarningCount();
        WebTarget base = client.target(generateURL("/f/g/i"));
        Response response = null;
        try {
            response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals(DuplicitePathMethodResource.NO_DUPLICITE_RESPONSE,
                    response.readEntity(String.class),
                    "Wrong body of response");
        } finally {
            response.close();
        }
        Assertions.assertEquals(0, getWarningCount() - initWarningsCount,
                "Wrong count of warnings in server log");
    }
}
