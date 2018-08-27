package org.telegram.passport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This object represents several elements one of which must be provided.
 */
public class PassportScopeElementOneOfSeveral extends PassportScopeElement{
	/**
	 * List of elements one of which must be provided; must contain either several of
	 * {@link PassportScope#PASSPORT}, {@link PassportScope#DRIVER_LICENSE}, {@link PassportScope#IDENTITY_CARD}, {@link PassportScope#INTERNAL_PASSPORT} <b>or</b>
	 * several of {@link PassportScope#UTILITY_BILL}, {@link PassportScope#BANK_STATEMENT}, {@link PassportScope#RENTAL_AGREEMENT}, {@link PassportScope#PASSPORT_REGISTRATION}, {@link PassportScope#TEMPORARY_REGISTRATION}
	 */
	public List<PassportScopeElementOne> oneOf;
	/**
	 * Optional. Use this parameter if you want to request a selfie with the document from this list that the user chooses to upload.
	 */
	public boolean selfie;
	/**
	 * Optional. Use this parameter if you want to request a translation of the document from this list that the user chooses to upload.
	 */
	public boolean translation;

	public PassportScopeElementOneOfSeveral(List<PassportScopeElementOne> oneOf){
		this.oneOf=oneOf;
	}

	public PassportScopeElementOneOfSeveral(PassportScopeElementOne... oneOf){
		this.oneOf=Arrays.asList(oneOf);
	}

	public PassportScopeElementOneOfSeveral(String... oneOf){
		this.oneOf=new ArrayList<>(oneOf.length);
		for(String s:oneOf){
			this.oneOf.add(new PassportScopeElementOne(s));
		}
	}

	public PassportScopeElementOneOfSeveral(){

	}

	@Override
	Object toJSON(){
		if(oneOf==null || oneOf.isEmpty())
			return null;
		try{
			JSONObject o=new JSONObject();
			JSONArray oneOf=new JSONArray();
			for(PassportScopeElementOne e:this.oneOf)
				oneOf.put(e.toJSON());
			o.put("one_of", oneOf);
			if(selfie)
				o.put("selfie", true);
			if(translation)
				o.put("translation", true);
			return o;
		}catch(JSONException ignore){}
		return null;
	}

	@Override
	void validate() throws ScopeValidationException{
		if(oneOf!=null){
			int type=0;
			List<String> idDocuments=Arrays.asList(PassportScope.PASSPORT, PassportScope.IDENTITY_CARD, PassportScope.INTERNAL_PASSPORT, PassportScope.DRIVER_LICENSE);
			List<String> addressDocuments=Arrays.asList(PassportScope.UTILITY_BILL, PassportScope.BANK_STATEMENT, PassportScope.RENTAL_AGREEMENT, PassportScope.PASSPORT_REGISTRATION, PassportScope.TEMPORARY_REGISTRATION);
			for(PassportScopeElementOne el:oneOf){
				el.validate();
				if(Arrays.asList(PassportScope.PERSONAL_DETAILS, PassportScope.PHONE_NUMBER, PassportScope.EMAIL, PassportScope.ADDRESS).contains(el.type))
					throw new ScopeValidationException("one_of can only be used with documents");
				if(PassportScope.ID_DOCUMENT.equals(el.type) || PassportScope.ADDRESS_DOCUMENT.equals(el.type))
					throw new ScopeValidationException("one_of can only be used when exact document types are specified");
				int docType=0;
				if(idDocuments.contains(el.type))
					docType=1;
				else if(addressDocuments.contains(el.type))
					docType=2;
				if(type==0)
					type=docType;
				if(docType!=0 && type!=docType)
					throw new ScopeValidationException("One PassportScopeElementOneOfSeveral object can only contain documents of one type");
			}
		}
	}

	/**
	 * Request selfie
	 * @see PassportScopeElementOneOfSeveral#selfie
	 * @return this object for chaining
	 */
	public PassportScopeElementOneOfSeveral withSelfie(){
		selfie=true;
		return this;
	}

	/**
	 * Request translation
	 * @see PassportScopeElementOneOfSeveral#translation
	 * @return this object for chaining
	 */
	public PassportScopeElementOneOfSeveral withTranslation(){
		translation=true;
		return this;
	}
}
