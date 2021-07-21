package UnitTests;

import BillboardViewer.Billboard;
import BillboardViewer.BillboardParser;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBillboardParser {
    @Test
    public void  testparse() throws ParserConfigurationException, SAXException, IOException {
        Billboard Test = BillboardParser.parse(new File("./xmlDocs/1.xml"));
        assertEquals("Basic message-only billboard",Test.getMessage());
        assertEquals(null, Test.getInformation());
        assertEquals(null, Test.getPicture());
        assertEquals(null, Test.getInformationColour());
        assertEquals(null, Test.getMessageColour());
        assertEquals(null, Test.getBackground());
    }
    @Test
    public void  testparse2() throws ParserConfigurationException, SAXException, IOException {
        Billboard Test = BillboardParser.parse(new File("./xmlDocs/2.xml"));
        assertEquals("Billboard with an information tag and nothing else. Note that the text is word-wrapped. The quick brown fox jumped over the lazy dogs.",Test.getInformation());
        assertEquals(null, Test.getMessage());
        assertEquals(null, Test.getPicture());
        assertEquals(null, Test.getInformationColour());
        assertEquals(null, Test.getMessageColour());
        assertEquals(null, Test.getBackground());
    }
}
