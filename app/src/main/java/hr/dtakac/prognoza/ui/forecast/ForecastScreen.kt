package hr.dtakac.prognoza.ui.forecast

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hr.dtakac.prognoza.presentation.ForecastViewModel
import hr.dtakac.prognoza.ui.forecast.coming.ComingScreen
import hr.dtakac.prognoza.ui.theme.PrognozaTheme
import hr.dtakac.prognoza.ui.forecast.today.TodayScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: ForecastViewModel = hiltViewModel()
) {
    // Refresh state every time screen is re-entered
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.getState()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val state by remember { viewModel.state }
    val forecast = state.forecast ?: return

    PrognozaTheme(forecast.today.shortDescription) {
        val colorAnimationSpec = remember {
            tween<Color>(durationMillis = 1000)
        }
        val contentColor by animateColorAsState(
            targetValue = PrognozaTheme.colors.onBackground.copy(
                alpha = 0.87f
            ),
            animationSpec = colorAnimationSpec
        )
        val backgroundColor by animateColorAsState(
            targetValue = PrognozaTheme.colors.background,
            animationSpec = colorAnimationSpec
        )

        setStatusAndNavigationBarColors(backgroundColor)

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()

        var toolbarPlaceVisible by remember { mutableStateOf(false) }
        var toolbarDateTimeVisible by remember { mutableStateOf(false) }
        var toolbarTemperatureVisible by remember { mutableStateOf(false) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ForecastDrawerContent(
                    backgroundColor = backgroundColor,
                    contentColor = contentColor,
                    onTodayClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("today") {
                                popUpTo("today") { inclusive = true }
                            }
                        }
                    },
                    onComingClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("coming") {
                                popUpTo("coming") { inclusive = true }
                            }
                            toolbarPlaceVisible = true
                            toolbarDateTimeVisible = true
                            toolbarTemperatureVisible = true
                        }
                    },
                    onPlacePickerClick = {
                        // todo
                    },
                    onSettingsClick = {
                        // todo
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(horizontal = 24.dp)
                ) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        ForecastToolbar(
                            place = forecast.place.asString(),
                            placeVisible = toolbarPlaceVisible,
                            dateTime = forecast.today.time.asString(),
                            dateTimeVisible = toolbarDateTimeVisible,
                            temperature = forecast.today.temperature.asString(),
                            temperatureVisible = toolbarTemperatureVisible,
                            modifier = Modifier.background(backgroundColor),
                            onMenuClicked = { scope.launch { drawerState.open() } }
                        )

                        NavHost(navController = navController, startDestination = "today") {
                            composable("today") {
                                TodayScreen(
                                    state = forecast.today,
                                    place = forecast.place,
                                    onPlaceVisibilityChange = { visibilityPercent ->
                                        toolbarPlaceVisible = visibilityPercent == 0f
                                    },
                                    onDateTimeVisibilityChange = { visibilityPercent ->
                                        toolbarDateTimeVisible = visibilityPercent == 0f
                                    },
                                    onTemperatureVisibilityChange = { visibilityPercent ->
                                        toolbarTemperatureVisible = visibilityPercent <= 50f
                                    }
                                )
                            }
                            composable("coming") {
                                ComingScreen(state = forecast.coming)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun setStatusAndNavigationBarColors(color: Color) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color)
    systemUiController.setNavigationBarColor(color)
}