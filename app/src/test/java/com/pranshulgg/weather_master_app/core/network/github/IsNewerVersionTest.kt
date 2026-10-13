package com.pranshulgg.weather_master_app.core.network.github

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsNewerVersionTest {

    @Test
    fun installedVersionNewerThanRelease() {
        assertFalse(isNewerVersion(latestTag = "v3.10.6", currentTag = "v3.11.0"))
        assertFalse(isNewerVersion(latestTag = "v3.9.1", currentTag = "v3.10.0"))
        assertFalse(isNewerVersion(latestTag = "v2.7.1", currentTag = "v3.0.0-beta1"))
    }

    @Test
    fun sameVersion() {
        assertFalse(isNewerVersion(latestTag = "v3.10.6", currentTag = "v3.10.6"))
        assertFalse(isNewerVersion(latestTag = "v1.2.3", currentTag = "v1.2.3.0"))
    }

    @Test
    fun releaseNewerThanInstalled() {
        assertTrue(isNewerVersion(latestTag = "v3.11.0", currentTag = "v3.10.6"))
        assertTrue(isNewerVersion(latestTag = "v3.10.0", currentTag = "v3.9.9"))
        assertTrue(isNewerVersion(latestTag = "v4.0.0", currentTag = "v3.99.99"))
        assertTrue(isNewerVersion(latestTag = "v1.2.3.4", currentTag = "v1.2.3"))
    }

    @Test
    fun releaseNewerThanPreReleaseOfSameVersion() {
        assertTrue(isNewerVersion(latestTag = "v3.0.0", currentTag = "v3.0.0-beta3"))
        assertTrue(isNewerVersion(latestTag = "v3.0.0", currentTag = "3.0.0-alpha1"))
        assertTrue(isNewerVersion(latestTag = "v2.0.0", currentTag = "v2.0.0-beta.4"))
        assertTrue(isNewerVersion(latestTag = "v3.0.1", currentTag = "v3.0.0-beta3"))
    }

    @Test
    fun unparseableTagsFallBackToTextComparison() {
        assertTrue(isNewerVersion(latestTag = "latest", currentTag = "v3.10.6"))
        assertFalse(isNewerVersion(latestTag = "nightly", currentTag = "nightly"))
        assertTrue(isNewerVersion(latestTag = "v3.10.6", currentTag = ""))
    }
}
