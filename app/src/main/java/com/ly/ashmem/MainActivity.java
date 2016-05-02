package com.ly.ashmem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ly.service.IMemoryService;
import com.ly.service.MemoryService;
import com.ly.service.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
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
                Intent bindIntent = new Intent();
                bindIntent.setClass(MainActivity.this, Server.class);
                bindService(bindIntent,sc, Context.BIND_AUTO_CREATE);
            }
        });
    }

    ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMemoryService memoryService = MemoryService.Stub.asInterface(service);
            try {
                ParcelFileDescriptor pfd = memoryService.getFileDescriptor();
                Log.d(TAG, "onServiceConnected: " + pfd.getFd());
                memoryService.setValue(65535);
                FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());

                byte buffer[] = new byte[4];
                int val = 0;
                fis.read(buffer);
                for (int i = 0; i < 4; i++) {
                    Log.d(TAG, "onServiceConnected: buffer[" + i +"] = " + buffer[i]);
                }
                val = val | ((buffer[0] & 0xFF) << 24);
                val = val | ((buffer[1] & 0xFF) << 16);
                val = val | ((buffer[2] & 0xFF) << 8);
                val = val | ((buffer[3] & 0xFF));
                Log.d(TAG, "onServiceConnected: val = " + val);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
}
