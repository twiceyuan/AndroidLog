package com.twiceyuan.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by twiceYuan on 8/18/16.
 * Email: i@twiceyuan.com
 * Site: http://twiceyuan.com
 * <p/>
 * 自定义日志工具类
 */
public class L {

    private static final String prefixLine = "┌───────────────────────────────────────────────────────────────────────────────────\n";
    private static final String middleLine = "├───────────────────────────────────────────────────────────────────────────────────\n";
    private static final String prefixChar = "│ ";
    private static final String suffixLine = "└───────────────────────────────────────────────────────────────────────────────────\n";

    private static final int JSON_PRETTIFY_INDENT = 2; // JSON 格式化参数
    private static final int DEFAULT_OFFSET       = 4; // 在本类的静态方法中，默认有 4 层方法栈

    private static final Logger sDefaultLevelLogger = new Logger();

    private static boolean sGlobalToggle = true;

    private L() {
    }

    public static Logger showPath() {
        Logger logger = cloneDefaultLogger();
        if (logger != null) {
            logger.setMethodOffset(-1);
            logger.showPath();
            return logger;
        } else {
            return sDefaultLevelLogger;
        }
    }

    public static Logger tag(Object tag) {
        Logger logger = cloneDefaultLogger();
        if (logger != null) {
            logger.setMethodOffset(-1);
            logger.setTag(tag);
            return logger;
        } else {
            return sDefaultLevelLogger;
        }
    }

