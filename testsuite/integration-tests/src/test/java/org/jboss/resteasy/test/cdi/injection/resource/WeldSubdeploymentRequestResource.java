package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.test.cdi.injection.WeldSubdeploymentTest;
import org.junit.jupiter.api.Assertions;

@Path("/request")
@RequestScoped
public class WeldSubdeploymentRequestResource {

    @Inject
    private WeldSubdeploymentCdiJpaInjectingBean bean;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getMethod() {
        Assertions.assertNotNull(bean.entityManagerFactory(), WeldSubdeploymentTest.ERROR_MESSAGE);
    }

}
