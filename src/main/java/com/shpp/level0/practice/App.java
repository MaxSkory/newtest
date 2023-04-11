package com.shpp.level0.practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final String PROPERTIES_FILE_NAME = "app.properties";
    public static final String USERNAME_KEY = "username";
    private static final String SYSTEM_OPTION_FILE_FORMAT_KEY = "format";
    private static final String XML_FORMAT_VALUE = "xml";
    private static final String GREETING_LINE = "Привіт";
    private static final String EXCLAMATION_POINT = "!";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        new App().run();
    }

    void run() {
        Properties currentProperties = mergeProperties(getJarProperties(), getExternalProperties());
        Message greetingMessage = new Message(getMessage(currentProperties, USERNAME_KEY));
        String message = getFormattedMessage(greetingMessage);
        logger.trace("Printing message");
        System.out.println(message);
    }

    private String getFormattedMessage(Message greetingMessage) {
        logger.trace("Formatting message to a specific type");
        return isFileTypeXml(SYSTEM_OPTION_FILE_FORMAT_KEY)
                ? transformMessage(greetingMessage, new XmlMapper()) :
                transformMessage(greetingMessage, new ObjectMapper());

    }

    private String getMessage(Properties currentProperties, String usernameKey) {
        logger.trace("Processing new message");
        String name = currentProperties.getProperty(usernameKey);
        return GREETING_LINE + " " + name + EXCLAMATION_POINT;
    }

    private Properties mergeProperties(Properties internalProperties, Properties externalProperties) {
        if (!externalProperties.isEmpty()) {
            logger.info("Merging internal and external property files");
            internalProperties.putAll(externalProperties);
        }
        return internalProperties;
    }

    private String getPathToExternalProperties() {
        File file = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return file.getParentFile().getPath();
    }

    private boolean isFileTypeXml(String key) {
        String property = System.getProperty(key);
        return property != null && property.equals(XML_FORMAT_VALUE);
    }

    private Properties getJarProperties() {
        logger.info("Reading internal properties file");
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(Objects.requireNonNull(getClass()
                    .getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME))));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Can't read " + PROPERTIES_FILE_NAME + " from jar.");
        }
    }

    private Properties getExternalProperties() {
        logger.info("Reading external properties file");
        String path = getPathToExternalProperties();
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(path + "/" + PROPERTIES_FILE_NAME, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            logger.warn("The external properties file " + path + PROPERTIES_FILE_NAME
                    + " not found" + System.lineSeparator()
                    + "The program will be executed with default properties file");
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

    private static class Message {
        private final String message;

        public Message(String message) {
            logger.trace("Creating a new Message instance");
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
