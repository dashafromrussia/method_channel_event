package com.example.untitled;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.MethodChannel;




public class MainActivity extends FlutterActivity {
    private static final String EVENTS = "poc.deeplink.flutter.dev/events";
    private static final String CHANNEL = "poc.deeplink.flutter.dev/channel";

    private BroadcastReceiver linksReceiver;
    private String startString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();


        if (data != null) {
            startString = ((Uri) data).toString();
        }

    }


    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        System.out.println("ONcccccccccccccccc");
        /*new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                        if (call.method.equals("initialLink")) {
                            if (startString != null) {
                                result.success(startString);
                            }
                        }
                    }
                });*/
        new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENTS).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, final EventChannel.EventSink events) {
                        linksReceiver = createChangeReceiver(events);
                    }

                    @Override
                    public void onCancel(Object args) {
                        linksReceiver = null;
                    }
                }
        );
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("initialLink")) {
                                System.out.println("javaaaa");
                                if (startString != null) {
                                    result.success(startString);
                                }
// int batteryLevel = this.getBatteryLevel();
// List<Integer> results = new ArrayList<>();
//results.add(123);
                            }
                        }
                );
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(intent.getAction() == android.content.Intent.ACTION_VIEW && linksReceiver != null) {
            linksReceiver.onReceive(this.getApplicationContext(), intent);
        }
    }


    private BroadcastReceiver createChangeReceiver(final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // NOTE: assuming intent.getAction() is Intent.ACTION_VIEW

                String dataString = intent.getDataString();

                if (dataString == null) {
                    events.error("UNAVAILABLE", "Link unavailable", null);
                } else {
                    events.success(dataString);
                }
                ;
            }
        };
    }
}
