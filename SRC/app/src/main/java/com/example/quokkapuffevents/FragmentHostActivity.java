package com.example.quokkapuffevents;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/*
*  For Testing purposes.
* Creates and empty activity to run a fragment in.
 */
public class FragmentHostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = new com.example.quokkapuffevents.controller.NotificationDash();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, fragment);
        transaction.commitNow();
    }
}
