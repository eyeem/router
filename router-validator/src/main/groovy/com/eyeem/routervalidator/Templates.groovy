package com.eyeem.routervalidator

import java.util.zip.ZipFile

class Templates {
    static String load(String filename) {

        def templateFileUrl = Templates.class.getClassLoader().getResource("templates/${filename}")
        if (templateFileUrl == null) {
            throw new FileNotFoundException("File not found: $filename")
        }

        templateFileUrl = new URL(templateFileUrl.toString())

        try {
            return templateFileUrl.openStream().getText("UTF-8")
        } catch (FileNotFoundException e) {
            // fallback to read JAR directly
            URI jarFile = (templateFileUrl.openConnection() as JarURLConnection).jarFileURL.toURI()
            ZipFile zip
            try {
                zip = new ZipFile(new File(jarFile))
            } catch (FileNotFoundException ex) {
                System.err.println("[router-validator] no router-validator.jar. run `./gradlew router-validator:jar` first.")
                throw ex
            }
            return zip.getInputStream((zip.getEntry(filename))).getText("UTF-8")
        }
    }
}
