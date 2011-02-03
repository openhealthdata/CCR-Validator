/*
 * Copyright 2010 OpenHealthData, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhealthdata.validation.result;

import java.util.LinkedList;
import java.util.List;

import org.openhealthdata.validation.result.ValidationResult.ValidationUsed.Profile;

/**
 * Implementation of ValidationResultManager for the rules engine. An instance
 * becomes the <code>global variable</code> for the rule packages.
 * 
 * @author Steven Waldren <swaldren at openhealthdata dot com>
 * 
 */
public class BaseValidationManager implements ValidationResultManager {

	private ValidationResult result = new ValidationResult();

	/**
	 * Add a new Error from the rules engine to the validation result
	 */
	public void addError(String testUID, String message, String serverity,
			String xpath) {
		// Check to see if it is a fatal error and set result status
		if (serverity.equals(ErrorType.FATAL)) {
			result.setStatus(ValidationResult.FAILED);
		}
		ErrorType e = new ErrorType();
		e.setXPathLocation(xpath);
		e.setServerity(serverity);
		e.setMessage(message);
		TestResultType trt = getTestOrNew(testUID);

		if (trt.getStatus() != null) {
			trt.setStatus(serverity);
			trt.getError().add(e);
		} else {
			trt.setStatus(getUpdatedStatus(trt.getStatus(), serverity));
			trt.getError().add(e);
		}
	}

	/*
	 * Updates the status of the Test based on the error severity
	 */
	private String getUpdatedStatus(String testStatus, String errorServerity) {

		if (testStatus == null || testStatus.equals("")) {
			if (errorServerity.equals(ErrorType.FATAL)) {
				return TestResultType.FAILED;
			} else {
				return TestResultType.PASSED;
			}
		}

		if (testStatus.equals(TestResultType.FAILED)) {
			return TestResultType.FAILED;
		} else if (testStatus.equals(TestResultType.MISSING)) {
			if (errorServerity.equals(ErrorType.FATAL)) {
				return TestResultType.FAILED;
			} else {
				return TestResultType.PASSED;
			}
		} else if (testStatus.equals(TestResultType.PASSED)) {
			if (errorServerity.equals(ErrorType.FATAL)) {
				return TestResultType.FAILED;
			} else {
				return TestResultType.PASSED;
			}
		} else {
			// DEFAULT TO A FAILED TEST
			return TestResultType.FAILED;
		}
	}

	/**
	 * Add error new error from the rules engine
	 */
	public void addError(String testUID, ErrorType error) {
		TestResultType trt = getTestOrNew(testUID);
		trt.setStatus(getUpdatedStatus(trt.getStatus(), error.getServerity()));
		trt.getError().add(error);
	}

	/*
	 * Check to see if the Test already exists in the ValidationResult
	 */
	private TestResultType getTestResult(String uid) {
		for (TestResultType trt : result.getTestResult()) {
			if (uid.equals(trt.getUid())) {
				return trt;
			}
		}
		return null;
	}

	/**
	 * Add a list of new errors to the validation result
	 */
	public void addError(String testUID, List<ErrorType> error) {
		TestResultType trt = getTestOrNew(testUID);
		for (ErrorType e : error) {
			trt.setStatus(getUpdatedStatus(trt.getStatus(), e.getServerity()));
			trt.getError().add(e);
		}
	}

	/**
	 * Add a new error from the rules engine
	 */
	public void addError(String testUID, String message, String serverity,
			int lineNumber, int columnNumber) {
		// Check to see if it is a fatal error and set result status
		if (serverity.equals(ErrorType.FATAL)) {
			result.setStatus(ValidationResult.FAILED);
		}
		ErrorType e = new ErrorType();
		InFileLocation loc = new InFileLocation();
		loc.setColumnNumber(columnNumber);
		loc.setLineNumber(lineNumber);
		e.setInFileLocation(loc);
		e.setServerity(serverity);
		e.setMessage(message);
		TestResultType trt = getTestOrNew(testUID);
		if (trt.getStatus() != null) {
			trt.setStatus(serverity);
			trt.getError().add(e);
		} else {
			trt.setStatus(getUpdatedStatus(trt.getStatus(), serverity));
			trt.getError().add(e);
		}
	}

