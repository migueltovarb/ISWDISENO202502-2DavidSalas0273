package com.vetcarepro.cli;

/**
 * Imprime menús y textos con colores ANSI.
 */
public class MenuRenderer {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";

    public void title(String text) {
        System.out.println(BLUE + text + RESET);
    }

    public void success(String text) {
        System.out.println(GREEN + text + RESET);
    }

    public void error(String text) {
        System.out.println(RED + text + RESET);
    }

    public void warning(String text) {
        System.out.println(YELLOW + text + RESET);
    }

    public void separator() {
        System.out.println("-------------------------------------");
    }
}
