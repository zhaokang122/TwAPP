package com.example.twapp.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;

import com.speedata.libutils.DataConversionUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2017/9/27   13:27.
 *         联系方式:QQ:420401567
 *         功能描述:  串口 gpio操作 ，初步筛选数据
 */
public class ReadSerialPort {
    private static DeviceControl deviceControl = null;
    private static SerialPort serialPort = null;
    private static ReadThread readThread = null;

    public static void startReader() {
        initSerialPort();
        readThread = new ReadThread();
        readThread.start();
    }

    public static void initSerialPort() {
        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN, 98);
//            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN, 94);
            deviceControl.PowerOnDevice();
            SystemClock.sleep(300);
            serialPort = new SerialPort();
//            serialPort.OpenSerial(SerialPort.SERIAL_TTYMT3, 9600);
            serialPort.OpenSerial(SerialPort.SERIAL_TTYMT0, 9600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            parsePessage((byte[]) msg.obj);
            Log.i("tw", "handleMessage: " + DataConversionUtils.byteArrayToInt((byte[]) msg.obj));
        }
    };

    /**
     * 校验初始数据 头0x5A 取长度
     *
     * @param bytes 串口原数据
     */
    private static void parsePessage(byte bytes[]) {
//        Log.e("tws",  "###" + DataConversionUtils.byteArrayToStringLog(bytes, bytes.length));
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[bytes.length - 1] == 0x5A) {
                return;
            } else if ((byte) bytes[i] == 0x5A) {//校验头
                byte[] len = DataConvertUtil.cutBytes(bytes, i + 1, 1);
                int lens = DataConversionUtils.byteArrayToInt(len);
                if (lens < 8 || lens > 255) {
                    continue;
                }
                int resultLens = lens + 1;//除了数据头数据在bytes中的长度
                if (i + resultLens > bytes.length) {
                    continue;
                }
                byte[] result = DataConvertUtil.cutBytes(bytes, i, lens + 1);
                if (result.length < 11) {
                    continue;
                } else {
//                    Log.e("tws", "串口第一次过滤数据 resultBytes:" + DataConversionUtils.byteArrayToStringLog(result, resultLens));
                    EventBus.getDefault().post(new MyEventBus(result));
                    return;
                }
            } else {
                continue;
            }

        }
    }


    static class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                Log.i("tw", "run: ");
                try {
                    byte[] bytes = serialPort.ReadSerial(serialPort.getFd(), 257);
                    Log.i("tw", "run: " + Arrays.toString(bytes));
                    if (bytes != null) {
                        handler.sendMessage(handler.obtainMessage(0, bytes));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 销毁 退出
     */
    public static void onDestroy() {
        if (deviceControl != null && serialPort != null && readThread != null) {
            try {
                serialPort.CloseSerial(serialPort.getFd());
                deviceControl.PowerOffDevice();
                readThread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
