package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Utility class for reading and deserializing {@link Level} objects from an input stream (e.g., JSON file).
 * <p>
 * This class uses Jackson's {@link ObjectMapper} to convert JSON data into a {@link Level} instance.
 * It is typically used to load level configurations from resource files within the application.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     InputStream is = getClass().getResourceAsStream("/level/level1.json");
 *     LevelReader reader = new LevelReader(is);
 *     Level level = reader.readFile();
 * </pre>
 * </p>
 */
public class LevelReader {
    /** Jackson ObjectMapper for JSON deserialization. */
    private ObjectMapper mapper;
    /** Input stream for the level resource. */
    private InputStream inputStream;

    /**
     * Constructs a LevelReader for a given input stream.
     *
     * @param inputStream the input stream to read the level data from (typically from a resource file)
     */
    public LevelReader(InputStream inputStream) {
        mapper = new ObjectMapper();
        this.inputStream = inputStream;
    }

    /**
     * Returns the ObjectMapper used for deserialization.
     *
     * @return the {@link ObjectMapper} instance used by this reader
     */
    public ObjectMapper getMapper() { return this.mapper; }

    /**
     * Reads and deserializes a {@link Level} object from the input stream.
     *
     * @return the {@link Level} object if successful, or {@code null} if an error occurs during deserialization
     */
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
