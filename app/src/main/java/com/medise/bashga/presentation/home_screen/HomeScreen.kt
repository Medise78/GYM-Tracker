@file:OptIn(ExperimentalGlideComposeApi::class)

package com.medise.bashga.presentation.home_screen

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.medise.bashga.BottomNav
import com.medise.bashga.R
import com.medise.bashga.domain.model.PersonEntity
import com.medise.bashga.util.ActivityPerson
import com.medise.bashga.util.DateConverterToPersian
import com.medise.bashga.util.Routes
import java.lang.reflect.Type
import java.time.LocalDate
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val date = DateConverterToPersian()

    var searchText by remember {
        mutableStateOf("")
    }

    date.GregorianToPersian(
        LocalDate.now().year,
        LocalDate.now().dayOfMonth,
        LocalDate.now().dayOfMonth
    )

    val exportDataLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(),
    ) {
        if (it == null) return@rememberLauncherForActivityResult
        val jsonString = Gson().toJson(viewModel.state.value.success)
        context.contentResolver.openOutputStream(it)
            ?.bufferedWriter()?.apply {
                write(jsonString)
                flush()
            }
    }

    val importDataLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val jsonString =
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: "[]"
        val listType: Type = object : TypeToken<List<PersonEntity>>() {}.type
        val items: List<PersonEntity> =
            Gson().fromJson(jsonString, listType)

        items.forEach {
            viewModel.insertAllPersons(personEntity = it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.AddScreen.route) },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add-Icon",
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            BottomNav(navController = navController)
        },
        topBar = {
            TopAppBarCustom(
                onItem1Click = {
                    exportDataLauncher.launch("backup${Date().time}.json")
                },
                onItem2Click = {
                    importDataLauncher.launch(arrayOf("*/*"))
                }
            )
        }
    ) { paddingValues ->
        paddingValues.toString()
        if (state.success.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "بدون عضو", style = MaterialTheme.typography.h6)
            }
        } else {
            val persons =
                state.success.filter { it.name?.contains(searchText, ignoreCase = true) == true }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 65.dp)
            ) {
                item {
                    SearchBox(searchText = searchText, onSearchTextChange = {
                        searchText = it
                    }) {}
                }
                items(persons) { items ->
                    if (items.remainDate?.toInt()?.equals(0) == true) {
                        PersonCard(
                            person = items.copy(
                                payStatus = ActivityPerson.NOTPAID
                            ), shape = RoundedCornerShape(20.dp)
                        ) {
                            viewModel.deletePerson(items)
                            Toast.makeText(context, "${items.name} حذف شد", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        PersonCard(person = items, shape = RoundedCornerShape(20.dp)) {
                            viewModel.deletePerson(items)
                            Toast.makeText(context, "${items.name} حذف شد", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonCard(
    title: String = "",
    canExpand: Boolean = false,
    isExpanded: Boolean = false,
    person: PersonEntity,
    shape: RoundedCornerShape,
    onNegativeClick: () -> Unit = {},
    onPositiveClick: () -> Unit = {},
    onDeleteIconClick: () -> Unit
) {
    var isExpandable by remember {
        mutableStateOf(isExpanded)
    }
    var paymentCheckbox by remember {
        mutableStateOf(false)
    }

    val payment = when (person.payStatus) {
        ActivityPerson.PAID -> !paymentCheckbox
        else -> paymentCheckbox
    }

    Column(modifier = Modifier.padding(bottom = 5.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(188.dp)
                .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 0.dp)
                .clickable(onClick = {
                    isExpandable = !isExpandable
                }, enabled = canExpand),
            shape = shape,
            elevation = 10.dp,
            backgroundColor = Color.White,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Row {
                                    Image(
                                        painter = rememberImagePainter(data = person.personImage?:R.drawable.person),
                                        contentDescription = "Person-Image",
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp)
                                            .clip(CircleShape)
                                            .size(65.dp)
                                            .border(1.dp, Color.Black, CircleShape),
                                        contentScale = ContentScale.Crop,
                                    )


                                    Spacer(modifier = Modifier.padding(end = 15.dp))
                                    Text(
                                        text = person.name ?: "بدون اسم",
                                        style = MaterialTheme.typography.body2,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = person.lastName ?: "",
                                        style = MaterialTheme.typography.body2,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row {
                                    Checkbox(
                                        checked = payment,
                                        onCheckedChange = { paymentCheckbox = !paymentCheckbox },
                                        enabled = false
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "از تاریخ: ${person.startDay ?: ""}",
                                    style = TextStyle(textDecoration = TextDecoration.None),
                                    fontSize = 17.sp,
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "تا تاریخ: ${person.endDay ?: ""}",
                                    style = TextStyle(textDecoration = TextDecoration.None),
                                    fontSize = 17.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = onDeleteIconClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red.copy(0.5f),
                                )
                            }
                        }
                    }
                }
            }
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AnimatedVisibility(visible = isExpandable) {
                if (isExpandable) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp)
                            .padding(start = 15.dp, end = 15.dp, bottom = 0.dp),
                        backgroundColor = Color.Black,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                    ) {
                        Column {
                            Text(
                                text = title,
                                color = Color.White,
                                modifier = Modifier.padding(start = 25.dp, top = 5.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = onPositiveClick, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Green
                                    )
                                ) {
                                    Text(text = "پرداخت شد")
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                                Button(
                                    onClick = onNegativeClick, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    )
                                ) {
                                    Text(text = "پرداخت نشد")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBox(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "جستجوی عضو")
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search",
                    tint = Color.Black,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                disabledIndicatorColor = Color.Black
            )
        )
    }
}


@Composable
fun TopAppBarCustom(
    onItem1Click: () -> Unit,
    onItem2Click: () -> Unit,
) {

    var expand by remember {
        mutableStateOf(false)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = "باشگاه پلاس",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp)
                )
            },
            backgroundColor = Color.White,
            elevation = 10.dp,
            actions = {
                IconButton(onClick = {
                    expand = !expand
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
                if (expand) {
                    DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                        DropdownMenuItem(onClick = { onItem1Click();expand = false }) {
                            Row {
                                Text(text = "بک اپ")
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "backup"
                                )
                            }
                        }
                        DropdownMenuItem(onClick = { onItem2Click();expand = false }) {
                            Row {
                                Text(text = "برگرداندن اطلاعات")
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "backup"
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}














