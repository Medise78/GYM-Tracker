package com.medise.bashga.presentation.add_screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.persian_date_picker.PersianDatePicker
import com.medise.bashga.R
import com.medise.bashga.domain.model.PersonEntity
import com.medise.bashga.util.ActivityPerson
import com.medise.bashga.util.DateConverterToPersian
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    viewModel: AddScreenViewModel = hiltViewModel(),
    navController: NavController
) {
    val fName = viewModel.fName.observeAsState("")
    val lName = viewModel.lName.observeAsState("")

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val date = DateConverterToPersian()
    val date2 = DateConverterToPersian()

    date.GregorianToPersian(
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY_OF_MONTH
    )

    date.GregorianToPersian(
        LocalDate.now().year,
        LocalDate.now().monthValue,
        LocalDate.now().dayOfMonth
    )

    var hideDatePiker by remember {
        mutableStateOf(true)
    }

    val openDialog = remember {
        mutableStateOf(false)
    }

    var payStatus by remember {
        mutableStateOf(false)
    }

    var day by remember {
        mutableStateOf("")
    }
    var month by remember {
        mutableStateOf("")
    }
    var year by remember {
        mutableStateOf("")
    }

    var stateColor by remember {
        mutableStateOf(Color.Red)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    var checkState by remember {
        mutableStateOf(false)
    }

    val imageCrop = rememberLauncherForActivityResult(
        contract =
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) {
        val cropOptions = CropImageContractOptions(it, CropImageOptions())
        imageCrop.launch(cropOptions)
    }
    val pay = if (payStatus) ActivityPerson.PAID else ActivityPerson.NOTPAID


    LaunchedEffect(key1 = true) {
        viewModel.sharedFlow.collectLatest { event ->
            when (event) {
                is AddScreenViewModel.UiEvent.SavedNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) { paddingValues ->

        paddingValues.calculateBottomPadding()
        paddingValues.calculateTopPadding()
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "مشخصات کاربر", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = modifier) {

                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)

                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }
                }
                if (bitmap.value != null) {
                    bitmap.value?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = "person-Image",
                            modifier = Modifier
                                .width(90.dp)
                                .height(90.dp)
                                .size(95.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.person),
                        contentDescription = "person-Image",
                        modifier = Modifier
                            .width(90.dp)
                            .height(90.dp)
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape)
                            .fillMaxSize(0.5f),
                        contentScale = ContentScale.Inside
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .border(1.dp, Color.Black, CircleShape)
                        .clip(
                            CircleShape
                        )
                        .background(Color.White)
                        .size(23.dp)
                        .align(Alignment.BottomEnd)
                        .padding()
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                )
            }

            Spacer(modifier = Modifier.height(70.dp))
            CustomTextField(
                text = fName.value ?: "",
                onTextChange = {
                    viewModel.fName.value = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                labelText = "نام",
            )
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(
                text = lName.value,
                onTextChange = {
                    viewModel.lName.value = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                labelText = "نام خانوادگی"
            )
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(
                text = if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) "${day + "/" + month + "/" + year}" else "",
                onTextChange = {},
                keyboardOptions = KeyboardOptions(),
                labelText = "",
                imageVector = Icons.Default.DateRange
            ) {
                hideDatePiker = false
            }

            if (!hideDatePiker) {
                PersianDatePicker(onDismiss = {
                    hideDatePiker = true
                }, setDate = {
                    day = it["day"]!!
                    month = it["month"]!!
                    year = it["year"]!!
                }, minYear = 1400)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {
                        checkState = !checkState
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Checkbox(checked = checkState, onCheckedChange = { checkState = !checkState })
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 5.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(text = "عضو بسیج", textAlign = TextAlign.End)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Button(
                    onClick = { openDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = stateColor
                    )
                ) {
                    Text(text = if (!payStatus) "منتظر پرداخت" else "پرداخت شد")
                }
            }



            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (fName.value.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("مقادیر را پر کنید")
                        }
                    } else {
                        val start = LocalDate.parse(
                            "$day/$month/$year", DateTimeFormatter.ofPattern("d/M/yyyy")
                        )
                        val end = LocalDate.parse(
                            "$day/$month/$year", DateTimeFormatter.ofPattern("d/M/yyyy")
                        ).plusMonths(1)

                        date2.GregorianToPersian(year.toInt(), month.toInt(), day.toInt())
                        viewModel.addPerson(
                            PersonEntity(
                                name = fName.value,
                                lastName = lName.value,
                                startDay = start.toString(),
                                endDay = end.toString(),
                                payStatus = pay,
                                isPayed = payStatus,
                                personImage = imageUri.toString(),
                                remainDate = ChronoUnit.DAYS.between(
                                    LocalDate.of(
                                        date.year,
                                        date.month,
                                        date.day
                                    ), end
                                ).toString(),
                                isHalfPrice = checkState
                            )
                        )
                    }

                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "اضافه کردن +")
            }

            if (openDialog.value) {
                Dialog(onDismissRequest = { openDialog.value = false }) {
                    DialogBox(
                        title = "وضعیت پرداخت",
                        onConfirm = {
                            payStatus = true
                            openDialog.value = false
                            stateColor = Color.Green
                        },
                        onDismiss = {
                            payStatus = false
                            openDialog.value = false
                            stateColor = Color.Red
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    text: String,
    onTextChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    labelText: String,
    imageVector: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                keyboardOptions = keyboardOptions,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                ),
                label = {
                    Text(text = labelText, color = Color.Black.copy(0.5f))
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    errorIndicatorColor = Color.Red
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Person")
                },
                trailingIcon = {
                    imageVector?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "date",
                            modifier = Modifier.clickable(onClick = onClick)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DialogBox(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Button(
                    onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    ),
                    modifier = Modifier
                        .width(110.dp)
                        .height(40.dp)
                ) {
                    Text(text = "لغو")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onConfirm, colors = ButtonDefaults.buttonColors(
                        Color.Green
                    ),
                    modifier = Modifier
                        .width(110.dp)
                        .height(40.dp)
                ) {
                    Text(text = "پرداخت شد")
                }
            }
        }
    }
}


