package adrean.thesis.puocc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import adrean.thesis.puocc.R;

public class ProfileFragment extends Fragment {

    HashMap<String,String> userData = new HashMap<>();
    private String userId,userName,userEmail,userAddress,userPhone;

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView uName = (TextView) view.findViewById(R.id.userNameTv);
        TextView uEmail = (TextView) view.findViewById(R.id.userEmailTv);
        TextView uPhone = (TextView) view.findViewById(R.id.userPhoneTv);

        Intent in = getActivity().getIntent();
        userData = (HashMap<String, String>) in.getSerializableExtra("userData");

        userId = userData.get("userId");
        userName = userData.get("userName");
        userEmail = userData.get("userEmail");
        userAddress = userData.get("userAddress");
        userPhone = userData.get("userPhone");

        uName.setText(userName);
        uEmail.setText(userEmail);
        uPhone.setText(userPhone);

        return view;
    }
}
