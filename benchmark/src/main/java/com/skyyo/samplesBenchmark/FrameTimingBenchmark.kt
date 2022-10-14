package com.skyyo.samplesBenchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ITERATIONS = 10

@LargeTest
@RunWith(AndroidJUnit4::class)
class FrameTimingBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollViewPager() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM,
            iterations = ITERATIONS,
            setupBlock = {
                val intent = Intent(ACTION_OPEN_FEATURE).apply {
                    putExtra(EXTRA_FEATURE_ROUTE, "viewPager")
                }
                startActivityAndWait(intent)
            }
        ) {
            /**
             * Compose does not have view IDs so we cannot directly access composables from UiAutomator.
             * To access a composable we need to set:
             * 1) Modifier.semantics { testTagsAsResourceId = true } once, high in the compose hierarchy
             * 2) Add Modifier.testTag("someIdentifier") to all of the composables you want to access
             *
             * Once done that, we can access the composable using By.res("someIdentifier")
             */
            val pager = device.findObject(By.res("horizontalPager"))
            // Set gesture margin to avoid triggering gesture navigation
            // with input events from automation.
            pager.setGestureMargin(device.displayWidth / 5)
            repeat(3) { pager.fling(Direction.RIGHT) }
        }
    }

    @Test
    fun scrollInfiniteViewPager() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM,
            iterations = ITERATIONS,
            setupBlock = {
                val intent = Intent(ACTION_OPEN_FEATURE).apply {
                    putExtra(EXTRA_FEATURE_ROUTE, "infiniteViewPager")
                }
                startActivityAndWait(intent)
            }
        ) {
            val pager = device.findObject(By.res("horizontalPager"))
            pager.setGestureMargin(device.displayWidth / 5)
            repeat(3) { pager.fling(Direction.RIGHT) }
        }
    }

    @Test
    fun switchStatePersistentBottomSheet() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM,
            iterations = ITERATIONS,
            setupBlock = {
                val intent = Intent(ACTION_OPEN_FEATURE).apply {
                    putExtra(EXTRA_FEATURE_ROUTE, "bottomSheetScaffold")
                }
                startActivityAndWait(intent)
            }
        ) {
            val switchBottomSheetStateButton = device.findObject(By.res("switchButton"))
            repeat(3) { switchBottomSheetStateButton.click() }
        }
    }
}