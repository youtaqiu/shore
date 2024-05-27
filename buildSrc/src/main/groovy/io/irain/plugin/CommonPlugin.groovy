package io.irain.plugin


import io.irain.plugin.compile.ResourcesPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin

/**
 * @author youta
 * */
class CommonPlugin implements Plugin<Project>{

    void apply(Project project) {
        project.pluginManager.apply(JavaLibraryPlugin.class)
        project.pluginManager.apply(ModulePlugin.class)
        project.pluginManager.apply(ResourcesPlugin.class)
    }

}
