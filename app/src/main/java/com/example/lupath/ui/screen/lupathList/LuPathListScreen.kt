package com.example.lupath.ui.screen.lupathList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.HikePlan
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuPathListScreen(
    navController: NavHostController,
    viewModel: HikePlanViewModel = viewModel()
) {
    val screenScrollState = rememberScrollState()
    val hikePlansList by viewModel.hikePlans.collectAsStateWithLifecycle()


    Scaffold(
        topBar = { LuPathTopBar(navController = navController) },
        containerColor = Color.White,
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding) // Apply padding from Scaffold
                .fillMaxSize(),
            // Center titles if desired for the whole list content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header Items ---
            item { // Title 1
                Text(
                    text = "My LuPath",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Lato,
                    modifier = Modifier.padding(10.dp)
                )
            }

            item { // Title 2
                Text(
                    text = "Calendar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Lato,
                    modifier = Modifier
                        // Align within the LazyColumn width
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 30.dp, end = 30.dp)
                )
            }

            item { // Calendar Item
                CustomCalendarM3Style(
                    hikePlans = hikePlansList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onDateClick = { clickedDate ->
                        println("Clicked on date: $clickedDate")
                    }
                )
            }

            item { // Spacer Item
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { // Title 3
                Text(
                    text = "Planned Hikes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Lato,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, bottom = 8.dp)
                )
            }

            // --- Plan Card Items ---
            items(
                items = hikePlansList,
                key = { plan -> plan.hashCode() } // Use plan.id if available
            ) { plan ->
                PlanCard(
                    mountainName = plan.mountainName,
                    difficulty = plan.difficulty,
                    date = plan.date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) ?: "No Date",
                    onEdit = { /* ... */ },
                    onDelete = { viewModel.removeHikePlan(plan) }
                )
                // Add spacing within the item lambda or use contentPadding on LazyColumn
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { // Spacer at the very end
                Spacer(modifier = Modifier.height(16.dp))
            }

        } // End of LazyColumn
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Image(
            painter = painterResource(id = R.drawable.lupath), // logo
            contentDescription = "Logo",
            modifier = Modifier.size(40.dp)
        )

        IconButton(onClick = {
            navController.navigate("settings")
        }) {
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
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors( // Add the 'colors' parameter
            containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9))
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(mountainName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(date, fontSize = 12.sp, color = Color.Black)
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

//@Composable
//fun CustomCalendarM3Style(
//    hikePlans: List<HikePlan>,
//    modifier: Modifier = Modifier,
//    onDateClick: (LocalDate) -> Unit = {}
//) {
//    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
//    val density = LocalDensity.current
//
//    val onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) }
//    val onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
//
//    val daysInMonth = currentMonth.lengthOfMonth()
//    val firstOfMonth = currentMonth.atDay(1)
//    val startDayOfWeek = firstOfMonth.dayOfWeek.value % 7
//    val paddingDays = startDayOfWeek
//    val monthName = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
//    val daysOfWeek = remember { getWeekDayAbbreviationList() } // Get S, M, T...
//    val plannedDates = remember(hikePlans) {
//        hikePlans.map { it.date }.toSet()
//    }
//// Swipe gesture tracking
//    var swipeOffsetX by remember { mutableFloatStateOf(0f) }
//    var gestureConsumed by remember { mutableStateOf(false) } // To prevent multiple month changes per swipe
//
//    Card(
//        modifier = modifier,
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.medium,
//        colors = CardDefaults.cardColors( // Add the 'colors' parameter
//            containerColor = Color(0xFFD9D9D9))
//
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            CalendarHeader(
//                monthName = monthName,
//                onPreviousMonth = onPreviousMonth,
//                onNextMonth = onNextMonth
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Column(
//                modifier = Modifier.pointerInput(currentMonth) { // Pass currentMonth as key to reset detector on month change
//                    detectHorizontalDragGestures(
//                        onDragStart = {
//                            swipeOffsetX = 0f // Reset offset at the start of a drag
//                            gestureConsumed = false // Reset consumed flag
//                        },
//                        onHorizontalDrag = { change, dragAmount ->
//                            // Only consume if not already consumed in this gesture
//                            if (!gestureConsumed) {
//                                swipeOffsetX += dragAmount
//                                // Optional: Consume the pointer event if handling drag
//                                // change.consume()
//                            }
//                        },
//                        onDragEnd = {
//                            if (!gestureConsumed) {
//                                val swipeThresholdPx = with(density) { 60.dp.toPx() } // Threshold in pixels
//
//                                if (swipeOffsetX > swipeThresholdPx) {
//                                    // Swiped Right (finger moved right) -> Previous Month
//                                    onPreviousMonth()
//                                    gestureConsumed = true
//                                } else if (swipeOffsetX < -swipeThresholdPx) {
//                                    // Swiped Left (finger moved left) -> Next Month
//                                    onNextMonth()
//                                    gestureConsumed = true
//                                }
//                                // Reset offset after drag ends, regardless of threshold met
//                                swipeOffsetX = 0f
//                            }
//                        }
//                    )
//                }
//            ) {
//                DaysOfWeekHeader(daysOfWeek)
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(7),
//                    // Let the grid determine its height based on content
//                    // Apply minimum height if needed, but avoid fixed large height
//                    modifier = Modifier.heightIn(min = 280.dp), // Ensure minimum touch area
//                    userScrollEnabled = false // Disable grid's own scrolling to prevent conflicts
//                ) {
//                    // --- Padding Items ---
//                    items(paddingDays) {
//                        // Render empty boxes for padding days at the start of the month
//                        Box(modifier = Modifier.size(40.dp)) // Same size as DayCell
//                    }
//
//                    // --- Day Items ---
//                    items(daysInMonth) { dayOfMonth ->
//                        val day = dayOfMonth + 1
//                        val date = currentMonth.atDay(day)
//                        val isPlanned = plannedDates.contains(date)
//                        val isToday = date == LocalDate.now()
//
//                        DayCell(
//                            day = day,
//                            date = date,
//                            isPlanned = isPlanned,
//                            isToday = isToday,
//                            onClick = { onDateClick(date) }
//                        )
//                    }
//                }
//            } // End of swipeable Column
//        } // End of Card's Column
//    } // End of Card
//}

@Composable
fun CustomCalendarM3Style(
    hikePlans: List<HikePlan>,
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate) -> Unit = {}
) {
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    // Keep density if needed for swipe threshold, but swipe might be less intuitive now
    // val density = LocalDensity.current

    val onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) }
    val onNextMonth = { currentMonth = currentMonth.plusMonths(1) }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstOfMonth = currentMonth.atDay(1)
    val startDayOfWeek = firstOfMonth.dayOfWeek.value % 7
    val paddingDays = startDayOfWeek
    val monthName = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val daysOfWeek = remember { getWeekDayAbbreviationList() }
    val plannedDates = remember(hikePlans, currentMonth) { // Recalculate if plans or month change
        hikePlans.map { it.date }.toSet()
    }

    // Remove swipe state if removing swipe modifier
    // var swipeOffsetX by remember { mutableFloatStateOf(0f) }
    // var gestureConsumed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarHeader(
                monthName = monthName,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Removed the Column with pointerInput modifier

            DaysOfWeekHeader(daysOfWeek)
            Spacer(modifier = Modifier.height(8.dp))

            // --- Replace LazyVerticalGrid with basic Columns/Rows ---
            // This will compose all day cells at once. Acceptable if calendar isn't huge.
            Column { // Column to hold the rows of weeks
                val totalCells = paddingDays + daysInMonth
                val numRows = (totalCells + 6) / 7 // Calculate number of rows needed

                var dayOfMonth = 1
                repeat(numRows) { // Create each week row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround // Distribute cells
                    ) {
                        repeat(7) { dayIndex -> // Create each day cell/placeholder
                            val cellIndex = it * 7 + dayIndex
                            if (cellIndex < paddingDays || dayOfMonth > daysInMonth) {
                                // Empty box for padding or after last day
                                Box(modifier = Modifier.size(40.dp))
                            } else {
                                // Actual DayCell
                                val date = currentMonth.atDay(dayOfMonth)
                                val isPlanned = plannedDates.contains(date)
                                val isToday = date == LocalDate.now()
                                DayCell(
                                    day = dayOfMonth,
                                    date = date,
                                    isPlanned = isPlanned,
                                    isToday = isToday,
                                    onClick = { onDateClick(date) }
                                )
                                dayOfMonth++ // Increment day
                            }
                        }
                    }
                    if (it < numRows - 1) { // Add spacing between weeks if desired
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            // --- End of basic Columns/Rows calendar grid ---

        } // End of Card's Column
    } // End of Card
}

private fun getWeekDayAbbreviationList(): List<String> {
    val days = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    return days.map { it.getDisplayName(TextStyle.NARROW, Locale.getDefault()) }
}

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


@Composable
private fun DaysOfWeekHeader(daysOfWeek: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    date: LocalDate,
    isPlanned: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isPlanned -> GreenLight
        else -> Color.Transparent
    }
    val contentColor = when {
        isPlanned -> Color.Black
        isToday -> Color.Black
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderModifier = if (isToday && !isPlanned) {
        Modifier.border(1.dp, GreenDark, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(borderModifier)
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