	/**
	 * Add a new Test to the ValidationResult. It will discard already existing
	 * Test (based on TestResultType.uid)
	 */
	public void addTest(TestResultType test) {
		// TODO Consider a sorted list for the TestResultType
		// would improve performance with large set of Tests

		// Check to make sure the test does not exist already in the list
		boolean contains = false;
		for (TestResultType list : result.getTestResult()) {
			if (list.getUid().equals(test.getUid())) {
				contains = true;
				break;
			}
		}
		if (!contains) {
			result.getTestResult().add(test);
		}
	}

	/**
	 * Add a new Test to the ValidationResult
	 */
	public TestResultType addTest(String testUID, String name,
			String description, String status) {
		TestResultType trt = getTestOrNew(testUID);
		TestResultType.TestDescription tDesc = new TestResultType.TestDescription();
		tDesc.setName(name);
		tDesc.setDescription(description);
		trt.setTestDescription(tDesc);
		if (trt.getStatus() != null) {
			trt.setStatus(getUpdatedStatus(trt.getStatus(), status));
		} else {
			trt.setStatus(status);
		}
		this.addTest(trt);
		return trt;
	}

	/**
	 * Get the ValidationResult managed by this manager
	 */
	public ValidationResult getResult() {
		updateValidationResultStatus();
		return result;
	}

	/**
	 * This method sets the result validation status based on current errors
	 */
	private void updateValidationResultStatus() {
		// If the ValidationResult.status is already failed, then do not
		// check to make sure, otherwise double check to make sure there
		// are no failed tests.

		if (result.getStatus() != null
				&& result.getStatus().equals(ValidationResult.FAILED)) {
			return;
		}

		// If there is any failed tests, then the validation fails
		for (TestResultType trt : this.result.getTestResult()) {
			if (trt.getStatus().equals(TestResultType.FAILED)) {
				result.setStatus(ValidationResult.FAILED);
				return;
			}
		}
		// No Failed Tests found; set validation to passed
		result.setStatus(ValidationResult.PASSED);
	}

	/**
	 * Get Test by unique identifier
	 */
	public TestResultType getTest(String uid) {
		return getTestResult(uid);
	}

	/*
	 * This method looks for an existing TestResultType and if not found creates
	 * a default TestResultType. This protects against some one writing a rule
	 * without a TestResultType
	 */
	private TestResultType getTestOrNew(String uid) {
		TestResultType trt = getTestResult(uid);
		if (trt == null) {
			// no existing test
			trt = new TestResultType();
			trt.setUid(uid);
			TestResultType.TestDescription tDesc = new TestResultType.TestDescription();
			tDesc.setDescription("Not Defined");
			tDesc.setName("Not Defined");
			trt.setTestDescription(tDesc);
		}
		return trt;
	}

	/**
	 * Get a list of Test results by status
	 */
	public List<TestResultType> getTestByStatus(String status) {
		LinkedList<TestResultType> rs = new LinkedList<TestResultType>();
		for (TestResultType t : result.getTestResult()) {
			if (status.equals(t.getStatus())) {
				rs.add(t);
			}
		}
		return rs;
	}

	/**
	 * Set the ValidationResult to manage from an existing ValidaitonResult
	 */
	public void setResult(ValidationResult result) {
		this.result = result;
	}

	/**
	 * Add a new Test to the ValidationResult
	 */
	public TestResultType addTest(String testUID, String name,
			String description, String status, List<String> profiles) {
		TestResultType trt = addTest(testUID, name, description, status);
		List<Profile> profs = result.getValidationUsed().getProfile();
		Profile found = null;
		for (String s : profiles) {
			for (Profile p : profs) {
				if (p.getName().equals(s)) {
					found = p;
					break;
				}
			}
			if (found == null) {
				found = new Profile();
			}
			found.setId(s);
			found.setName(s);
		}
		getTest(testUID).getTestDescription().getProfile().addAll(profiles);
		return trt;
	}