    private static Logger cloneDefaultLogger() {
        try {
            Logger logger = (Logger) sDefaultLevelLogger.clone();
            logger.setMethodOffset(-1);
            logger.showPath();
            return logger;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Logger createLogger() {
        Logger logger = new Logger();
        logger.setMethodOffset(-1);
        return logger;
    }

    public static Logger getDefaultLevelLogger() {
        return sDefaultLevelLogger;
    }

    public static void i(String message) {
        sDefaultLevelLogger.i(message);
    }

    public static void w(String message) {
        sDefaultLevelLogger.w(message);
    }

    public static void e(String message) {
        sDefaultLevelLogger.e(message);
    }

    public static void v(String message) {
        sDefaultLevelLogger.v(message);
    }

    public static void wtf(String message) {
        sDefaultLevelLogger.wtf(message);
    }

    public static void i(Throwable tr, String message) {
        sDefaultLevelLogger.i(tr, message);
    }

    public static void w(Throwable tr, String message) {
        sDefaultLevelLogger.w(tr, message);
    }

    public static void e(Throwable tr, String message) {
        sDefaultLevelLogger.e(tr, message);
    }

    public static void v(Throwable tr, String message) {
        sDefaultLevelLogger.v(tr, message);
    }

    public static void wtf(Throwable tr, String message) {
        sDefaultLevelLogger.wtf(message, tr);
    }

    public static void i(String prepare, Object... args) {
        sDefaultLevelLogger.i(prepare, args);
    }

    public static void e(String prepare, Object... args) {
        sDefaultLevelLogger.e(prepare, args);
    }

    public static void w(String prepare, Object... args) {
        sDefaultLevelLogger.w(prepare, args);
    }

    public static void v(String prepare, Object... args) {
        sDefaultLevelLogger.v(prepare, args);
    }

    public static void wtf(String prepare, Object... args) {
        sDefaultLevelLogger.wtf(prepare, args);
    }

    public static void json(String json) {
        sDefaultLevelLogger.json(json);
    }

    public static String prettifyJsonWithBorder(String json) {

        StringBuilder builder = new StringBuilder();
        try {
            String jsonLines[] = prettifyJson(json).split("\n");
            for (String jsonLine : jsonLines) {
                builder.append("│ ").append(jsonLine).append("\n");
            }
            return prefixLine + builder.toString() + suffixLine;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 纯粹的 JSON 美化方法，根据判断是 JSONObject 还是 JSONArray 分别输出字符串
     *
     * @param json 源
     * @return 格式化后的
     */
    private static String prettifyJson(String json) {
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                return jsonObjectToString(json);
            }
            if (json.startsWith("[")) {
                return jsonArrayToString(json);
            }
        } catch (Exception e) {
            return "Invalid JSON Format.";
        }
        return "Invalid JSON Format.";
    }

    /**
     * 打印代码和代码的位置
     */
    private static String printWithPath(String callerName, StackTraceElement element, String message) {
        StringBuilder builder = new StringBuilder();
        String pathString = element.toString();
        int classNameIndex = pathString.indexOf(callerName);
        pathString = classNameIndex != -1 ? pathString.substring(classNameIndex) : pathString;
        builder.append(prefixLine);
        builder.append(prefixChar).append("Path: ").append(pathString).append("\n");
        builder.append(middleLine);
        String messageLines[] = message.split("\n");
        for (String line : messageLines) {
            builder.append(prefixChar).append(line).append("\n");
        }
        builder.append(suffixLine);
        return builder.toString();
    }

    public static void setGlobalToggle(boolean globalToggle) {
        sGlobalToggle = globalToggle;
    }

    /**
     * MockTextUtils#isEmpty
     */
    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Mock JSONArray
     */
    static String jsonArrayToString(String string) {
        try {
            Class mJsonArrayClass = Class.forName("org.json.JSONArray");
            //noinspection unchecked
            Constructor constructor = mJsonArrayClass.getConstructor(String.class);
            Object mJsonArray = constructor.newInstance(string);
            //noinspection unchecked
            Method method = mJsonArrayClass.getDeclaredMethod("toString", int.class);
            return (String) method.invoke(mJsonArray, JSON_PRETTIFY_INDENT);
        } catch (Exception e) {
            callNativeLog("e", "LogError", "JSON Array 处理错误", e);
            return null;
        }
    }

    /**
     * Mock JSONObject
     */
    static String jsonObjectToString(String string) {
        try {
            Class mJsonObjectClass = Class.forName("org.json.JSONObject");
            //noinspection unchecked
            Constructor constructor = mJsonObjectClass.getConstructor(String.class);
            Object mJsonObject = constructor.newInstance(string);
            //noinspection unchecked
            Method method = mJsonObjectClass.getDeclaredMethod("toString", int.class);
            return (String) method.invoke(mJsonObject, JSON_PRETTIFY_INDENT);
        } catch (Exception e) {
            callNativeLog("e", "LogError", "JSON Object 处理错误", e);
            return null;
        }
    }

    /**
     * 使用反射调用系统的 log 方法
     *
     * @param methodName 日志类型，分为 i，w，v，e，wtf 五种
     * @param tag        日志 Tag
     * @param message    日志信息
     * @param throwable  日志 throwable 参数
     */
    private static void callNativeLog(String methodName, String tag, String message, Throwable throwable) {
        // null -> "null", 避免 message 为空抛出异常
        message = String.valueOf(message);
        if ("json".equals(methodName)) {
            // 系统没有 json 类型的方法，使用 i 代替
            methodName = "i";
        }
        try {
            Class logClass = Class.forName("android.util.Log");
            //noinspection unchecked
            Method logMethod = logClass.getMethod(methodName, String.class, String.class);
            //noinspection unchecked
            Method logMethodWithThrowable = logClass.getMethod(methodName, String.class, String.class, Throwable.class);
            if (throwable == null) {
                for (String line : message.split("\n")) {
                    logMethod.invoke(null, tag, line);
                }
            } else {
                String[] lines = message.split("\n");
                int lineCount = lines.length;
                for (int i = 0; i < lineCount - 1; i++) {
                    String line = lines[i];
                    logMethod.invoke(null, tag, line);
                }
                logMethodWithThrowable.invoke(null, tag, lines[lineCount - 1], throwable);
            }
        } catch (Exception e) {
            throw new IllegalStateException("本项目只能在 Android 平台中使用: ");
        }
    }

    private static String getCallerClassName(String fullClassName) {
        String className;
        int lastPointIndex = fullClassName.lastIndexOf(".");
        if (lastPointIndex > -1) {
            className = fullClassName.substring(lastPointIndex + 1);
        } else {
            className = fullClassName;
        }
        int lastDollarIndex = className.lastIndexOf("$");
        while (lastDollarIndex > -1) {
            className = className.substring(0, lastDollarIndex);
            lastDollarIndex = className.lastIndexOf("$");
        }
        return className;
    }

    interface StackTraceCallback {
        void call(String callerName, StackTraceElement element);
    }

    public static class Logger implements Cloneable {

        private int     mMethodOffset = DEFAULT_OFFSET;
        private boolean isShowPath    = false;

        private String mTag;

        private boolean mToggle = true; // 开关，可以整体控制日志

        private Logger() {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        private Logger setMethodOffset(int methodOffset) {
            mMethodOffset = DEFAULT_OFFSET + methodOffset;
            return this;
        }

        public Logger showPath() {
            isShowPath = true;
            return this;
        }

        /**
         * 设置当前 Logger 的日志开启还是关闭
         *
         * @param toggle true 为开启，false 为关闭，默认开启
         */
        public void setToggle(boolean toggle) {
            mToggle = toggle;
        }

        /**
         * 设置全局 Logger 的日志开启还是关闭。当全局关闭时，局部开启也不会打印；当全局开启时，局部关闭仍然会关闭
         *
         * @param toggle true 为开启，false 为关闭，默认开启
         */
        public void setGlobalToggle(boolean toggle) {
            sGlobalToggle = toggle;
        }

        public Logger setTag(Object tag) {
            if (tag instanceof CharSequence) {
                mTag = (String) tag;
            } else if (tag instanceof Class) {
                mTag = ((Class) tag).getSimpleName();
            } else {
                mTag = tag.getClass().getSimpleName();
            }
            return this;
        }

        private void printLog(final String logType, final String message, final Throwable throwable) {
            if (mToggle && sGlobalToggle) {
                getCallerClass(new StackTraceCallback() {
                    @Override
                    public void call(String callerName, StackTraceElement element) {
                        String msg = message;
                        if (element == null) {
                            if ("json".equals(logType)) {
                                msg = prettifyJsonWithBorder(message);
                            }
                            callNativeLog(logType, getTag(callerName), msg, throwable);
                        } else {
                            if ("json".equals(logType)) {
                                msg = prettifyJson(message);
                            }
                            callNativeLog(logType, getTag(callerName), printWithPath(callerName, element, msg), throwable);
                        }
                    }
                });
            }
        }

        private String getTag(String callerName) {
            return isEmpty(mTag) ? callerName : mTag;
        }

        public void i(final Throwable throwable, String message) {
            printLog("i", message, throwable);
        }

        public void w(final Throwable throwable, String message) {
            printLog("w", message, throwable);
        }

        public void e(final Throwable throwable, String message) {
            printLog("e", message, throwable);
        }

        public void v(final Throwable throwable, String message) {
            printLog("v", message, throwable);
        }

        public void wtf(final Throwable throwable, String message) {
            printLog("wtf", message, throwable);
        }

        public void i(final String message) {
            i(null, message);
        }

        public void w(final String message) {
            w(null, message);
        }

        public void e(final String message) {
            e(null, message);
        }

        public void v(final String message) {
            v(null, message);
        }

        public void wtf(final String message) {
            wtf(null, message);
        }

        public void i(final String prepare, final Object... args) {
            i(String.format(prepare, args));
        }

        public void e(final String prepare, final Object... args) {
            e(String.format(prepare, args));
        }

        public void w(final String prepare, final Object... args) {
            w(String.format(prepare, args));
        }

        public void v(final String prepare, final Object... args) {
            v(String.format(prepare, args));
        }

        public void wtf(final String prepare, final Object... args) {
            wtf(String.format(prepare, args));
        }

        public void i(final Throwable throwable, final String prepare, final Object... args) {
            i(throwable, String.format(prepare, args));
        }

        public void e(final Throwable throwable, final String prepare, final Object... args) {
            e(throwable, String.format(prepare, args));
        }

        public void w(final Throwable throwable, final String prepare, final Object... args) {
            w(throwable, String.format(prepare, args));
        }

        public void v(final Throwable throwable, final String prepare, final Object... args) {
            v(throwable, String.format(prepare, args));
        }

        public void wtf(final Throwable throwable, final String prepare, final Object... args) {
            wtf(throwable, String.format(prepare, args));
        }

        public void json(final String json) {
            printLog("json", json, null);
        }

        private void getCallerClass(StackTraceCallback callback) {
            try {
                throw new Exception();
            } catch (Exception e) {
                StackTraceElement[] entries = e.getStackTrace();

                /**
                 * entries[0] 是本方法
                 * entries[1] 是 Logger 类中的成员方法
                 * entries[2] 是 L 类中的静态方法代码或者直接调用 Logger 成员方法的代码
                 * entries[3] 是调用 L 类中静态方法的代码
                 */
                String fullClassName = entries[mMethodOffset].getClassName();
                String callClassName = getCallerClassName(fullClassName);
                if (isShowPath) {
                    callback.call(callClassName, entries[mMethodOffset]);
                } else {
                    callback.call(callClassName, null);
                }
            }
        }

        public boolean getShowPath() {
            return isShowPath;
        }

        public Logger setShowPath(boolean showPath) {
            isShowPath = showPath;
            return this;
        }
    }
}
