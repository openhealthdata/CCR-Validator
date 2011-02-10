package org.openhealthdata.validator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 2/4/11
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorManagerTest {

    @Test
    @Ignore
    public void validateTest() throws IOException{
        ValidationManager validationManager = new ValidationManager();
        String result = validationManager.validateToString(fileFromInputStream(getClass().getResourceAsStream("/ccr_sample.xml")));
        String expected = expectedResultAsString(getClass().getResourceAsStream("/expectedResult.xml"));
        Assert.assertEquals(expected, result);
    }

    private String expectedResultAsString(InputStream expectedStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(
                    new InputStreamReader(expectedStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            expectedStream.close();
        }

        String expectedContent = writer.toString();
        if ("\r\n".equals(System.getProperty("line.separator"))) {
          expectedContent = expectedContent.replaceAll("\r[^\n]", System.getProperty("line.separator"));
        } else {
          expectedContent = expectedContent.replaceAll("\r\n", System.getProperty("line.separator"));
        }
        return expectedContent;
    }

    private File fileFromInputStream(InputStream is) throws IOException{
        File f = File.createTempFile("tempInput", ".ccr");
        OutputStream out = new FileOutputStream(f);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0)
            out.write(buf, 0, len);
        out.close();
        is.close();
        return f;
    }
}
