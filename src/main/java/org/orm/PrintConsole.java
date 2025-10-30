package org.orm;

import java.util.Arrays;

 class PrintConsole {
    private PrintConsole() {}
    public static void print(String s) {
        if (s != null&&Configure.IsWriteConsole) {
            System.out.println(s);
        }
    }
    public static void print(String s, Object... param) {
        if (s != null&&Configure.IsWriteConsole) {
            System.out.println(String.format("%s params: %s",s, param.length==0?"": Arrays.toString(param)));
        }
    }

}
