package com.skyyo.samples.features.sampleContainer

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Destination
import com.skyyo.samples.application.ProfileGraph
import com.skyyo.samples.utils.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SampleContainerViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher
) : ViewModel() {

    fun goPaginationSimple() = navigationDispatcher.emit {
        it.navigate(Destination.Cats.route)
    }

    fun goPaginationRoom() = navigationDispatcher.emit {
        it.navigate(Destination.CatsRoom.route)
    }

    fun goPaginationPaging() = navigationDispatcher.emit {
        it.navigate(Destination.CatsPaging.route)
    }

    fun goPaginationPagingRoom() = navigationDispatcher.emit {
        it.navigate(Destination.CatsPagingRoom.route)
    }

    fun goMap() = navigationDispatcher.emit {
        it.navigate(Destination.Map.route)
    }

    fun goForceTheme() = navigationDispatcher.emit {
        it.navigate(Destination.ForceTheme.route)
    }

    fun goCameraX() = navigationDispatcher.emit {
        it.navigate(Destination.CameraX.route)
    }

    fun goBottomSheetDestination() = navigationDispatcher.emit {
        it.navigate(Destination.BottomSheet.route)
    }

    fun goBottomSheetsContainer() = navigationDispatcher.emit {
        it.navigate(Destination.ModalBottomSheet.route)
    }

    fun goBottomSheetsScaffold() = navigationDispatcher.emit {
        it.navigate(Destination.BottomSheetScaffold.route)
    }

    fun goViewPager() = navigationDispatcher.emit {
        it.navigate(Destination.ViewPager.route)
    }

    fun goInfiniteViewPager() = navigationDispatcher.emit {
        it.navigate(Destination.InfiniteViewPager.route)
    }

    fun goNavigationWithValuesSimple() = navigationDispatcher.emit {
        it.navigate(Destination.DogFeed.route)
    }

    fun goNavigationWithValuesObject() = navigationDispatcher.emit {
        it.navigate(Destination.CatFeed.route)
    }

    fun goStickyHeaders() = navigationDispatcher.emit {
        it.navigate(Destination.StickyHeader.route)
    }

    fun goInputAutoValidation() = navigationDispatcher.emit {
        it.navigate(Destination.InputValidationAuto.route)
    }

    fun goInputDebounceValidation() = navigationDispatcher.emit {
        it.navigate(Destination.InputValidationDebounce.route)
    }

    fun goInputManualValidation() = navigationDispatcher.emit {
        it.navigate(Destination.InputValidationManual.route)
    }

    fun goOtp() = navigationDispatcher.emit {
        it.navigate(Destination.Otp.route)
    }

    fun goNestedHorizontalLists() = navigationDispatcher.emit {
        it.navigate(Destination.AppBarElevation.route)
    }

    fun goAutoScroll() = navigationDispatcher.emit {
        it.navigate(Destination.AutoScroll.route)
    }

    fun goTable() = navigationDispatcher.emit {
        it.navigate(Destination.Table.route)
    }

    fun goParallaxEffect() = navigationDispatcher.emit {
        it.navigate(Destination.ParallaxEffect.route)
    }

    fun goCustomView() = navigationDispatcher.emit {
        it.navigate(Destination.CustomView.route)
    }

    fun goMarqueeText() = navigationDispatcher.emit {
        it.navigate(Destination.MarqueeText.route)
    }

    fun goHiltComposeSharedViewModel() = navigationDispatcher.emit {
        it.navigate(ProfileGraph.Profile.route)
    }

    fun goQrScanning() = navigationDispatcher.emit {
        it.navigate(Destination.QrCodeScanning.route)
    }

    fun goScrollAnimation1() = navigationDispatcher.emit {
        it.navigate(Destination.ScrollAnimation1.route)
    }

    fun goSnackbar() = navigationDispatcher.emit {
        it.navigate(Destination.Snackbar.route)
    }

    fun goSnap() = navigationDispatcher.emit {
        it.navigate(Destination.Snap.route)
    }

    fun goBottomBar() = navigationDispatcher.emit {
        it.navigate(Destination.BottomBar.route)
    }

    fun goNavigationDrawer() = navigationDispatcher.emit {
        it.navigate(Destination.Drawer.route)
    }

    fun goGradientScroll() = navigationDispatcher.emit {
        it.navigate(Destination.GradientScroll.route)
    }

    fun goNoticeableScrollableRow() = navigationDispatcher.emit {
        it.navigate(Destination.NoticeableScrollableRow.route)
    }

    fun goExoPlayerColumnReference() = navigationDispatcher.emit {
        it.navigate(Destination.ExoPlayerColumnReference.route)
    }

    fun goExoPlayerColumnIndexed() = navigationDispatcher.emit {
        it.navigate(Destination.ExoPlayerColumnIndexed.route)
    }

    fun goExoPlayerColumnAutoplay() = navigationDispatcher.emit {
        it.navigate(Destination.ExoPlayerColumnAutoplay.route)
    }

    fun goExoPlayerColumnDynamicThumb() = navigationDispatcher.emit {
        it.navigate(Destination.ExoPlayerColumnDynamicThumb.route)
    }

    fun goDominantColor() = navigationDispatcher.emit {
        it.navigate(Destination.DominantColor.route)
    }

    fun goZoomable() = navigationDispatcher.emit {
        it.navigate(Destination.Zoomable.route)
    }

    fun goPdfViewer() = navigationDispatcher.emit {
        it.navigate(Destination.PdfViewer.route)
    }

    fun goAutoFill() = navigationDispatcher.emit {
        it.navigate(Destination.AutoFill.route)
    }
}
