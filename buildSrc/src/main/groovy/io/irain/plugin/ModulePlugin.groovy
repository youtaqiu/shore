package io.irain.plugin


import io.irain.plugin.compile.CompileArgsPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author youta
 * */
class ModulePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.pluginManager.apply(CompileArgsPlugin.class)
        project.pluginManager.apply(CorePlugin.class)
    }

}
