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
		intent.putExtra("scope", params.scope.toJSON().toString());
		intent.putExtra("public_key", params.publicKey);
		intent.putExtra("payload", "nonce"); // this is needed for the older versions so it passes all the checks to get the "app outdated" error from the server
		intent.putExtra("nonce", params.nonce);
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
		if(params.scope.data==null || params.scope.data.isEmpty())
			throw new IllegalArgumentException("scope must not be empty");
		try{
			params.scope.validate();
		}catch(ScopeValidationException x){
			throw new IllegalArgumentException("scope is invalid", x);
		}
		if(params.publicKey==null)
			throw new IllegalArgumentException("publicKey must not be null");
		try{
			X509EncodedKeySpec spec=new X509EncodedKeySpec(Base64.decode(params.publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", ""), 0));
			KeyFactory.getInstance("RSA").generatePublic(spec);
		}catch(Exception x){
			throw new IllegalArgumentException("publicKey has invalid format", x);
		}
		if(TextUtils.isEmpty(params.nonce))
			throw new IllegalArgumentException("nonce must not be empty");
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
		 */
		public PassportScope scope;

		/**
		 * The public key of the bot.
		 * Required.
		 */
		public String publicKey;

		/**
		 * An arbitrary string that is passed to the bot server.
		 * Required.
		 */
		public String nonce;
	}
}
