package com.eyeem.routervalidator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.file.DefaultSourceDirectorySet

class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('router', RouterPluginExtension)

        // Add 'router' as a source set extension
        project.android.sourceSets.all { sourceSet ->
            sourceSet.extensions.create('router', DefaultSourceDirectorySet, 'router', project.fileResolver)
        }

        project.afterEvaluate {
            // find variants in the project
            def variants = null
            if (project.android.hasProperty('applicationVariants')) {
                variants = project.android.applicationVariants
            }
            else if (project.android.hasProperty('libraryVariants')) {
                variants = project.android.libraryVariants
            }
            else {
                throw new IllegalStateException('Android project must have applicationVariants or libraryVariants!')
            }

            // run code generation over the variants
            variants.all { variant ->
                File sourceFolder = project.file("${project.buildDir}/generated/source/router/${variant.dirName}")
                Task validatorTask = project.task("validateRouterFor${variant.name.capitalize()}", type: ValidatorTask) {
                    outputDir = sourceFolder
                    yamlFile = project.router.path
                    packageName = project.router.packageName
                }

                variant.registerJavaGeneratingTask(validatorTask, validatorTask.outputDir)
                variant.addJavaSourceFoldersToModel(validatorTask.outputDir)
            }
        }
    }
}