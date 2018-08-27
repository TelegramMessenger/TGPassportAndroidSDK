package org.telegram.passport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * This object represents one particular element that must be provided.
 */
public class PassportScopeElementOne extends PassportScopeElement{
	/**
	 * Element type. One of {@link PassportScope#PERSONAL_DETAILS}, {@link PassportScope#PASSPORT}, {@link PassportScope#DRIVER_LICENSE}, {@link PassportScope#IDENTITY_CARD}, {@link PassportScope#INTERNAL_PASSPORT}, {@link PassportScope#ADDRESS}, {@link PassportScope#UTILITY_BILL},
	 * {@link PassportScope#BANK_STATEMENT}, {@link PassportScope#RENTAL_AGREEMENT}, {@link PassportScope#PASSPORT_REGISTRATION}, {@link PassportScope#TEMPORARY_REGISTRATION}, {@link PassportScope#PHONE_NUMBER}, {@link PassportScope#EMAIL}
	 * <br/><br/>
	 * You can also use the special type {@link PassportScope#ID_DOCUMENT} as an alias for one of {@link PassportScope#PASSPORT}, {@link PassportScope#DRIVER_LICENSE}, {@link PassportScope#IDENTITY_CARD}
	 * and the special type {@link PassportScope#ADDRESS_DOCUMENT} as an alias for one of {@link PassportScope#UTILITY_BILL}, {@link PassportScope#BANK_STATEMENT}, {@link PassportScope#RENTAL_AGREEMENT}.
	 */
	public String type;
	/**
	 * Optional. Use this parameter if you want to request a selfie with the document as well. Available for {@link PassportScope#PASSPORT}, {@link PassportScope#DRIVER_LICENSE},
	 * {@link PassportScope#IDENTITY_CARD} and {@link PassportScope#INTERNAL_PASSPORT}
	 */
	public boolean selfie;
	/**
	 * Optional. Use this parameter if you want to request a translation of the document as well. Available for {@link PassportScope#PASSPORT}, {@link PassportScope#DRIVER_LICENSE},
	 * {@link PassportScope#IDENTITY_CARD}, {@link PassportScope#INTERNAL_PASSPORT}, {@link PassportScope#UTILITY_BILL}, {@link PassportScope#BANK_STATEMENT}, {@link PassportScope#RENTAL_AGREEMENT}, {@link PassportScope#PASSPORT_REGISTRATION} and {@link PassportScope#TEMPORARY_REGISTRATION}
	 */
	public boolean translation;
	/**
	 * Optional. Use this parameter to request the first, last and middle name of the user in the language of the country that issued the document.
	 * Available for {@link PassportScope#PERSONAL_DETAILS}
	 */
	public boolean nativeNames;

	public PassportScopeElementOne(String type){
		this.type=type;
	}

	public PassportScopeElementOne(){

	}

	@Override
	Object toJSON(){
		if(!selfie && !translation && !nativeNames)
			return type;
		try{
			JSONObject o=new JSONObject();
			o.put("type", type);
			if(selfie)
				o.put("selfie", true);
			if(translation)
				o.put("translation", true);
			if(nativeNames)
				o.put("native_names", true);
			return o;
		}catch(JSONException ignore){}
		return null;
	}

	@Override
	void validate() throws ScopeValidationException{
		if(nativeNames && !PassportScope.PERSONAL_DETAILS.equals(type))
			throw new ScopeValidationException("nativeNames can only be used with personal_details");
		if(selfie && Arrays.asList(PassportScope.UTILITY_BILL, PassportScope.BANK_STATEMENT, PassportScope.RENTAL_AGREEMENT, PassportScope.PASSPORT_REGISTRATION, PassportScope.TEMPORARY_REGISTRATION).contains(type))
			throw new ScopeValidationException("selfie can only be used with identity documents");
		if(Arrays.asList(PassportScope.PHONE_NUMBER, PassportScope.EMAIL, PassportScope.ADDRESS, PassportScope.PERSONAL_DETAILS).contains(type)){
			if(selfie)
				throw new ScopeValidationException("selfie can only be used with identity documents");
			if(translation)
				throw new ScopeValidationException("translation can only be used with documents");
			if(nativeNames && !PassportScope.PERSONAL_DETAILS.equals(type))
				throw new ScopeValidationException("nativeNames can only be used with personal_details");
		}
	}

	/**
	 * Request selfie
	 * @see PassportScopeElementOne#selfie
	 * @return this object for chaining
	 */
	public PassportScopeElementOne withSelfie(){
		selfie=true;
		return this;
	}

	/**
	 * Request translation
	 * @see PassportScopeElementOne#translation
	 * @return this object for chaining
	 */
	public PassportScopeElementOne withTranslation(){
		translation=true;
		return this;
	}

	/**
	 * Request native names
	 * @see PassportScopeElementOne#nativeNames
	 * @return this object for method chaining
	 */
	public PassportScopeElementOne withNativeNames(){
		nativeNames=true;
		return this;
	}
}
