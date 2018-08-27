package org.telegram.passport;

import org.json.JSONObject;

public abstract class PassportScopeElement{
	/*package*/ abstract Object toJSON();
	/*packags*/ abstract void validate() throws ScopeValidationException;
}
