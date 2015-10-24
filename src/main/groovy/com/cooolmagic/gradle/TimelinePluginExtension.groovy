package com.cooolmagic.gradle

class TimelinePluginExtension {
    boolean enabled = true
    long thresholdTimeMillis = 1 * 1000
    List alwaysInclude = []
    List alwaysExclude = []
    String reportsDir = null
    List timelineEntries = []
}
