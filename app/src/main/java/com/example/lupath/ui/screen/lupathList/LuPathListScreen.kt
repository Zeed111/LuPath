package com.example.lupath.ui.screen.lupathList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.HikePlan
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.Lato
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuPathListScreen(
    navController: NavHostController,
    viewModel: HikePlanViewModel = viewModel()
) {
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

//             Calendar to display planned hike dates
            CustomCalendarM3Style(
                hikePlans = viewModel.hikePlans,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal=16.dp, vertical=8.dp),

                onDateClick = { clickedDate ->
                    println("Clicked on date: $clickedDate")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
            ) {
                items(
                    items = viewModel.hikePlans,
                    key = { plan -> plan.hashCode() }
                ) { plan ->
                    PlanCard(
                        mountainName = plan.mountainName,
                        difficulty = plan.difficulty,
                        date = plan.date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                        onEdit = {
                            // TODO: Implement Edit Action
                            println("Edit: ${plan.mountainName}")
                        },
                        onDelete = {
                            // *** IMPLEMENT Delete Action ***
                            viewModel.removeHikePlan(plan)
                        }
                    )
                }
            }
        }
    }
}

//@Composable
//fun CustomCalendar(hikePlans: List<HikePlan>, modifier: Modifier = Modifier) {
//    val currentMonth = YearMonth.now()
//    val daysInMonth = currentMonth.lengthOfMonth()
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(7), // 7 days a week
//        modifier = modifier.fillMaxWidth()
//    ) {
//        items(daysInMonth) { day ->
//            val date = currentMonth.atDay(day + 1) // Convert day to LocalDate
//            val isPlanned = hikePlans.any { it.date == date }
//
//            Box(
//                modifier = Modifier
//                    .padding(4.dp)
//                    .background(
//                        if (isPlanned) Color.Green else Color.Transparent,
//                        shape = RoundedCornerShape(8.dp)
//                    )
//                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
//                    .size(40.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = (day + 1).toString(),
//                    color = if (isPlanned) Color.White else Color.Black,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}

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

@Composable
fun CustomCalendarM3Style(
    hikePlans: List<HikePlan>,
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate) -> Unit = {} // Optional: Callback for clicking a date
) {
    // State for the currently displayed month
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Generate the days to display, including padding for the start of the month
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstOfMonth = currentMonth.atDay(1)
    // Adjust start day based on Sunday as the first day (value 7). Monday is 1.
    val startDayOfWeek = firstOfMonth.dayOfWeek.value % 7 // Sunday = 0, Monday = 1, ... Saturday = 6
    val paddingDays = startDayOfWeek

    val monthName = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val daysOfWeek = remember { getWeekDayAbbreviationList() } // Get S, M, T...

    // Create a set of planned dates for efficient lookup
    val plannedDates = remember(hikePlans) {
        hikePlans.map { it.date }.toSet()
    }

    Card( // Wrap in a Card for elevation and shape similar to DatePicker dialog
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium // Or small/large as preferred
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Month Name and Navigation
            CalendarHeader(
                monthName = monthName,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Days of the Week Labels
            DaysOfWeekHeader(daysOfWeek)

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                // Calculate appropriate height or let it wrap content
                // modifier = Modifier.height(300.dp) // Example fixed height
            ) {
                // Add empty boxes for padding days before the 1st
                items(paddingDays) {
                    Box(modifier = Modifier.size(40.dp)) // Placeholder box
                }

                // Add actual day cells
                items(daysInMonth) { dayOfMonth ->
                    val day = dayOfMonth + 1
                    val date = currentMonth.atDay(day)
                    val isPlanned = plannedDates.contains(date)
                    val isToday = date == LocalDate.now()

                    DayCell(
                        day = day,
                        date = date,
                        isPlanned = isPlanned,
                        isToday = isToday,
                        onClick = { onDateClick(date) }
                    )
                }
            }
        }
    }
}

// Helper to get abbreviated day names (S, M, T...)
private fun getWeekDayAbbreviationList(): List<String> {
    // Ensure Sunday is first if that's the desired calendar layout
    // val days = DayOfWeek.values() // Default order might be Monday-first
    // Adjust order if needed. Example for Sun-Sat:
    val days = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    return days.map { it.getDisplayName(TextStyle.NARROW, Locale.getDefault()) }
}

// Composable for the Calendar Header (Month + Arrows)
@Composable
private fun CalendarHeader(
    monthName: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(
            text = monthName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

// Composable for the Days of the Week Header (S, M, T...)
@Composable
private fun DaysOfWeekHeader(daysOfWeek: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround // Distribute evenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall, // Use a smaller label style
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Subtle color
                modifier = Modifier.width(40.dp) // Match DayCell width approx
            )
        }
    }
}

// Composable for a single Day cell in the grid
@Composable
private fun DayCell(
    day: Int,
    date: LocalDate,
    isPlanned: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isPlanned -> MaterialTheme.colorScheme.primary // Highlight planned dates
        else -> Color.Transparent
    }
    val contentColor = when {
        isPlanned -> MaterialTheme.colorScheme.onPrimary // Text color on highlight
        isToday -> MaterialTheme.colorScheme.primary // Highlight today's number
        else -> MaterialTheme.colorScheme.onSurface // Default text color
    }
    val borderModifier = if (isToday && !isPlanned) { // Add border only for today if not planned
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(40.dp) // Fixed size for each cell
            .clip(CircleShape) // Clip content to circle
            .background(backgroundColor)
            .then(borderModifier) // Apply border if needed
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = contentColor,
            fontWeight = if (isToday || isPlanned) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
