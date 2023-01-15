package com.medise.bashga.presentation.date_pay_expired

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medise.bashga.BottomNav
import com.medise.bashga.presentation.home_screen.PersonCard
import com.medise.bashga.util.ActivityPerson
import com.medise.bashga.util.DateConverterToPersian
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePayExpired(
    viewModel: DatePayExpiredViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val dateConverterToPersian = DateConverterToPersian()
    val date = DateConverterToPersian()

    var expandedState by remember {
        mutableStateOf(false)
    }

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
            dateConverterToPersian.GregorianToPersian(
                LocalDate.now().year,
                LocalDate.now().monthValue,
                LocalDate.now().dayOfMonth
            )
            val personFilter = state.filter {
                LocalDate.parse(it.endDay).isBefore(
                    LocalDate.of(
                        dateConverterToPersian.year,
                        dateConverterToPersian.month,
                        dateConverterToPersian.day
                    )
                )
            }
            LazyColumn(contentPadding = PaddingValues(bottom = 65.dp)) {
                items(personFilter) { person ->

                    person.payStatus = ActivityPerson.NOTPAID
                    val dateRange = dateConverterToPersian.day.minus(LocalDate.parse(person.endDay)?.dayOfMonth ?: 0)

                    PersonCard(
                        person = person,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        title = "وضعیت پرداخت:",
                        isExpanded = expandedState,
                        canExpand = true,
                        onNegativeClick = {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("${dateRange} روز از موعد پرداخت گذشته است.")
                            }
                        },
                        onPositiveClick = {
                            viewModel.updatePerson(
                                person.copy(
                                    startDay = LocalDate.parse(person.startDay)?.plusMonths(1).toString(),
                                    endDay = LocalDate.parse(person.endDay).plusMonths(1).toString(),
                                    payStatus = ActivityPerson.PAID,
                                    isPayed = true,
                                    remainDate = Period.between(
                                        LocalDate.of(date.year, date.month, date.day),
                                        LocalDate.parse(
                                            LocalDate.parse(person.endDay).plusMonths(1)
                                                .toString()
                                        )
                                    ).days.toString()
                                )
                            )
                        }
                    ) {
                        viewModel.deletePerson(person)
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("${person.name} حذف شد")
                        }
                    }
                }
            }
        }
    }
}

