package org.telegram.passport.example;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.telegram.passport.PassportScope;
import org.telegram.passport.PassportScopeElementOne;
import org.telegram.passport.PassportScopeElementOneOfSeveral;
import org.telegram.passport.TelegramLoginButton;
import org.telegram.passport.TelegramPassport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

	private TelegramLoginButton loginButton;
	private SeekBar cornerRoundnessSlider;
	private String payload=UUID.randomUUID().toString();

	private View.OnClickListener loginClickListener=new View.OnClickListener(){
		@Override
		public void onClick(View v){
			TelegramPassport.AuthRequest params=new TelegramPassport.AuthRequest();
			params.botID=443863171;
			params.nonce=payload;
			params.publicKey="-----BEGIN PUBLIC KEY-----\n"+
			"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzmgKr0fPP4rB/TsNEweC\n"+
			"hoG3ntUxuBTmHsFBW6CpABGdaTmKZSjAI/cTofhBgtRQIOdX0YRGHHHhwyLf49Wv\n"+
			"9l+XexbJOa0lTsJSNMj8Y/9sZbqUl5ur8ZOTM0sxbXC0XKexu1tM9YavH+Lbrobk\n"+
			"jt0+cmo/zEYZWNtLVihnR2IDv+7tSgiDoFWi/koAUdfJ1VMw+hReUaLg3vE9CmPK\n"+
			"tQiTy+NvmrYaBPb75I0Jz3Lrz1+mZSjLKO25iT84RIsxarBDd8iYh2avWkCmvtiR\n"+
			"Lcif8wLxi2QWC1rZoCA3Ip+Hg9J9vxHlzl6xT01WjUStMhfwrUW6QBpur7FJ+aKM\n"+
			"oaMoHieFNCG4qIkWVEHHSsUpLum4SYuEnyNH3tkjbrdldZanCvanGq+TZyX0buRt\n"+
			"4zk7FGcu8iulUkAP/o/WZM0HKinFN/vuzNVA8iqcO/BBhewhzpqmmTMnWmAO8WPP\n"+
			"DJMABRtXJnVuPh1CI5pValzomLJM4/YvnJGppzI1QiHHNA9JtxVmj2xf8jaXa1LJ\n"+
			"WUNJK+RvUWkRUxpWiKQQO9FAyTPLRtDQGN9eUeDR1U0jqRk/gNT8smHGN6I4H+NR\n"+
			"3X3/1lMfcm1dvk654ql8mxjCA54IpTPr/icUMc7cSzyIiQ7Tp9PZTl1gHh281ZWf\n"+
			"P7d2+fuJMlkjtM7oAwf+tI8CAwEAAQ==\n"+
			"-----END PUBLIC KEY-----";
			PassportScope scope=new PassportScope();
			/*ArrayList<String> scope=new ArrayList<>();
			ViewGroup scopeView1=findViewById(R.id.scope1), scopeView2=findViewById(R.id.scope2);
			for(int i=0;i<scopeView1.getChildCount();i++){
				CheckBox check=(CheckBox)scopeView1.getChildAt(i);
				if(check.isChecked())
					scope.add(check.getText().toString());
			}
			for(int i=0;i<scopeView2.getChildCount();i++){
				CheckBox check=(CheckBox)scopeView2.getChildAt(i);
				if(check.isChecked())
					scope.add(check.getText().toString());
			}
			if(scope.size()==0){
				Toast.makeText(MainActivity.this, "Scope is empty", Toast.LENGTH_SHORT).show();
				return;
			}*/
			scope.data=new ArrayList<>();
			if(isChecked(R.id.scope_personal_details)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.PERSONAL_DETAILS;
				e.nativeNames=isChecked(R.id.scope_native_names);
				scope.data.add(e);
			}
			List<PassportScopeElementOne> idList;
			if(isChecked(R.id.scope_id_all_of)){
				idList=(List<PassportScopeElementOne>)(List<?>)scope.data; // I don't care
			}else{
				PassportScopeElementOneOfSeveral one=new PassportScopeElementOneOfSeveral();
				one.oneOf=new ArrayList<>();
				idList=one.oneOf;
				scope.data.add(one);
			}
			if(isChecked(R.id.scope_passport)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.PASSPORT;
				e.selfie=isChecked(R.id.scope_passport_selfie);
				e.translation=isChecked(R.id.scope_passport_translation);
				idList.add(e);
			}
			if(isChecked(R.id.scope_driver_license)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.DRIVER_LICENSE;
				e.selfie=isChecked(R.id.scope_driver_license_selfie);
				e.translation=isChecked(R.id.scope_driver_license_translation);
				idList.add(e);
			}
			if(isChecked(R.id.scope_identity_card)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.IDENTITY_CARD;
				e.selfie=isChecked(R.id.scope_identity_card_selfie);
				e.translation=isChecked(R.id.scope_identity_card_translation);
				idList.add(e);
			}
			if(isChecked(R.id.scope_internal_passport)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.INTERNAL_PASSPORT;
				e.selfie=isChecked(R.id.scope_internal_passport_selfie);
				e.translation=isChecked(R.id.scope_internal_passport_translation);
				idList.add(e);
			}


			if(isChecked(R.id.scope_address)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.ADDRESS;
				scope.data.add(e);
			}
			List<PassportScopeElementOne> addressList;
			if(isChecked(R.id.scope_id_all_of)){
				addressList=(List<PassportScopeElementOne>)(List<?>)scope.data; // I don't care
			}else{
				PassportScopeElementOneOfSeveral one=new PassportScopeElementOneOfSeveral();
				one.oneOf=new ArrayList<>();
				addressList=one.oneOf;
				scope.data.add(one);
			}
			if(isChecked(R.id.scope_utility_bill)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.UTILITY_BILL;
				e.translation=isChecked(R.id.scope_utility_bill_translation);
				addressList.add(e);
			}
			if(isChecked(R.id.scope_bank_statement)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.BANK_STATEMENT;
				e.translation=isChecked(R.id.scope_bank_statement_translation);
				addressList.add(e);
			}
			if(isChecked(R.id.scope_rental_agreement)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.RENTAL_AGREEMENT;
				e.translation=isChecked(R.id.scope_rental_agreement_translation);
				addressList.add(e);
			}
			if(isChecked(R.id.scope_passport_registration)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.PASSPORT_REGISTRATION;
				e.translation=isChecked(R.id.scope_passport_registration_translation);
				addressList.add(e);
			}
			if(isChecked(R.id.scope_temporary_registration)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.TEMPORARY_REGISTRATION;
				e.translation=isChecked(R.id.scope_temporary_registration_translation);
				addressList.add(e);
			}

			if(isChecked(R.id.scope_phone)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.PHONE_NUMBER;
				scope.data.add(e);
			}
			if(isChecked(R.id.scope_email)){
				PassportScopeElementOne e=new PassportScopeElementOne();
				e.type=PassportScope.EMAIL;
				scope.data.add(e);
			}

			//params.scope=scope;
			params.scope=new PassportScope(
					new PassportScopeElementOneOfSeveral(PassportScope.PASSPORT, PassportScope.IDENTITY_CARD).withSelfie(),
					new PassportScopeElementOne(PassportScope.PERSONAL_DETAILS).withNativeNames(),
					PassportScope.DRIVER_LICENSE,
					PassportScope.ADDRESS,
					PassportScope.ADDRESS_DOCUMENT,
					PassportScope.PHONE_NUMBER
			);
			TelegramPassport.request(MainActivity.this, params, 105);
		}
	};

	private View.OnLongClickListener loginLongClickListener=new View.OnLongClickListener(){
		@Override
		public boolean onLongClick(View v){
			TelegramPassport.showAppInstallAlert(MainActivity.this);
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null)
			payload=savedInstanceState.getString("payload");
		setContentView(R.layout.activity_main);

		loginButton=findViewById(R.id.login_button);
		loginButton.setOnClickListener(loginClickListener);
		loginButton.setOnLongClickListener(loginLongClickListener);

		cornerRoundnessSlider=findViewById(R.id.corner_roundness_slider);
		cornerRoundnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				loginButton.setCornerRoundness(progress/(float)seekBar.getMax());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar){

			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putSerializable("payload", payload);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==105){
			if(resultCode==RESULT_OK){
				Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show();
			}else if(resultCode==TelegramPassport.RESULT_ERROR){
				new AlertDialog.Builder(this)
						.setTitle(R.string.error)
						.setMessage(data.getStringExtra("error"))
						.setPositiveButton(R.string.ok, null)
						.show();
			}else{
				Toast.makeText(this, R.string.login_canceled, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean isChecked(@IdRes int id){
		return ((CompoundButton)findViewById(id)).isChecked();
	}
}
