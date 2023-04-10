package com.shpp.level0.practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class App {
    public static final String PROPERTIES_FILE_NAME = "app.properties";
    public static final String USERNAME_KEY = "username";
    private static final String SYSTEM_OPTION_KEY = "format";
    private static final String XML_OUTPUT_FORMAT = "xml";
    private static final String GREETING_LINE = "Привіт";
    private static final String EXCLAMATION_POINT = "!";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        new App().run();
    }

    void run() {
        Properties properties = getJarProperties();
        String externalPropertiesPath = getPathToExternalProperties();
        properties.putAll(getExternalProperties(externalPropertiesPath));
        String name = properties.getProperty(USERNAME_KEY);
        String message = GREETING_LINE + " " + name + EXCLAMATION_POINT;
        Message greetingMessage = new Message(message);
        if (isFileTypeXml(SYSTEM_OPTION_KEY)) {
            message = transformMessage(greetingMessage, new XmlMapper());
        } else {
            message = transformMessage(greetingMessage, new ObjectMapper());
        }
        System.out.println(message);
    }

    private String getPathToExternalProperties() {
        File file = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.getParentFile().getPath();
    }

    private boolean isFileTypeXml(String key) {
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

    private Properties getExternalProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(path + "/" + PROPERTIES_FILE_NAME, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            logger.info("The external properties file " + path + PROPERTIES_FILE_NAME +
                    " not found" + System.lineSeparator() +
                    "The program will be executed with default properties file");
        }
        return properties;
    }

    private String transformMessage(Message message, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't processed message object" + message, e);
        }
    }

    static class Message {
        public final String message;

        public Message(String message) {
            this.message = message;
        }
    }
}
