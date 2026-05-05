package util.models;

import models.parser.OsmParser;
import models.parser.AbstractParser;
import util.TestParser;

public class ParserResults {
    AbstractParser actualParser;
    AbstractParser expectedParser;
    String name;

    public ParserResults(String osmFile, String jsonFile){
        actualParser = new OsmParser(osmFile);
        expectedParser = new TestParser(jsonFile);
        name = osmFile;

        actualParser.parse();
        expectedParser.parse();
    }

    public AbstractParser getActualParser() {
        return actualParser;
    }
    public AbstractParser getExpectedParser() {
        return expectedParser;
    }
    public String getName() {
        return name;
    }
}
