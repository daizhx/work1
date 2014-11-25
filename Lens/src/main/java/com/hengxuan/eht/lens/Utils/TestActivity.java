package com.hengxuan.eht.lens.Utils;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hengxuan.eht.lens.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final EditText path = (EditText) findViewById(R.id.et_path);
        final EditText text = (EditText) findViewById(R.id.et_text);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = path.getText().toString();
                String t = text.getText().toString();
                if(file == null){
                    path.setError("not null");
                }else{
                    FileOutputStream fos = null;
                    try {
                        boolean append;
                        if(checkBox.isChecked()){
                            append = true;
                        }else{
                            append = false;
                        }
                        fos = FileUtils.openFile(TestActivity.this, file,append);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(t.isEmpty()){
                        Log.d("daizhx", "aaaaaaaaaaaaaa");
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    try {
                        fos.write(t.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
