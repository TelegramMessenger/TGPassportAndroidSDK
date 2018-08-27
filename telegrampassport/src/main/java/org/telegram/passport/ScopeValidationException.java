package org.telegram.passport;

/**
 * An exception thrown when {@link org.telegram.passport.TelegramPassport.AuthRequest#scope} contains an invalid combination of parameters.
 */
public class ScopeValidationException extends Exception{
	public ScopeValidationException(String message){
		super(message);
	}
}
