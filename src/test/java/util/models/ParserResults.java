package util.models;

import models.parser.OsmData;
import models.parser.OsmParser;
import models.parser.AbstractParser;
import util.TestParser;

import java.io.IOException;

public class ParserResults {
    private final AbstractParser<OsmData> actualParser;
    private final AbstractParser<OsmData> expectedParser;
    private final String name;

    public ParserResults(String osmFile, String jsonFile) throws IOException {
        this.actualParser = new OsmParser(osmFile);
        this.expectedParser = new TestParser(jsonFile);
        this.name = osmFile;

        this.actualParser.parse(osmFile);
        this.expectedParser.parse(jsonFile);
    }

    public AbstractParser<OsmData> getActualParser() {
        return actualParser;
    }
    public AbstractParser<OsmData> getExpectedParser() {
        return expectedParser;
    }
    public String getName() {
        return name;
    }
}
