/**
 * 
 */
package org.openhealthdata.validation.result;

import java.util.HashMap;


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
		
		for (String k : expectedResult.keySet()){
			if (tests.containsKey(k)){
				if (!expectedResult.get(k).equals(tests.get(k))){
					same = false;
					break;
				}
			}else{
				// The expected test is not in the result
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
