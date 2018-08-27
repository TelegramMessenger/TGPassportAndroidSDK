package org.telegram.passport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This object represents the data to be requested.
 */
public class PassportScope{

	/**
	 * Value for {@link PassportScopeElementOne#type}: request personal details
	 */
	public static final String PERSONAL_DETAILS="personal_details";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request passport as one of identity documents
	 */
	public static final String PASSPORT="passport";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request internal passport as one of identity documents
	 */
	public static final String INTERNAL_PASSPORT="internal_passport";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request driver license as one of identity documents
	 */
	public static final String DRIVER_LICENSE="driver_license";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request ID card as one of identity documents
	 */
	public static final String IDENTITY_CARD="identity_card";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request any identity document. Same as [{@link #PASSPORT}, {@link #DRIVER_LICENSE}, {@link #IDENTITY_CARD}]
	 */
	public static final String ID_DOCUMENT="id_document";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request residential address
	 */
	public static final String ADDRESS="address";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request utility bill as one of proofs of address
	 */
	public static final String UTILITY_BILL="utility_bill";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request bank statement as one of proofs of address
	 */
	public static final String BANK_STATEMENT="bank_statement";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request rental agreement as one of proofs of address
	 */
	public static final String RENTAL_AGREEMENT="rental_agreement";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request registration in the internal passport as one of proofs of address
	 */
	public static final String PASSPORT_REGISTRATION="passport_registration";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request temporary registration as one of proofs of address
	 */
	public static final String TEMPORARY_REGISTRATION="temporary_registration";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request any proof of address. Same as [{@link #UTILITY_BILL}, {@link #BANK_STATEMENT}, {@link #RENTAL_AGREEMENT}]
	 */
	public static final String ADDRESS_DOCUMENT="address_document";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request phone number
	 */
	public static final String PHONE_NUMBER="phone_number";
	/**
	 * Value for {@link PassportScopeElementOne#type}: request email address
	 */
	public static final String EMAIL="email";

	/**
	 * Scope version, must be 1
	 */
	public int v=1;
	/**
	 * List of requested elements, each type may be used only once in the entire array of {@link PassportScopeElement} objects
	 */
	public List<PassportScopeElement> data;
	private JSONObject json;

	/**
	 * Construct a new PassportScope and initialize its {@link PassportScope#data} field.
	 * @param data list of requested elements
	 */
	public PassportScope(List<PassportScopeElement> data){
		this.data=data;
	}

	/**
	 * Construct a new PassportScope and initialize its {@link PassportScope#data} field.
	 * @param data list of requested elements
	 */
	public PassportScope(PassportScopeElement... data){
		this.data=Arrays.asList(data);
	}

	/**
	 * Construct a new PassportScope with a JSON object to be passed directly to the Telegram app.
	 * The JSON object must contain fields <code>data</code> and <code>v</code>.
	 * @param json JSON object to set
	 * @throws ScopeValidationException if the object doesn't contain the required fields
	 */
	public PassportScope(JSONObject json) throws ScopeValidationException{
		if(!json.has("data") || !json.has("v"))
			throw new ScopeValidationException("JSON object must contain v and data fields");
		this.json=json;
	}

	/**
	 * Construct a new PassportScope and initialize its {@link PassportScope#data} field.
	 * @param data {@link String}s and {@link PassportScopeElement}s with data to be requested.
	 * {@link String}s can be used to simplify the code; they will be converted to {@link PassportScopeElementOne} with all flags set to false.
	 * Null elements will be skipped.
	 */
	public PassportScope(Object... data){
		this.data=new ArrayList<>();
		for(Object o:data){
			if(o==null)
				continue;
			if(o instanceof PassportScopeElement)
				this.data.add((PassportScopeElement) o);
			else if(o instanceof String)
				this.data.add(new PassportScopeElementOne((String)o));
			else
				throw new IllegalArgumentException("This constructor only accepts PassportScopeElement and String, found "+o.getClass().getSimpleName());
		}
	}

	public PassportScope(){

	}

	public JSONObject toJSON(){
		if(json!=null)
			return json;
		try{
			JSONObject s=new JSONObject();
			s.put("v", v);
			JSONArray d=new JSONArray();
			for(PassportScopeElement el:data){
				Object j=el.toJSON();
				if(j!=null)
					d.put(j);
			}
			s.put("data", d);
			return s;
		}catch(JSONException ignore){}
		return null;
	}

	public void validate() throws ScopeValidationException{
		if(json!=null)
			return;
		for(PassportScopeElement el:data)
			el.validate();
	}
}
