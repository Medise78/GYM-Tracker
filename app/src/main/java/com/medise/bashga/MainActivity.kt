package com.medise.bashga

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.medise.bashga.presentation.add_screen.AddScreen
import com.medise.bashga.presentation.date_pay_expired.DatePayExpired
import com.medise.bashga.presentation.expierd_person.ExpiredPerson
import com.medise.bashga.presentation.expierd_person.ExpiredViewModel
import com.medise.bashga.presentation.half_price_screen.HalfPriceScreen
import com.medise.bashga.presentation.home_screen.HomeScreen
import com.medise.bashga.ui.theme.BashgaTheme
import com.medise.bashga.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewModel:ExpiredViewModel by viewModels()

        setContent {
            BashgaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    val navController = rememberNavController()
                    val scaffoldState = rememberScaffoldState()
                    var bottomBarState by rememberSaveable {
                        mutableStateOf(true)
                    }

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        val packageName = baseContext.packageName
//                        val pm:PowerManager =
//                            baseContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//                        if (!pm.isIgnoringBatteryOptimizations(packageName)){
//                            val intent = Intent()
//                            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            intent.data = Uri.parse("package:$packageName")
//                            baseContext.startActivity(intent)
//                        }
//                    }
                    
                    Scaffold(
                        scaffoldState = scaffoldState,
                        bottomBar = {
                            BottomNav(navController = navController, isVisible = bottomBarState)
                        }
                    ) { paddingValues ->
                        paddingValues.calculateBottomPadding()
                        NavHost(
                            navController = navController,
                            startDestination = Routes.HomeScreen.route
                        ) {
                            composable(Routes.HomeScreen.route) {
                                bottomBarState = true
                                HomeScreen(navController = navController)
                            }
                            composable(Routes.AddScreen.route) {
                                bottomBarState = false
                                AddScreen(navController = navController)
                            }
                            composable(Routes.ExpiredPersons.route) {
                                bottomBarState = true
                                ExpiredPerson(navController = navController)
                            }
                            composable(Routes.DatePayExpired.route) {
                                bottomBarState = true
                                DatePayExpired(navController = navController)
                            }
                            composable(Routes.HalfPrice.route){
                                HalfPriceScreen(navController = navController)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    private fun openSettings(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
    }
}

@Composable
fun BottomNav(
    navController: NavController,
    isVisible: Boolean = true
) {

    if (isVisible) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BottomNavigation(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val list = Routes.items.minus(Routes.AddScreen)

                list.forEach { item ->
                    BottomNavigationItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = false
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            item.icon?.let { Icon(imageVector = it, contentDescription = "icons") }
                        },
                        label = {
                            Text(text = item.title, color = Color.Black)
                        },
                        alwaysShowLabel = true,
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.Black.copy(0.5f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


