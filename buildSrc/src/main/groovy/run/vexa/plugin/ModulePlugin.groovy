package run.vexa.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import run.vexa.plugin.compile.CompileArgsPlugin

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
