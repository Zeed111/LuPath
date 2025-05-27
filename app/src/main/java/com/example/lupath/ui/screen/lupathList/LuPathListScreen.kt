package com.example.lupath.ui.screen.lupathList

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.HikePlan
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.helper.ConfirmationDialog
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.screen.mountainDetails.getDrawableResIdFromString
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
    viewModel: HikePlanViewModel = hiltViewModel()
) {
    val hikePlansList by viewModel.hikePlans.collectAsStateWithLifecycle()
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var planToDelete by remember { mutableStateOf<HikePlan?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { LuPathTopBar(navController = navController) },
        containerColor = Color.White,
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
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
            if (hikePlansList.isEmpty()) {
                item {
                    Text(
                        text = "No hikes planned yet.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(
                    items = hikePlansList,
                    key = { plan -> plan.id } // Use a stable unique ID for the key
                ) { plan ->
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope() // For launching coroutines from Composable
                    PlanCard(
                        hikePlan = plan,
                        navController = navController,// Pass the whole HikePlan UI model
                        onEdit = {
                            val initialDateEpochDay = plan.date.toEpochDay()
                            val encodedNotes = try {
                                URLEncoder.encode(plan.notes ?: "", StandardCharsets.UTF_8.name())
                            } catch (e: Exception) {
                                ""
                            }

                            navController.navigate(
                                "datepicker/${plan.mountainId}?hikePlanId=${plan.id}&initialSelectedDateEpochDay=${initialDateEpochDay}&notes=${encodedNotes}"
                            )},
                        onDeleteRequest = {
                            planToDelete = plan // Set the plan you intend to delete
                            showDeleteConfirmationDialog = true // Show the dialog
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showDeleteConfirmationDialog && planToDelete != null) {
            ConfirmationDialog(
                dialogTitle = "Confirm Deletion",
                dialogText = "Are you sure you want to delete the plan for ${planToDelete!!.mountainName} on ${planToDelete!!.date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}?",
                onConfirmation = {
                    scope.launch { // Launch coroutine for DB operation
                        viewModel.removeHikePlan(planToDelete!!)
                        Toast.makeText(context, "Hike plan deleted", Toast.LENGTH_SHORT).show()
                        planToDelete = null // Reset
                        showDeleteConfirmationDialog = false
                    }
                },
                onDismissRequest = {
                    planToDelete = null // Reset
                    showDeleteConfirmationDialog = false
                }
            )
        }
    }
}

@Composable
fun LuPathTopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Image(
            painter = painterResource(id = R.drawable.lupath),
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
    hikePlan: HikePlan,
    navController: NavHostController,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("mountainDetail/${hikePlan.mountainId}")
        },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenLight)
    ) {
        Row(
            modifier = Modifier
            .fillMaxWidth()
            .background(GreenLight)
        ) {
            Image(
                painter = if (hikePlan.imageResourceName != null &&
                    getDrawableResIdFromString(context, hikePlan.imageResourceName) != null)
                    painterResource(id = getDrawableResIdFromString(context, hikePlan.imageResourceName)!!)
                else
                    painterResource(id = R.drawable.mt_pulag_ex), // Fallback placeholder
                contentDescription = "Image of ${hikePlan.mountainName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column {
                    Text(
                        hikePlan.mountainName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        hikePlan.date.format(DateTimeFormatter.ofPattern("MMMM dd, y")),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // --- Display Notes ---
                if (!hikePlan.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        // text = "Notes: ${hikePlan.notes}",
                        text = hikePlan.notes,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.align(Alignment.Top).padding(end = 4.dp)) {
                IconButton(onClick = { expanded = true }, modifier = Modifier.size(40.dp)) {
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
                            onDeleteRequest()
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

            DaysOfWeekHeader(daysOfWeek)
            Spacer(modifier = Modifier.height(8.dp))

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
                    if (it < numRows - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

private fun getWeekDayAbbreviationList(): List<String> {
    val days = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
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