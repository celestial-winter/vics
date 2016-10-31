package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.VicsWebApp;
import com.infinityworks.webapp.config.PersistenceConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = {VicsWebApp.class, PersistenceConfig.class, RepositoryConfiguration.class})
public abstract class RepositoryTest {
}
