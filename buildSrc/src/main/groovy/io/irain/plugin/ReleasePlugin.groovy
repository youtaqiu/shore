package io.irain.plugin

import net.researchgate.release.GitAdapter
import net.researchgate.release.ReleaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author youta
 * */
@SuppressWarnings("unused")
class ReleasePlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.pluginManager.apply(net.researchgate.release.ReleasePlugin.class)
		def release = project.extensions.getByType(ReleaseExtension.class)
		release.failOnCommitNeeded = false
		release.failOnUnversionedFiles = false
		String tagTemplate = "v"+"\$version"
		release.tagTemplate = tagTemplate
		def scmAdapter = List.of(GitAdapter)
		release.scmAdapters = scmAdapter
		GitAdapter.GitConfig gitConfig = new GitAdapter.GitConfig()
		def requireBranch = project.property("release.branch") as String
		gitConfig.requireBranch = requireBranch
		release.git = gitConfig
	}
}
