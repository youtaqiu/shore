package sh.rime.plugin


import sh.rime.plugin.dependency.CompileProcessorPlugin
import sh.rime.plugin.dependency.ManagementPlugin
import sh.rime.plugin.dependency.OptionalPlugin
import sh.rime.plugin.tasks.DeleteExpand
import sh.rime.plugin.info.ManifestPlugin
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
