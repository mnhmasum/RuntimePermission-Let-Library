package com.masum.runtime.permisssion;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.canelmas.let.AskPermission;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.Let;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements RuntimePermissionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getUserLocationAndDoSomething();
            }
        });
    }

    @AskPermission(ACCESS_FINE_LOCATION)
    private void getUserLocationAndDoSomething() {
        Toast.makeText(
                this,
                "Now that I have the permission I need, I'll get your location and do something with it",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Let.handle(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onShowPermissionRationale(List<String> permissionList, final RuntimePermissionRequest permissionRequest) {
        Toast.makeText(this, "" + permissionList.get(0), Toast.LENGTH_SHORT).show();

        final StringBuilder sb = new StringBuilder();

        for (String permission : permissionList) {
            sb.append(getRationale(permission));
            sb.append("\n");
        }

        new AlertDialog.Builder(this).setTitle("Permission Required!")
                .setMessage(sb.toString())
                .setCancelable(true)
                .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionRequest.retry();
                    }
                })
                .show();
    }

    @Override
    public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {

        Log.e("GRANT", "TEST" );
        final StringBuilder sb = new StringBuilder();

        for (DeniedPermission result : deniedPermissionList) {
            if (result.isNeverAskAgainChecked()) {
                sb.append(result.getPermission());
                sb.append("\n");
            }
        }

        if (sb.length() != 0) {
            new AlertDialog.Builder(this).setTitle("Go Settings ->  Apps -> MyApp -> Permissions  and Grant Permission")
                    .setMessage(sb.toString())
                    .setCancelable(true)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 1);
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private String getRationale(String permission) {
        if (permission.equals(Manifest.permission.GET_ACCOUNTS)) {
            return "Need account level access";
        } else if (permission.equals(Manifest.permission.RECEIVE_SMS)) {
            return "Please turn on sms permission";
        } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return "Please turn on your location otherwise you can not use this app";
        } else {
            return "Need rotational change";
        }
    }
}
