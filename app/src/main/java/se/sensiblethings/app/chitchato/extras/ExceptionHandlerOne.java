package se.sensiblethings.app.chitchato.extras;

import android.app.Activity;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;

import se.sensiblethings.app.chitchato.activities.ErrorActivity;

/**
 * Created by user on 11/18/2016.
 */
public class ExceptionHandlerOne implements java.lang.Thread.UncaughtExceptionHandler {
    private final Activity _mActivity;
    private final String LINE_SEPARATOR = "\n";


    public ExceptionHandlerOne(Activity context) {
        _mActivity = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringWriter stacktrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stacktrace));
        StringBuilder _erReport = new StringBuilder();
        _erReport.append("###################ERROR CAUSE###################\n\n");
        _erReport.append(stacktrace.toString());

        Intent intent = new Intent(_mActivity, ErrorActivity.class);
        intent.putExtra("error", _erReport.toString());
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);


    }
}
