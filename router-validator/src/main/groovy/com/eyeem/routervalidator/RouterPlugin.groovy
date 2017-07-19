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
        // there's some discussion here but unsure if we need to care about this
        // https://discuss.gradle.org/t/defaultsourcedirectoryset-alternative/15193
        // This is broken in gradle 4.0
//        project.android.sourceSets.all { sourceSet ->
//            sourceSet.extensions.create('router', DefaultSourceDirectorySet, 'router', project.fileResolver)
//        }

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
                    yamlFile = "${project.rootDir}/${project.router.path}"
                    packageName = project.router.packageName
                    holdersPackageName = project.router.holdersPackageName
                    decoratorsPackageName = project.router.decoratorsPackageName
                    resourcePackageName = project.router.resourcePackageName
                    configFor = project.router.configFor
                }

                variant.registerJavaGeneratingTask(validatorTask, validatorTask.outputDir)
                variant.addJavaSourceFoldersToModel(validatorTask.outputDir)
            }
        }
    }
}