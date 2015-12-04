package com.eyeem.routervalidator

import groovy.text.GStringTemplateEngine

class ClassEmitter {

    def template
    def templateData
    File baseDir
    String packageName
    String className

    private final GStringTemplateEngine engine = new GStringTemplateEngine()

    public void print() {
        def targetDir = makeFileDir()
        new FileWriter("${targetDir}/${className}.java").withWriter { Writer writer ->
            engine.createTemplate(template)
                    .make(access: templateData, packageName: packageName, className: className)
                    .writeTo(writer)
        }
    }

    private String makeFileDir() {
        String packageAsDir = packageName.replaceAll(~/\./, "/")
        def fileDir = new File(baseDir, packageAsDir)
        fileDir.mkdirs()
        return fileDir.absolutePath
    }
}
