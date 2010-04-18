/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs.command;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benny
 */
public class CodeSnifferXmlLogParserTest {

    public CodeSnifferXmlLogParserTest() {
    }

    /**
     * Test of parse method, of class CodeSnifferXmlLogParser.
     */
    @Test
    public void testParse_AlwaysReturnsValidParseResult() {
        File file = null;
        CodeSnifferXmlLogParser instance = new CodeSnifferXmlLogParser();
        CodeSnifferXmlLogResult result = instance.parse(file);

        assertNotNull(result);
        assertNotNull(result.getCsErrors());
        assertNotNull(result.getCsWarnings());
    }
/*
    @Test
    public void testParse_Testresult1_ErrorCountIs13()
    {
        File f = new File("/home/benny/code/java/phpcs/phpcs2/test/unit/src/de/whitewashing/php/cs/testresult1.xml");

        CodeSnifferXmlLogParser instance = new CodeSnifferXmlLogParser();
        CodeSnifferXmlLogResult result = instance.parse(f);

        assertEquals(13, result.getCsErrors().size());
    }

    @Test
    public void testParse_TestResult1_WarningCountIs18()
    {
        File f = new File("/home/benny/code/java/phpcs/phpcs2/test/unit/src/de/whitewashing/php/cs/testresult1.xml");

        CodeSnifferXmlLogParser instance = new CodeSnifferXmlLogParser();
        CodeSnifferXmlLogResult result = instance.parse(f);

        assertEquals(18, result.getCsWarnings().size());
    }
    */
}
