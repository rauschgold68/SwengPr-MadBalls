package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class LevelReader {
    private ObjectMapper mapper;
    private InputStream inputStream;

    // Constructor for classpath resource
    public LevelReader(InputStream inputStream) {
        mapper = new ObjectMapper();
        this.inputStream = inputStream;
    }

    public ObjectMapper getMapper() { return this.mapper; }

    public Level readFile() {
        try {
            Level level = mapper.readValue(inputStream, Level.class);
            return level;
        } catch (Exception e) {
            System.err.println("Error: " + e + " occurred while trying to read resource. Returning null.");
            return null;
        }
    }
}
