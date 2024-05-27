package io.irain.plugin


import io.irain.plugin.dependency.CompileProcessorPlugin
import io.irain.plugin.dependency.ManagementPlugin
import io.irain.plugin.dependency.OptionalPlugin
import io.irain.plugin.tasks.DeleteExpand
import io.irain.plugin.info.ManifestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author youta
 * */
class CorePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.pluginManager.apply(DeleteExpand.class)
        project.pluginManager.apply(ManagementPlugin.class)
        project.pluginManager.apply(OptionalPlugin.class)
        project.pluginManager.apply(CompileProcessorPlugin.class)
        project.pluginManager.apply(ManifestPlugin.class)
    }

}
