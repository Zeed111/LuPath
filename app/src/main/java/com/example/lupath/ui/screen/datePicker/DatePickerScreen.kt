package com.example.lupath.ui.screen.datePicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lupath.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenLight
import com.google.accompanist.pager.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePickerScreen(navController: NavController) {
//    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
//    val datePickerState = rememberDatePickerState()
//    val openDialog = remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        IconButton(onClick = { navController.popBackStack() }) {
//                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                        }
//                        Image(
//                            painter = painterResource(id = R.drawable.lupath), // your logo
//                            contentDescription = "Logo",
//                            modifier = Modifier.size(40.dp)
//                        )
//                        IconButton(onClick = { /* Settings click */ }) {
//                            Icon(Icons.Default.Settings, contentDescription = "Settings")
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
//        },
//        floatingActionButton = {
//            Card(
//                shape = RoundedCornerShape(20.dp),
//                elevation = CardDefaults.cardElevation(8.dp),
//                modifier = Modifier
//                    .padding(16.dp)
//                    .height(48.dp)
//                    .clickable {
//                        openDialog.value = true
//                    }
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                ) {
//                    Icon(Icons.Default.Edit, contentDescription = "Add", tint = Color.Black)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Add Plan", color = Color.Black)
//                }
//            }
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            Text("Calendar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(350.dp)
//                    .align(Alignment.CenterHorizontally),
//                elevation = CardDefaults.cardElevation(6.dp),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color(0xFFD9D9D9)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = selectedDate?.toString() ?: "Select a date using Add Plan",
//                        color = Color.Black
//                    )
//                }
//            }
//        }
//
//        // Date Picker Dialog
//        if (openDialog.value) {
//            DatePickerDialog(
//                onDismissRequest = { openDialog.value = false },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            val millis = datePickerState.selectedDateMillis
//                            if (millis != null) {
//                                selectedDate = Instant.ofEpochMilli(millis)
//                                    .atZone(ZoneId.systemDefault())
//                                    .toLocalDate()
//                            }
//                            openDialog.value = false
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { openDialog.value = false }) {
//                        Text("Cancel")
//                    }
//                }
//            ) {
//                DatePicker(state = datePickerState)
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* My Lupath List */},
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clickable {
                            // You can later pass selectedDate to the List screen
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Add", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Plan", color = Color.Black)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔙 Back, Logo, Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
//                Text("LuPath", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Image(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            // Calendar Label
            Text(
                text = "Calendar",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            // Calendar Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
//                    .wrapContentSize(),
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GreenLight
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GreenLight)
                        .wrapContentHeight()
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier

                    )
                }
//                DatePicker(
//                    state = datePickerState,
//                    modifier = Modifier.padding(16.dp)
//                )

                selectedDate = datePickerState.selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
            }
        }
    }
}