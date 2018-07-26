package org.telegram.passport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * A collection of utility methods and constants to use with Telegram Passport.
 */
public class TelegramPassport{

	/**
	 * Intent action to open a Telegram Passport authorization activity in a client app
	 */
	public static final String ACTION_AUTHORIZE="org.telegram.passport.AUTHORIZE";

	/**
	 * Activity result code: unrecoverable error
	 */
	public static final int RESULT_ERROR=Activity.RESULT_FIRST_USER;

	/**
	 * Value for {@link AuthRequest#scope}: request personal details
	 */
	public static final String SCOPE_PERSONAL_DETAILS="personal_details";
	/**
	 * Value for {@link AuthRequest#scope}: request personal details and passport as one of identity documents
	 */
	public static final String SCOPE_PASSPORT="passport";
	/**
	 * Value for {@link AuthRequest#scope}: request personal details and driver license as one of identity documents
	 */
	public static final String SCOPE_DRIVER_LICENSE="driver_license";
	/**
	 * Value for {@link AuthRequest#scope}: request personal details and ID card as one of identity documents
	 */
	public static final String SCOPE_IDENTITY_CARD="identity_card";
	/**
	 * Value for {@link AuthRequest#scope}: request personal details and any identity document. Same as [{@link #SCOPE_PASSPORT}, {@link #SCOPE_DRIVER_LICENSE}, {@link #SCOPE_IDENTITY_CARD}]
	 */
	public static final String SCOPE_ID_DOCUMENT="id_document";
	/**
	 * Value for {@link AuthRequest#scope}: request a selfie with identity document
	 */
	public static final String SCOPE_ID_SELFIE="id_selfie";
	/**
	 * Value for {@link AuthRequest#scope}: request residential address
	 */
	public static final String SCOPE_ADDRESS="address";
	/**
	 * Value for {@link AuthRequest#scope}: request residential address and utility bill as one of proofs of address
	 */
	public static final String SCOPE_UTILITY_BILL="utility_bill";
	/**
	 * Value for {@link AuthRequest#scope}: request residential address and bank statement as one of proofs of address
	 */
	public static final String SCOPE_BANK_STATEMENT="bank_statement";
	/**
	 * Value for {@link AuthRequest#scope}: request residential address and rental agreement as one of proofs of address
	 */
	public static final String SCOPE_RENTAL_AGREEMENT="rental_agreement";
	/**
	 * Value for {@link AuthRequest#scope}: request residential address and any proof of address. Same as [{@link #SCOPE_UTILITY_BILL}, {@link #SCOPE_BANK_STATEMENT}, {@link #SCOPE_RENTAL_AGREEMENT}]
	 */
	public static final String SCOPE_ADDRESS_DOCUMENT="address_document";
	/**
	 * Value for {@link AuthRequest#scope}: request phone number
	 */
	public static final String SCOPE_PHONE_NUMBER="phone_number";
	/**
	 * Value for {@link AuthRequest#scope}: request email address
	 */
	public static final String SCOPE_EMAIL="email";

	/**
	 * Start the authorization flow for Telegram Passport. If Telegram app is installed, this opens it.
	 * If it isn't, an alert is shown suggesting to install it.
	 * @param activity calling activity
	 * @param params AuthRequest to pass to the Telegram app
	 * @param customRequestCode request code to be used in {@link android.app.Activity#startActivityForResult}
	 */
	public static void request(final Activity activity, AuthRequest params, int customRequestCode){
		Intent intent=getAuthIntent(params);
		if(intent.resolveActivity(activity.getPackageManager())==null){
			showAppInstallAlert(activity);
			return;
		}
		activity.startActivityForResult(intent, customRequestCode);
	}

	/**
	 * Serialize authorization parameters into an Intent for later use in {@link android.app.Activity#startActivityForResult}.
	 * Before launching this intent, make sure to check that there's Telegram app installed to handle it.
	 * @param params AuthRequest to pass to the Telegram app
	 * @return An intent
	 * @throws IllegalArgumentException if the AuthRequest object isn't filled in correctly
	 */
	public static Intent getAuthIntent(AuthRequest params){
		performSanityCheck(params);
		Intent intent=new Intent(ACTION_AUTHORIZE);
		intent.putExtra("bot_id", params.botID);
		JSONArray scope=new JSONArray();
		for(String s:params.scope)
			scope.put(s);
		intent.putExtra("scope", scope.toString());
		intent.putExtra("public_key", params.publicKey);
		intent.putExtra("payload", params.payload);
		return intent;
	}

