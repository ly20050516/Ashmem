package com.ly.service;

import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by LY on 4/24/2016.
 */
public class MemoryService extends IMemoryService.Stub {

    private static final String TAG = "MemoryService";
    private MemoryFile mMemoryFile = null;

    public MemoryService(){

        try {
            mMemoryFile = new MemoryFile("AshmemService",4);
            setValue(10);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ParcelFileDescriptor getFileDescriptor() throws RemoteException {
        Log.d(TAG,"getFileDescriptor");

        ParcelFileDescriptor pfd = null;
        FileDescriptor fd = null;
        Class clz = mMemoryFile.getClass();
        try {
            Method getParceFileDescriptor = clz.getMethod("getFileDescriptor");
            fd = (FileDescriptor) getParceFileDescriptor.invoke(mMemoryFile);
            if(fd != null){
                Log.d(TAG, "getFileDescriptor: fd" + fd);

                Class clzp = ParcelFileDescriptor.class;
                Constructor c = clzp.getDeclaredConstructor(fd.getClass());
                pfd = (ParcelFileDescriptor) c.newInstance(fd);
                Log.d(TAG, "getFileDescriptor: pfd" + pfd.getFd());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return pfd;
    }

    @Override
    public void setValue(int val) throws RemoteException {

        Log.d(TAG, "setValue: val " + val);
        if(null == mMemoryFile)
            return ;
        byte buffer[] = new byte[4];
        buffer[0] = (byte)((val >> 24) & 0xFF);
        buffer[1] = (byte)((val >> 16) & 0xFF);
        buffer[2] = (byte)((val >> 8) & 0xFF);
        buffer[3] = (byte)((val) & 0xFF);

        try {
            mMemoryFile.writeBytes(buffer,0,0,4);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
