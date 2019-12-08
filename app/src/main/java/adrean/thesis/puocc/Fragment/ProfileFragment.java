package adrean.thesis.puocc.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import adrean.thesis.puocc.LoginActivity;
import adrean.thesis.puocc.R;

public class ProfileFragment extends Fragment {

    HashMap<String,String> userData = new HashMap<>();
    private String userId,userName,userEmail,userAddress,userPhone;
    SharedPreferences mPreferences;
    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView uName = (TextView) view.findViewById(R.id.userNameTv);
        TextView uEmail = (TextView) view.findViewById(R.id.userEmailTv);
        TextView uPhone = (TextView) view.findViewById(R.id.userPhoneTv);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        Button logoutBtn = (Button) view.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                mPreferences.edit().clear().apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Intent in = getActivity().getIntent();
        userData = (HashMap<String, String>) in.getSerializableExtra("userData");

        userId = mPreferences.getString("userId","");
        userName = mPreferences.getString("userName","");
        userEmail = mPreferences.getString("userEmail","");
        userAddress = mPreferences.getString("userAddress","");
        userPhone = mPreferences.getString("userPhone","");

        uName.setText(userName);
        uEmail.setText(userEmail);
        uPhone.setText(userPhone);

        return view;
    }
}
