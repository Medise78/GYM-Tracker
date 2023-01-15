package com.medise.bashga.presentation.expierd_person

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medise.bashga.BottomNav
import com.medise.bashga.presentation.home_screen.PersonCard
import com.medise.bashga.util.ActivityPerson
import com.medise.bashga.util.DateConverterToPersian
import com.medise.bashga.util.showNotification
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpiredPerson(
    viewModel: ExpiredViewModel = hiltViewModel(),
    navController: NavController,
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val date = DateConverterToPersian()

    var expandCard by remember {
        mutableStateOf(false)
    }

    viewModel.fetch()

    date.GregorianToPersian(
        LocalDate.now().year,
        LocalDate.now().monthValue,
        LocalDate.now().dayOfMonth
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomNav(navController = navController)
            }
        ) { paddingValues ->
            paddingValues.calculateBottomPadding()

            LazyColumn(contentPadding = PaddingValues(bottom = 65.dp)) {
                val p = state.filter {
                    LocalDate.parse(it.endDay)?.isEqual(
                        LocalDate.of(
                            date.year,
                            date.month,
                            date.day
                        )
                    ) == true
                }
                items(p) { persons ->

                    persons.payStatus = ActivityPerson.NOTPAID
                    PersonCard(
                        title = "وضعیت پرداخت:",
                        canExpand = true,
                        isExpanded = expandCard,
                        person = persons,
                        shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
                        onPositiveClick = {
                            viewModel.updatePerson(
                                persons.copy(
                                    name = persons.name,
                                    lastName = persons.lastName,
                                    startDay = LocalDate.parse(persons.startDay)?.plusMonths(1)
                                        .toString(),
                                    endDay = LocalDate.parse(persons.endDay).plusMonths(1)
                                        .toString(),
                                    payStatus = ActivityPerson.PAID,
                                    isPayed = true,
                                    personImage = persons.personImage,
                                    remainDate = ChronoUnit.DAYS.between(
                                        LocalDate.of(date.year, date.month, date.day),
                                        LocalDate.parse(
                                            LocalDate.parse(persons.endDay).plusMonths(1)
                                                .toString()
                                        )
                                    ).toString()
                                )
                            )
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("${persons.name} تمدید شد")
                            }
                        },
                        onNegativeClick = {
                            viewModel.updatePerson(
                                persons.copy(
                                    name = persons.name,
                                    lastName = persons.lastName,
                                    startDay = persons.startDay,
                                    endDay = persons.endDay,
                                    payStatus = ActivityPerson.NOTPAID,
                                    isPayed = false
                                )
                            )
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("${persons.name} به لیست پرداخت نکرده ها اضافه شد")
                            }
                        }
                    ) {
                        viewModel.deletePerson(persons)
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("${persons.name} حذف شد")
                        }
                    }
                }
            }
        }
    }
}

