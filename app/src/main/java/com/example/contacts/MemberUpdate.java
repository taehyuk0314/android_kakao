package com.example.contacts;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MemberUpdate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_update);
        Context _this = MemberUpdate.this;
        String[] spec = getIntent()
                .getStringExtra("spec")
                .split(",")
                ;
        Index.Member m = new Index.Member();
        ImageView photo = findViewById(R.id.profile);
        photo.setImageDrawable(
                getResources()
                        .getDrawable(
                                getResources()
                                        .getIdentifier(
                                                _this.getPackageName()+":drawable/"
                                                        +spec[6],

                                                null,
                                                null),
                                _this.getTheme()
                        )
        );
        EditText name = findViewById(R.id.name);
        name.setText(spec[1]);
        EditText changePhone = findViewById(R.id.changePhone);
        changePhone.setText(spec[4]);
        EditText changeEmail = findViewById(R.id.changeEmail);
        changeEmail.setText(spec[3]);
        EditText changeAddress = findViewById(R.id.changeAddress);
        changeAddress.setText(spec[5]);

    }
}
