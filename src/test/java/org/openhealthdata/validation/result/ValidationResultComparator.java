/**
 * 
 */
package org.openhealthdata.validation.result;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author ohdohd
 *
 */
public class ValidationResultComparator {
	/**
	 * Compares a ValidationResult against a list of test names and expected results.
	 * @param vr
	 * @param expectedResult
	 * @return
	 */
	public static boolean compare(ValidationResult vr, HashMap<String, String> expectedResult, boolean noExtraTest){
		boolean same = true;
		
		HashMap<String, String> tests = new HashMap<String, String>();
		for (TestResultType t : vr.getTestResult()){
			tests.put(t.getUid(), t.getStatus());
		}
		Logger.getLogger(ValidationResultComparator.class.getName()).log(Level.INFO, "Found "+tests.size()+" test in validation result");
		for (String k : expectedResult.keySet()){
			if (tests.containsKey(k)){
				Logger.getLogger(ValidationResultComparator.class.getName()).log(Level.INFO, "Found "+k+" test in validation result");
				if (!expectedResult.get(k).equals(tests.get(k))){
					Logger.getLogger(ValidationResultComparator.class.getName()).log(Level.INFO, "Test "+k+" not what was expected");
					same = false;
					break;
				}
			}else{
				// The expected test is not in the result
				Logger.getLogger(ValidationResultComparator.class.getName()).log(Level.INFO, "Expected test "+k+" not in validation result");
					same = false;
					break;
			}
		}
		
		if (noExtraTest && same){
			same = expectedResult.keySet().equals(tests.keySet());
		}
		
		return same;
	}
}
