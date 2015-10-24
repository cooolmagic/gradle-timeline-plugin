package com.cooolmagic.gradle

class TimelinePluginExtension {
    boolean enabled = true
    long thresholdTimeMillis = 5 * 1000
    List alwaysInclude = []
    List alwaysExclude = []
    String reportsDir = null
    List timelineEntries = []
}
