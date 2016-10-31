package com.infinityworks.webapp.feature;

import com.infinityworks.canvass.pafstub.PafServerStub;
import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.VicsWebApp;
import com.infinityworks.webapp.clients.email.EmailClient;
import com.infinityworks.webapp.clients.email.ImmutableEmailResponse;
import com.infinityworks.webapp.config.Config;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.security.SecurityConfig;
import com.infinityworks.webapp.service.UserService;
import com.infinityworks.webapp.testsupport.mocks.PdfServerStub;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = {
        VicsWebApp.class,
        RepositoryConfiguration.class,
        SecurityConfig.class,
        Config.class
})
public abstract class WebApplicationTest {
    protected MockMvc mockMvc;
    protected final PafServerStub pafApiStub = new PafServerStub(9002);
    protected final PdfServerStub pdfServerStub = new PdfServerStub(18089);

    @Autowired
    protected WebApplicationContext applicationContext;

    @Autowired
    protected Filter springSecurityFilterChain;

    protected User admin() {
        UserService userService = getBean(UserService.class);
        return userService.getByUsername("me@admin.uk").get();
    }

    protected User covs() {
        UserService userService = getBean(UserService.class);
        return userService.getByUsername("cov@south.cov").get();
    }

    protected User earlsdon() {
        UserService userService = getBean(UserService.class);
        return userService.getByUsername("earlsdon@cov.uk").get();
    }

    protected <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Before
    public void setUp() {
        pafApiStub.start();
        pdfServerStub.start();
    }

    @After
    public void tearDown() throws Exception {
        pafApiStub.stop();
        pdfServerStub.stop();
    }
}

@Configuration
class FeatureTestMock {
    /**
     * Mock the email client so we don't call out to SendGrid in tests
     */
    @Bean
    public EmailClient emailClient() {
        return content -> Try.success(ImmutableEmailResponse.builder().withMessage("success").build());
    }
}