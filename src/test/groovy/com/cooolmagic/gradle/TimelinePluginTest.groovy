package com.cooolmagic.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class TimelinePluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.cooolmagic.gradle.timeline'

        assertTrue(project.timeline instanceof TimelinePluginExtension)
    }
}
