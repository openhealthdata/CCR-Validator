package org.openhealthdata.validator;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

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
    public void validateTest(){
        File xml = new File("/ccr_sample.xml");
        ValidationManager validationManager = new ValidationManager();
        String expectedResult = validationManager.validateToString(xml);
        System.out.println(expectedResult);
    }
}
