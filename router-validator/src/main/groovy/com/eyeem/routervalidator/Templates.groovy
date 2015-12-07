package com.eyeem.routervalidator

class Templates {
    static String load(ClassLoader classLoader, String filename) {
        classLoader.getResourceAsStream("templates/${filename}").text;
    }
}
