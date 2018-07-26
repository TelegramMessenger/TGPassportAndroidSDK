package org.telegram.passport.example;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.passport.TelegramLoginButton;
import org.telegram.passport.TelegramPassport;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

	private TelegramLoginButton loginButton;
	private SeekBar cornerRoundnessSlider;
	private String payload=UUID.randomUUID().toString();
	private ArrayList<String> lastScope;

	private View.OnClickListener loginClickListener=new View.OnClickListener(){
		@Override
		public void onClick(View v){
			TelegramPassport.AuthRequest params=new TelegramPassport.AuthRequest();
			params.botID=443863171;
			params.payload=payload;
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
			ArrayList<String> scope=new ArrayList<>();
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
			}
			params.scope=scope;
			lastScope=scope;
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
}
