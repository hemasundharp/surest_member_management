package com.surest.api.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    @Test
    void testAddCorsMappings() {
        CorsRegistry registry = new CorsRegistry();
        webConfig.addCorsMappings(registry);
        assertNotNull(registry);
    }

    @Test
    void testExtendMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());

        webConfig.extendMessageConverters(converters);

        assertTrue(converters.stream().noneMatch(c -> c instanceof MappingJackson2XmlHttpMessageConverter));
        assertTrue(converters.stream().anyMatch(c -> c instanceof MappingJackson2HttpMessageConverter));
    }
}
