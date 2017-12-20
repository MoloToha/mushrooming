package com.mushrooming.base;

import android.util.Log;

/**
 * Created by piotrek on 21.11.17.
 */

// one class for logging everything
public abstract class Logger {

    /* example usages:

        Logger.debug(this, "Entering function f with args: %d, %d", 5, 15)
        // -> logs debug message "[...]/<invoking object class name>: <\n>
        //    Entering function f with args: 5, 15"
        // ( or "[...]/<<LOG_ERROR>>: <\n>
        //   error trying to print formatted ERROR message: "Entering function f with args: %d, %d" "
        //   when args for string formatting are incorrect )

        // when formatting args are incorrect always sth like that is printed (with appropriate log type)

        Logger.debug(this, Logger.INT_VARIABLE_VALUE, "x", 5);
        // -> logs debug message "[...]/<invoking object class name>: <\n>Value of variable x is 5"

        Logger.error(this, badExceptionThatHappened, "exception happened")
        // invokes Log.e(this.getClass().toString, "exception happened", badExceptionThatHappened)
        // which prints also trace from Throwable
     */

    public static String INT_VARIABLE_VALUE = "Value of variable %s is %d";
    public static String DOUBLE_VARIABLE_VALUE = "Value of variable %s is %f";
    public static String STRING_VARIABLE_VALUE = "Value of variable %s is '%d'";

    public static void error(Object o, String messageFormat, Object... formatArgs) {
        try {
            String msg = String.format(messageFormat, formatArgs);
            App.instance().getDebug().addLog(Debug.LogType.ERROR, msg);
            Log.e(o.getClass().toString(), "\n" + msg);
        } catch (Throwable e) {
            Log.e(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted ERROR message: \n'" + messageFormat);
        }
    }

    public static void errorWithException(Object o, Throwable exc, String messageFormat, Object... formatArgs) {
        try {
            Log.e(o.getClass().toString(), "\n" + String.format(messageFormat, formatArgs), exc);
        } catch (Throwable e) {
            Log.e(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted ERROR message: \n'" + messageFormat);
        }
    }

    public static void warning(Object o, String messageFormat, Object... formatArgs) {
        try {
            String msg = String.format(messageFormat, formatArgs);
            App.instance().getDebug().addLog(Debug.LogType.WARNING, msg);
            Log.w(o.getClass().toString(), "\n" + msg);
        } catch (Throwable e) {
            Log.w(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted WARNING message: \n'" + messageFormat);
        }
    }

    public static void warningWithExcexption(Object o, Throwable exc, String messageFormat, Object... formatArgs) {
        try {
            Log.w(o.getClass().toString(), "\n" + String.format(messageFormat, formatArgs), exc);
        } catch (Throwable e) {
            Log.w(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted WARNING message: \n'" + messageFormat);
        }
    }

    public static void info(Object o, String messageFormat, Object... formatArgs) {
        try {
            String msg = String.format(messageFormat, formatArgs);
            App.instance().getDebug().addLog(Debug.LogType.INFO, msg);
            Log.i(o.getClass().toString(), "\n" + msg);
        } catch (Throwable e) {
            Log.i(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted INFO message: \n'" + messageFormat);
        }
    }

    public static void infoWithException(Object o, Throwable exc, String messageFormat, Object... formatArgs) {
        try {
            Log.i(o.getClass().toString(), "\n" + String.format(messageFormat, formatArgs), exc);
        } catch (Throwable e) {
            Log.i(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted INFO message: \n'" + messageFormat);
        }
    }

    public static void debug(Object o, String messageFormat, Object... formatArgs) {
        try {
            String msg = String.format(messageFormat, formatArgs);
            App.instance().getDebug().addLog(Debug.LogType.Debug, msg);
            Log.d(o.getClass().toString(), "\n" + msg);
        } catch (Throwable e) {
            Log.d(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted DEBUG message: \n'" + messageFormat);
        }
    }

    public static void debugWithException(Object o, Throwable exc, String messageFormat, Object... formatArgs) {
        try {
            Log.d(o.getClass().toString(), "\n" + String.format(messageFormat, formatArgs), exc);
        } catch (Throwable e) {
            Log.d(o.getClass().toString()+";<<LOG_ERROR>>",
                    "\nerror trying to print formatted DEBUG message: \n'" + messageFormat);
        }
    }

}
