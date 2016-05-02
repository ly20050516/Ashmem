package com.ly.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/**
 * Created by LY on 4/24/2016.
 */
public interface IMemoryService extends IInterface {

    public static abstract class Stub extends Binder implements IMemoryService{

        private static final String DESCRIPTOR = "com.ly.ashmem.IMemoryService";

        public Stub(){
            attachInterface(this,DESCRIPTOR);
        }

        public static IMemoryService asInterface(IBinder obj){
            if(obj == null)
                return null;
            IInterface iin = (IInterface)obj.queryLocalInterface(DESCRIPTOR);
            if(iin != null && iin instanceof  IMemoryService)
                return (IMemoryService)iin;
            return new IMemoryService.Stub.Proxy(obj);
        }

        /**
         * Retrieve the Binder object associated with this interface.
         * You must use this instead of a plain cast, so that proxy objects
         * can return the correct result.
         */
        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code){
                case INTERFACE_TRANSACTION:{
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getFileDescriptor:{
                    data.enforceInterface(DESCRIPTOR);
                    ParcelFileDescriptor result = this.getFileDescriptor();
                    reply.writeNoException();
                    if(result != null){
                        reply.writeInt(1);
                        result.writeToParcel(reply,0);
                    }else{
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_setValue:{
                    data.enforceInterface(DESCRIPTOR);

                    int val = data.readInt();
                    this.setValue(val);
                    reply.writeNoException();

                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IMemoryService{

            private IBinder mRemote;

            Proxy(IBinder binder){
                mRemote = binder;
            }

            public String getInterfaceDescriptor(){
                return DESCRIPTOR;
            }
            /**
             * Retrieve the Binder object associated with this interface.
             * You must use this instead of a plain cast, so that proxy objects
             * can return the correct result.
             */
            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public ParcelFileDescriptor getFileDescriptor() throws RemoteException {

                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();

                ParcelFileDescriptor result;
                try{
                    data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_getFileDescriptor, data, reply, 0);
                    reply.readException();
                    if(0 != reply.readInt())
                        result = ParcelFileDescriptor.CREATOR.createFromParcel(reply);
                    else
                        result = null;

                }finally {
                    data.recycle();
                    reply.recycle();
                }
                return result;
            }

            @Override
            public void setValue(int val) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try{
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(val);
                    mRemote.transact(TRANSACTION_setValue, data, reply, 0);
                    reply.readException();
                }finally {
                    data.recycle();
                    reply.recycle();
                }
            }
        }

        static final int TRANSACTION_getFileDescriptor = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_setValue = IBinder.FIRST_CALL_TRANSACTION + 1;
    }

    public ParcelFileDescriptor getFileDescriptor() throws RemoteException;
    public void setValue(int val) throws RemoteException;
}
