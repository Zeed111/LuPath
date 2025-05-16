package com.example.lupath.ui.screen.datePicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.HikePlan
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lupath.ui.theme.GreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(
    navController: NavHostController,
    viewModel: HikePlanViewModel = hiltViewModel(),
    mountainId: String
) {
    val datePickerState = rememberDatePickerState()
    val screenScrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis == null) {
                        android.widget.Toast.makeText(context, "Please select a date",
                            android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val date = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        viewModel.addHikePlanFromPicker(
                            mountainIdFromPicker = mountainId, // This 'mountainId' is a parameter of DatePickerScreen
                            selectedDate = date
                        )

                        val selectedDateText = try {
                            date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")).let {
                                URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                            }
                        } catch (e: Exception) {
                            "Invalid Date"
                        }

                        navController.navigate("lupath_list") {
                            launchSingleTop = true
                            // Consider popUpTo to clear backstack if needed
                        }
                    }
                },
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize(),
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Add", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Plan", color = Color.Black, fontFamily = Lato)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(screenScrollState)
                .padding(bottom = 80.dp)
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
//                Text("LuPath", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Image(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                IconButton(onClick = {
                    navController.navigate("settings")
                }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            // Calendar Label
            Text(
                text = "Calendar",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = Lato
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
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth(),

                )
            }
        }
    }
}