	/**
	 * Add new Test to ValidationResult
	 */
	public TestResultType addTest(String testUID, String name,
			String description, String status, String profileID) {
		TestResultType test = addTest(testUID, name, description, status);
		// Check for ValidationUsed
		if (getResult().getValidationUsed() != null) {
			// ValidationUsed Found so get Profiles
			List<Profile> profs = getResult().getValidationUsed().getProfile();
			// Check for existing Profile
			Profile found = null;
			for (Profile p : profs) {
				if (p.getId().equals(profileID)) {
					found = p;
					break;
				}
			}
			// Check if no existing profile found
			if (found == null) {
				found = new Profile();
				found.setId(profileID);
				found.setName(profileID);
				this.addProfile(found);
				test.getTestDescription().getProfile().add(found);
			} else {
				for (Object o : test.getTestDescription().getProfile()) {
					if (((Profile) o).getId().equals(found.getId())) {
						// Profile already in the list do not add
						return test;
					}
				}
				// Profile not already in the list so add
				test.getTestDescription().getProfile().add(found);
			}
		} else {
			Profile p = new Profile();
			p.setId(profileID);
			p.setName(profileID);
			this.addProfile(p);
			test.getTestDescription().getProfile().add(p);
		}
		return test;
	}

	/**
	 * Add the validation used for the ValidationResult
	 */
	public void addValidationUsed(String schemaName, String schemaVersion,
			String profileName, String profileVersion, String profileID) {
		ValidationResult.ValidationUsed vu = new ValidationResult.ValidationUsed();
		ValidationResult.ValidationUsed.Schema s = new ValidationResult.ValidationUsed.Schema();
		s.setName(schemaName);
		s.setVersion(schemaVersion);
		vu.setSchema(s);
		ValidationResult.ValidationUsed.Profile p = new ValidationResult.ValidationUsed.Profile();
		p.setId(profileID);
		p.setName(profileName);
		p.setVersion(profileVersion);
		vu.getProfile().add(p);
		getResult().setValidationUsed(vu);

	}

	/**
	 * Add a new profile to the ValidationUsed in the ValidationResult
	 */
	public void addProfile(String profileName, String profileVersion,
			String profileID) {
		if (getResult().getValidationUsed() == null) {
			addValidationUsed(null, null, profileName, profileVersion,
					profileID);
		} else {
			ValidationResult.ValidationUsed.Profile p = new ValidationResult.ValidationUsed.Profile();
			p.setId(profileID);
			p.setName(profileName);
			p.setVersion(profileVersion);
			getResult().getValidationUsed().getProfile().add(p);
		}

	}

	/**
	 * Add a new profile to the ValidationUsed in the ValidationResult
	 */
	public void addProfile(Profile p) {
		if (getResult().getValidationUsed() == null) {
			ValidationResult.ValidationUsed vu = new ValidationResult.ValidationUsed();
			vu.getProfile().add(p);
			getResult().setValidationUsed(vu);
		} else {
			getResult().getValidationUsed().getProfile().add(p);
		}
	}

	/**
	 * Return the name of the file tested from the ValidationResult being
	 * managed
	 */
	public String getFileTested() {
		return getResult().getFileTested();
	}

	/**
	 * Set the name of the file tested in the ValidationResult being managed
	 */
	public void setFileTested(String filename) {
		getResult().setFileTested(filename);

	}

	private List<RuleType> rules = new LinkedList<RuleType>();

	public void addRule(String id, String name, String title, String packge,
			String description, String profile, String source, String author) {
		RuleType r = new RuleType();
		r.setId(id);
		r.setName(name);
		r.setTitle(title);
		r.setPackage(packge);
		r.setProfile(profile);
		r.setSource(source);
		r.setAuthor(author);
		rules.add(r);
	}
	
	public void addRules(List<RuleType> r){
		rules.addAll(r);
	}
	
	public List<RuleType> getRules(){
		return rules;
	}

	public void removeRule(String id) {
		// Assume a short list of rules
		// TODO improve performance with Binary or other search
		// Will need to add check to addRule method to not add duplicates
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).getId() == id) {
				rules.remove(i);
			}
		}
	}

	public void clearRuleList() {
		rules.clear();
	}
}
