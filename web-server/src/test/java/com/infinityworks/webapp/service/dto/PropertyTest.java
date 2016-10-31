package com.infinityworks.webapp.service.dto;

import com.google.common.io.Resources;
import com.infinityworks.pafclient.dto.Property;
import org.junit.Test;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PropertyTest {
    @Test
    public void deserialisesAProperty() throws Exception {
        Property property = objectMapper.readValue(
                Resources.getResource("json/paf-property.json"), Property.class);

        assertThat(property.postTown(), is("Morpeth"));
        assertThat(property.street(), is("Tranwell Court"));
        assertThat(property.house(), is("1"));
        assertThat(property.postCode(), is("NE61 6PG"));
        assertThat(property.voters().get(0).fullName(), is("Deaux, John"));
    }
}
