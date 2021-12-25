package top.pressed.argmous.spring.util;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodUtil {

    public static String getFullName(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName() + "." + Arrays.toString(method.getParameterTypes());
    }
}
