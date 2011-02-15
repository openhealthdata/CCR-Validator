package org.openhealthdata.validator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openhealthdata.validation.result.TestResultType;
import org.openhealthdata.validation.result.ValidationResult;
import org.openhealthdata.validation.result.ValidationResultComparator;
import org.openhealthdata.validator.drools.InternalKnowledgeBaseManager;

import java.io.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: diego
 * Date: 2/4/11
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorManagerTest {

    @Test
    public void validateTest() throws IOException{
        ValidationManager validationManager = new ValidationManager(new InternalKnowledgeBaseManager());
        ValidationResult vr = validationManager.validate(fileFromInputStream(getClass().getResourceAsStream("/ccr_sample.xml")));
        Assert.assertTrue(ValidationResultComparator.compare(vr, createExpectedResult(), true));
    }
    
    private HashMap<String, String> createExpectedResult(){
    	HashMap<String, String> res = new HashMap<String, String>();
    	res.put("255b550a-4c64-4890-ae1a-e264fb2bd895", TestResultType.PASSED);
    	res.put("f55b0a86-8b56-4468-8de3-0246fe186fe9", TestResultType.PASSED);
    	res.put("f55b0a86-8b56-4468-8de3-0246fe186fa6", TestResultType.FAILED);
    	res.put("f9fdf3f6-5c66-4e9f-ba2c-d57fbe294567", TestResultType.PASSED);
    	res.put("cceef309-dac4-402a-923c-699e53e65ae9", TestResultType.PASSED);
    	return res;
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
