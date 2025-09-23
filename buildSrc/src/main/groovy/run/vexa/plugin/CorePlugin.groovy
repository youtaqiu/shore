package run.vexa.plugin


import run.vexa.plugin.dependency.CompileProcessorPlugin
import run.vexa.plugin.dependency.ManagementPlugin
import run.vexa.plugin.dependency.OptionalPlugin
import run.vexa.plugin.tasks.DeleteExpand
import run.vexa.plugin.info.ManifestPlugin
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
