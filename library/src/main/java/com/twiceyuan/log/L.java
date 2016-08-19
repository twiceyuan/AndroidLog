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
    private static final int DEFAULT_OFFSET       = 6; // 在本类的静态方法中，默认有 6 层方法栈

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

    public static void i(String message, Throwable tr) {
        sDefaultLevelLogger.i(message, tr);
    }

    public static void w(String message, Throwable tr) {
        sDefaultLevelLogger.w(message, tr);
    }

    public static void e(String message, Throwable tr) {
        sDefaultLevelLogger.e(message, tr);
    }

    public static void v(String message, Throwable tr) {
        sDefaultLevelLogger.v(message, tr);
    }

    public static void wtf(String message, Throwable tr) {
        sDefaultLevelLogger.wtf(message, tr);
    }

    public static void e(Throwable throwable) {
        sDefaultLevelLogger.e(throwable);
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
                return new JSONObject(json).toString(JSON_PRETTIFY_INDENT);
            }
            if (json.startsWith("[")) {
                return new JSONArray(json).toString(JSON_PRETTIFY_INDENT);
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

        private void filterLog(Callback callback) {
            if (mToggle && sGlobalToggle) {
                callback.call();
            }
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

        public void i(final String message) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, message);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, message));
                        }
                    });
                }
            });
        }

        public void w(final String message) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.w(TextUtils.isEmpty(mTag) ? callerName : mTag, message);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.w(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, message));
                        }
                    });
                }
            });
        }

        public void e(final String message) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.e(TextUtils.isEmpty(mTag) ? callerName : mTag, message);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.e(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, message));
                        }
                    });
                }
            });
        }

        public void v(final String message) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.v(TextUtils.isEmpty(mTag) ? callerName : mTag, message);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.v(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, message));
                        }
                    });
                }
            });
        }

        public void wtf(final String message) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.wtf(TextUtils.isEmpty(mTag) ? callerName : mTag, message);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.wtf(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, message));
                        }
                    });
                }
            });
        }

        public void e(final Throwable throwable) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.e(TextUtils.isEmpty(mTag) ? callerName : mTag, throwable.getMessage(), throwable);
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                        }
                    });
                }
            });
        }

        public void i(final String prepare, final Object... args) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, String.format(prepare, args));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, String.format(prepare, args)));
                        }
                    });
                }
            });
        }

        public void e(final String prepare, final Object... args) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.e(TextUtils.isEmpty(mTag) ? callerName : mTag, String.format(prepare, args));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.e(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, String.format(prepare, args)));
                        }
                    });
                }
            });
        }

        public void w(final String prepare, final Object... args) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.w(TextUtils.isEmpty(mTag) ? callerName : mTag, String.format(prepare, args));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.w(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, String.format(prepare, args)));
                        }
                    });
                }
            });
        }

        public void v(final String prepare, final Object... args) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.v(TextUtils.isEmpty(mTag) ? callerName : mTag, String.format(prepare, args));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.v(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, String.format(prepare, args)));
                        }
                    });
                }
            });
        }

        public void wtf(final String prepare, final Object... args) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.wtf(TextUtils.isEmpty(mTag) ? callerName : mTag, String.format(prepare, args));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.wtf(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, String.format(prepare, args)));
                        }
                    });
                }
            });
        }

        public void json(final String json) {
            filterLog(new Callback() {
                @Override
                public void call() {
                    getCallerClass(new ClassNameCallback() {
                        @Override
                        public void call(String callerName) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, prettifyJsonWithBorder(json));
                        }
                    }, new StackTraceCallback() {
                        @Override
                        public void call(String callerName, StackTraceElement element) {
                            Log.i(TextUtils.isEmpty(mTag) ? callerName : mTag, printWithPath(callerName, element, prettifyJson(json)));
                        }
                    });
                }
            });
        }

        private void getCallerClass(ClassNameCallback normalCallback, StackTraceCallback showPathCallback) {
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
                int lastPointIndex = fullClassName.lastIndexOf(".");
                if (lastPointIndex > -1) {
                    if (isShowPath) {
                        showPathCallback.call(fullClassName.substring(lastPointIndex + 1), entries[mMethodOffset]);
                    } else {
                        normalCallback.call(fullClassName.substring(lastPointIndex + 1));
                    }
                } else {
                    if (isShowPath) {
                        showPathCallback.call(fullClassName, entries[mMethodOffset]);
                    } else {
                        normalCallback.call(fullClassName);
                    }
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

        interface ClassNameCallback {
            void call(String callerName);
        }

        interface StackTraceCallback {
            void call(String callerName, StackTraceElement element);
        }

        interface Callback {
            void call();
        }
    }

    static class Log {

        public static void i(String tag, String message) {
            callNativeLog("i", tag, message, null);
        }

        public static void v(String tag, String message) {
            callNativeLog("v", tag, message, null);
        }

        public static void w(String tag, String message) {
            callNativeLog("w", tag, message, null);
        }

        public static void wtf(String tag, String message) {
            callNativeLog("wtf", tag, message, null);
        }

        public static void e(String tag, String message) {
            callNativeLog("e", tag, message, null);
        }

        public static void e(String tag, String message, Throwable throwable) {
            callNativeLog("e", tag, message, throwable);
        }

        private static void callNativeLog(String methodName, String tag, String message, Throwable throwable) {
            try {
                Class logClass = Class.forName("android.util.Log");
                if (throwable == null) {
                    //noinspection unchecked
                    Method method = logClass.getMethod(methodName, String.class, String.class);
                    method.invoke(null, tag, message);
                } else {
                    //noinspection unchecked
                    Method method = logClass.getMethod(methodName, String.class, String.class, Throwable.class);
                    method.invoke(null, tag, message, throwable);
                }
            } catch (Exception e) {
                throw new IllegalStateException("本项目只能在 Android 平台中使用: ");
            }
        }
    }

    static class TextUtils {
        public static boolean isEmpty(String str) {
            return str == null || str.length() == 0;
        }
    }

    static class JSONObject {

        private Class  mJsonObjectClass;
        private Object mJsonObject;

        public JSONObject(String string) {
            try {

                mJsonObjectClass = Class.forName("org.json.JSONObject");
                //noinspection unchecked
                Constructor constructor = mJsonObjectClass.getConstructor(String.class);
                mJsonObject = constructor.newInstance(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String toString(int indentType) {
            try {
                //noinspection unchecked
                Method method = mJsonObjectClass.getDeclaredMethod("toString", int.class);
                return (String) method.invoke(mJsonObject, indentType);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static class JSONArray {

        private Class  mJsonArrayClass;
        private Object mJsonArray;

        public JSONArray(String string) {
            try {
                mJsonArrayClass = Class.forName("org.json.JSONArray");
                //noinspection unchecked
                Constructor constructor = mJsonArrayClass.getConstructor(String.class);
                mJsonArray = constructor.newInstance(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String toString(int indentType) {
            try {
                //noinspection unchecked
                Method method = mJsonArrayClass.getDeclaredMethod("toString", int.class);
                return (String) method.invoke(mJsonArray, indentType);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
