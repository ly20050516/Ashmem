package com.ly.service;

import android.os.Binder;
import android.os.IInterface;

/**
 * Created by LY on 5/2/2016.
 */
public interface IAshmemCPPService extends IInterface {

    public static abstract class Stub extends Binder implements IAshmemCPPService{

    }

}