	/**
	 * Show an app installation alert, in case you need to do that yourself.
	 * @param activity calling Activity
	 */
	public static void showAppInstallAlert(final Activity activity){
		String appName=null;
		try{
			PackageManager pm=activity.getPackageManager();
			appName=pm.getApplicationLabel(pm.getApplicationInfo(activity.getPackageName(), 0)).toString().replace("<", "&lt;");
		}catch(PackageManager.NameNotFoundException ignore){}
		ImageView banner=new ImageView(activity);
		banner.setImageResource(R.drawable.telegram_logo_large);
		banner.setBackgroundColor(0xFF4fa9e6);
		float dp=activity.getResources().getDisplayMetrics().density;
		int pad=Math.round(34*dp);
		banner.setPadding(0, pad, 0, pad);
		LinearLayout content=new LinearLayout(activity);
		content.setOrientation(LinearLayout.VERTICAL);
		content.addView(banner);
		TextView alertText=new TextView(activity);
		alertText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		alertText.setTextColor(0xFF000000);
		alertText.setText(Html.fromHtml(activity.getString(R.string.PassportSDK_DownloadTelegram, appName).replaceAll("\\*\\*([^*]+)\\*\\*", "<b>$1</b>")));
		alertText.setPadding(Math.round(24*dp), Math.round(24*dp), Math.round(24*dp), Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP ? Math.round(24*dp) : Math.round(2*dp));
		content.addView(alertText);
		AlertDialog alert=new AlertDialog.Builder(activity, /*Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP ? AlertDialog.THEME_HOLO_LIGHT :*/ R.style.Theme_Telegram_Alert)
				.setView(content)
				.setPositiveButton(R.string.PassportSDK_OpenGooglePlay, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.telegram.messenger")));
					}
				})
				.setNegativeButton(R.string.PassportSDK_Cancel, null)
				.show();
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
			int titleDividerId=activity.getResources().getIdentifier("titleDivider", "id", "android");
			View titleDivider=alert.findViewById(titleDividerId);
			if(titleDivider!=null){
				titleDivider.setVisibility(View.GONE);
			}
		}
	}

	private static void performSanityCheck(AuthRequest params){
		if(params.botID<=0)
			throw new IllegalArgumentException("botID must be >= 0");
		if(params.scope==null)
			throw new IllegalArgumentException("scope must not be null");
		if(params.scope.isEmpty())
			throw new IllegalArgumentException("scope must not be empty");
		if(params.publicKey==null)
			throw new IllegalArgumentException("publicKey must not be null");
		try{
			X509EncodedKeySpec spec=new X509EncodedKeySpec(Base64.decode(params.publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", ""), 0));
			KeyFactory.getInstance("RSA").generatePublic(spec);
		}catch(Exception x){
			throw new IllegalArgumentException("publicKey has invalid format", x);
		}
		if(TextUtils.isEmpty(params.payload))
			throw new IllegalArgumentException("payload must not be empty");
	}

	/**
	 * This object represents a Telegram Passport authorization request.
	 */
	public static class AuthRequest{
		/**
		 * Bot ID. This is the number at the beginning of the bot token.
		 * Required.
		 */
		public int botID;

		/**
		 * List of the names of fields you want to request.
		 * Required.
		 * @see #SCOPE_PERSONAL_DETAILS
		 * @see #SCOPE_PASSPORT
		 * @see #SCOPE_DRIVER_LICENSE
		 * @see #SCOPE_IDENTITY_CARD
		 * @see #SCOPE_ID_DOCUMENT
		 * @see #SCOPE_ID_SELFIE
		 * @see #SCOPE_ADDRESS
		 * @see #SCOPE_UTILITY_BILL
		 * @see #SCOPE_BANK_STATEMENT
		 * @see #SCOPE_RENTAL_AGREEMENT
		 * @see #SCOPE_ADDRESS_DOCUMENT
		 * @see #SCOPE_PHONE_NUMBER
		 * @see #SCOPE_EMAIL
		 */
		public List<String> scope;

		/**
		 * The public key of the bot.
		 * Required.
		 */
		public String publicKey;

		/**
		 * An arbitrary string that is passed to the bot server.
		 * Required.
		 */
		public String payload;
	}
}
