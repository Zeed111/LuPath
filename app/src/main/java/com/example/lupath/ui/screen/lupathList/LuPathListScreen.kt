package com.example.lupath.ui.screen.lupathList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.lupath.data.model.HikePlan
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuPathListScreen(
    navController: NavHostController,
    mountainName : String,
    selectedDate : String,
    viewModel: HikePlanViewModel = viewModel()
) {
    val decodedDate = URLDecoder.decode(selectedDate, StandardCharsets.UTF_8.toString())

    Text(text = "Date: $decodedDate")
    Text(text = "Mountain: $mountainName")
    Text(text = "Date: $selectedDate")

    Scaffold(
        topBar = { LuPathTopBar(navController) },
        containerColor = Color.White,
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My LuPath",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Lato,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = "Calendar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Lato,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 20.dp, start = 30.dp)
            )

            // Calendar to display planned hike dates
//            CustomCalendar(hikePlans = hikePlans)

//            CustomCalendar(hikePlans = viewModel.hikePlans)
//
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
////                    items(viewModel.hikePlans) { plan ->
////                        PlanCard(
////                            mountainName = plan.mountainName,
////                            difficulty = plan.difficulty,
////                            date = plan.date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
////                            onEdit = { /* Handle Edit */ },
////                            onDelete = { }
////                        )
////                    }
//                    items(viewModel.hikePlans) { plan ->
//                        Text(text = "Mountain: ${plan.mountainName}, Difficulty: ${plan.difficulty}, Date: ${plan.date}")
//                    }
//                }
        }
    }
}

@Composable
fun CustomCalendar(hikePlans: List<HikePlan>) {
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    LazyVerticalGrid(
        columns = GridCells.Fixed(7), // 7 days a week
        modifier = Modifier.fillMaxWidth()
    ) {
        items(daysInMonth) { day ->
            val date = currentMonth.atDay(day + 1) // Convert day to LocalDate
            val isPlanned = hikePlans.any { it.date == date }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        if (isPlanned) Color.Green else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (day + 1).toString(),
                    color = if (isPlanned) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LuPathTopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true } // Clears the back stack
            }
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Image(
            painter = painterResource(id = R.drawable.lupath), // logo
            contentDescription = "Logo",
            modifier = Modifier.size(40.dp)
        )

        IconButton(onClick = { /* Navigate to settings */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun PlanCard(
    mountainName: String,
    difficulty: String,
    date: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(mountainName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(date, fontSize = 12.sp, color = Color.Gray)
                Text("Difficulty: $difficulty", fontSize = 12.sp)
            }

            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.align(Alignment.Top)) {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}