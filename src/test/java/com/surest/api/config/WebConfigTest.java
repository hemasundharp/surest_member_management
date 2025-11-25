package com.surest.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    @Test
    void testAddCorsMappings_AllPaths() {
        CorsRegistry registry = new CorsRegistry();
        webConfig.addCorsMappings(registry);

        assertNotNull(registry);

    }

    @Test
    void testExtendMessageConverters_RemovesXmlConverter() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        converters.add(xmlConverter);
        converters.add(jsonConverter);
        webConfig.extendMessageConverters(converters);
        assertFalse(converters.contains(xmlConverter));
        assertTrue(converters.contains(jsonConverter));
    }

    @Test
    void testExtendMessageConverters_NoXmlConverter() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        converters.add(jsonConverter);
        webConfig.extendMessageConverters(converters);
        assertEquals(1, converters.size());
        assertTrue(converters.contains(jsonConverter));
    }

    @Test
    void testAddCorsMappings_NullRegistryShouldNotThrow() {
        assertDoesNotThrow(() -> webConfig.addCorsMappings(new CorsRegistry()));
    }

}
