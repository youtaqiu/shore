package run.vexa.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import run.vexa.plugin.info.BootPlugin

/**
 * service plugin.
 * @author youta
 * */
class ServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply(JavaPlugin.class)
        project.pluginManager.apply(ModulePlugin.class)
        project.pluginManager.apply(BootPlugin.class)
    }

}
