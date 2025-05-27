package com.example.lupath.ui.screen.datePicker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.HikePlanViewModel
import com.example.lupath.helper.ConfirmationDialog
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(
    navController: NavHostController,
    viewModel: HikePlanViewModel = hiltViewModel(),
    mountainId: String,
    hikePlanIdForEdit: String?,
    initialSelectedDateEpochDay: Long,
    initialNotes: String?
) {
    val screenScrollState = rememberScrollState()
    val context = LocalContext.current
    val isEditMode = hikePlanIdForEdit != null

    val initialSelectedMillis = if (isEditMode && initialSelectedDateEpochDay != -1L) {
        LocalDate.ofEpochDay(initialSelectedDateEpochDay).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    } else {
        null
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedMillis,
        yearRange = (LocalDate.now().year)..(LocalDate.now().year + 100),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedLocalDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !selectedLocalDate.isBefore(LocalDate.now()) // Allow today and future dates
            }
            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        },
    )

    var notesText by rememberSaveable { mutableStateOf(initialNotes.orEmpty()) }
    var showEditConfirmationDialog by remember { mutableStateOf(false) }
// To temporarily hold the data before confirming the edit
    var tempSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var tempNotesText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis == null) {
                        Toast.makeText(context, "Please select a date",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        val date = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val isOriginalDateInEditMode = isEditMode && initialSelectedDateEpochDay != -1L && date.toEpochDay() == initialSelectedDateEpochDay
                        val currentNotes = notesText.trim()
                        if (date.isBefore(LocalDate.now()) && !isOriginalDateInEditMode) {
                            Toast.makeText(context, "Please select today or a future date.", Toast.LENGTH_LONG).show()
                        } else {
                            // Date is valid (today or future), proceed with action
                            if (isEditMode) {
                                tempSelectedDate = date
                                tempNotesText = currentNotes
                                showEditConfirmationDialog = true
                            } else {
                                viewModel.addHikePlanFromPicker(
                                    mountainIdFromPicker = mountainId,
                                    selectedDate = date,
                                    notes = currentNotes
                                )
                                Toast.makeText(context, "Hike plan added!", Toast.LENGTH_SHORT).show()
                                navController.navigate("lupath_list") {
                                    popUpTo("lupath_list") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
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
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = if (isEditMode) "Save Plan" else "Add Plan",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Save Plan" else "Add Plan", color = Color.Black, fontFamily = Lato)
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

            Text(
                text = if (isEditMode) "Edit Hike Plan" else "New Hike Plan",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = Lato
            )

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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Notes (Optional)",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Lato
            )
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 100.dp, max = 200.dp),
                label = { Text("e.g., Hike companions, specific gear, reminders") },
                placeholder = { Text("Add any details for this hike...")},
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDark,
                    unfocusedBorderColor = Color.Gray,
                ),
                singleLine = false
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showEditConfirmationDialog && isEditMode && tempSelectedDate != null) {
        val nonNullTempSelectedDate = tempSelectedDate!!
        ConfirmationDialog(
            dialogTitle = "Confirm Changes",
            dialogText = "Are you sure you want to update this hike plan?",
            onConfirmation = {
                viewModel.updateHikePlanDate(
                    hikePlanId = hikePlanIdForEdit,
                    mountainId = mountainId,
                    newDate = nonNullTempSelectedDate,
                    newNotes = tempNotesText
                )
                Toast.makeText(context, "Hike plan updated!", Toast.LENGTH_SHORT).show()
                showEditConfirmationDialog = false
                navController.navigate("lupath_list") {
                    popUpTo("lupath_list") { inclusive = true }
                    launchSingleTop = true
                }
            },
            onDismissRequest = { showEditConfirmationDialog = false }
        )
    }
}