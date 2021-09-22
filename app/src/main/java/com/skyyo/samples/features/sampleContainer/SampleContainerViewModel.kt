package com.skyyo.samples.features.sampleContainer

import androidx.lifecycle.ViewModel
import com.skyyo.samples.application.Screens
import com.skyyo.samples.utils.eventDispatchers.NavigationDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


@HiltViewModel
class SampleContainerViewModel @Inject constructor(
    private val navigationDispatcher: NavigationDispatcher
) :
    ViewModel() {

    private val _events = Channel<SampleContainerScreenEvent>()
    val events = _events.receiveAsFlow()

    //    fun goHome() = navigationDispatcher.emit {
//        it.navigate(Screens.DogFeed.route) {
//            popUpTo(Screens.SampleContainer.route) {
//                inclusive = true
//            }
//        }
//    }
    fun goPaginationSimple() = navigationDispatcher.emit {
        it.navigate(Screens.Cats.route)
    }

    fun goPaginationRoom() = navigationDispatcher.emit {
        it.navigate(Screens.CatsRoom.route)
    }

    fun goPaginationPaging() = navigationDispatcher.emit {
        it.navigate(Screens.CatsPaging.route)
    }

    fun goPaginationPagingRoom() = navigationDispatcher.emit {
        it.navigate(Screens.CatsPagingRoom.route)
    }

    fun goMap() = navigationDispatcher.emit {
        it.navigate(Screens.Map.route)
    }

    fun goForceTheme() = navigationDispatcher.emit {
        it.navigate(Screens.ForceTheme.route)
    }

    fun goCameraX() = navigationDispatcher.emit {
        it.navigate(Screens.CameraX.route)
    }

    fun goBottomSheetDestination() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheet.route)
    }

    fun goBottomSheetsContainer() = navigationDispatcher.emit {
        it.navigate(Screens.ModalBottomSheet.route)
    }

    fun goBottomSheetsScaffold() = navigationDispatcher.emit {
        it.navigate(Screens.BottomSheetScaffold.route)
    }

    fun goViewPager() = navigationDispatcher.emit {
        it.navigate(Screens.ViewPager.route)
    }

    fun goNavWithResultSample() = navigationDispatcher.emit {
        it.navigate(Screens.DogFeed.route)
    }

    fun goStickyHeaders() = navigationDispatcher.emit {
        it.navigate(Screens.StickyHeader.route)
    }

    fun goInputAutoValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationAuto.route)
    }

    fun goInputDebounceValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationDebounce.route)
    }

    fun goInputManualValidation() = navigationDispatcher.emit {
        it.navigate(Screens.InputValidationManual.route)
    }

    fun goAnimations() = navigationDispatcher.emit {
        it.navigate(Screens.Animations.route)
    }

    fun goOtp() = navigationDispatcher.emit {
        it.navigate(Screens.Otp.route)
    }

    fun goNestedHorizontalLists() = navigationDispatcher.emit {
        it.navigate(Screens.AppBarElevation.route)
    }

    fun goAutoScroll() = navigationDispatcher.emit {
        it.navigate(Screens.AutoScroll.route)
    }

    fun goTable() = navigationDispatcher.emit {
        it.navigate(Screens.Table.route)
    }

    fun goParallaxEffect() = navigationDispatcher.emit {
        it.navigate(Screens.ParallaxEffect.route)
    }

    fun goCustomView() = navigationDispatcher.emit {
        it.navigate(Screens.CustomView.route)
    }
}
