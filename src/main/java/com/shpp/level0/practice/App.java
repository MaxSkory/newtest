package com.shpp.level0.practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public class App {
    public static final String PROPERTIES_FILE_NAME = "app.properties";
    public static final String OUTSIDE_PROPERTIES_PATH = "./app.properties";
    public static final String PROPERTY_KEY = "username";
    private static final String OUTPUT_FORMAT_KEY = "type";
    private static final String XML_OUTPUT_FORMAT = "xml";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        new App().run();
    }

    void run() {
        String outputMessage;
        Properties properties = getJarProperties();
        properties.putAll(getOutsideProperties());
        String name = properties.getProperty(PROPERTY_KEY);
        Message greetingMessage = new Message(name);
        if (fileTypeSetToXml(OUTPUT_FORMAT_KEY)) {
            outputMessage = getMessage(greetingMessage, new XmlMapper());
        } else {
            outputMessage = getMessage(greetingMessage, new ObjectMapper());
        }
        System.out.println(outputMessage);
    }

    private boolean fileTypeSetToXml(String key) {
        String property = System.getProperty(key);
        return property != null && property.equals(XML_OUTPUT_FORMAT);
    }

    private Properties getJarProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(Objects.requireNonNull(getClass().
                    getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME))));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Can't read " + PROPERTIES_FILE_NAME + " from jar.");
        }
    }

    private Properties getOutsideProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(OUTSIDE_PROPERTIES_PATH));
            return properties;
        } catch (IOException e) {
            logger.info("The outside properties file " + OUTSIDE_PROPERTIES_PATH +
                    " not found" + System.lineSeparator() +
                    "The program will be executed with default properties file", e);
        }
        return properties;
    }

    private String getMessage(Message message, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
