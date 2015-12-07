package com.eyeem.routervalidator

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails
import org.yaml.snakeyaml.Yaml

/**
 * Task that validates a router by creating a correspondent java file
 */
class ValidatorTask extends DefaultTask {

    @Input String packageName
    @Input String decoratorsPackageName
    @Input String holdersPackageName

    /**
     * The output directory.
     */
    @OutputDirectory
    File outputDir

    /**
     * File containing the c
     */
    String yamlFile

    @TaskAction
    def validate() {
        ArrayList<RouterNode> nodes = new ArrayList<>();

        String yamlString = new File(yamlFile).getText('UTF-8')
        Map<String, Object> routerMap = (Map<String, Object>) new Yaml().load(yamlString);

        routerMap.each {
            key, value -> nodes.add(new RouterNode(
                    path : key,
                    type : value.type,
                    decoratorsPackageName : decoratorsPackageName,
                    holdersPackageName : decoratorsPackageName
            ).parse());
        }

        def templateData = [
                nodes : nodes
        ]

        ClassEmitter emitter = [template    : Templates.load(getClass().getClassLoader(), "SIMPLE"),
                                templateData: templateData,
                                baseDir     : outputDir,
                                packageName : packageName,
                                className   : "RouterConstants"]
        emitter.print()
    }
}