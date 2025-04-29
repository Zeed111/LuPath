package com.example.lupath.ui.screen.toBringList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.ChecklistItem
import com.example.lupath.data.model.ChecklistViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CheckListScreen(
//    navController: NavHostController,
//    viewModel: ChecklistViewModel = viewModel()
//) {
//    var showAddDialog by rememberSaveable { mutableStateOf(false) }
//    val predefinedItemsList by viewModel.predefinedItems.collectAsStateWithLifecycle() // <<< Collect State
//    val personalItemsList by viewModel.personalItems.collectAsStateWithLifecycle()   // <<< Collect State
//
//    Scaffold(
//        topBar = { LuPathTopBar(navController = navController) },
//        containerColor = Color.White,
//        bottomBar = { HomeBottomNav(navController) }
//    ) { paddingValues ->
//
//        LazyColumn(
//            modifier = Modifier
//                .padding(paddingValues) // Apply padding from Scaffold
//                .fillMaxSize()
//                .padding(horizontal = 16.dp), // Add horizontal padding once here
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // --- Predefined Checklist Section ---
//            item { // Header as an item
//                Text(
//                    text = "Check List",
//                    style = MaterialTheme.typography.headlineSmall,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .fillMaxWidth() // Allow aligning start
//                        .padding(top = 16.dp, bottom = 8.dp) // Add vertical padding
//                    // .align(Alignment.Start) // This won't work directly on item's modifier
//                    // Alignment is controlled by LazyColumn's horizontalAlignment or Text's textAlign
//                )
//            }
//
//            items(
//                items = predefinedItemsList,
//                key = { item: ChecklistItem -> item.id } // Explicit type for key lambda
//            ) { item: ChecklistItem -> // <<< Explicitly type 'item' here
//                ChecklistItemRow(
//                    item = item,
//                    onCheckedChange = { // isChecked parameter is implicitly Boolean
//                        viewModel.togglePredefinedItemChecked(item)
//                    }
//                )
//            }
//
//            item { // Spacer as an item
//                Spacer(modifier = Modifier.height(24.dp))
//            }
//
//            item { // Header as an item
//                Text(
//                    text = "My personal Check List",
//                    style = MaterialTheme.typography.headlineSmall,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .fillMaxWidth() // Allow aligning start
//                        .padding(bottom = 8.dp)
//                    // .align(Alignment.Start) // Controlled by LazyColumn alignment
//                )
//            }
//
//            // --- Personal Checklist Section ---
//            if (personalItemsList.isEmpty()) { // Use collected list
//                item { // Placeholder text as an item
//                    Text(
//                        text = "No personal items added yet.",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.padding(vertical = 16.dp) // More padding maybe
//                    )
//                }
//            }
//
//            items(
//                items = personalItemsList,
//                key = { item: ChecklistItem -> item.id } // Explicit type for key lambda
//            ) { item: ChecklistItem -> // <<< Explicitly type 'item' here
//                ChecklistItemRow(
//                    item = item,
//                    onCheckedChange = { // isChecked parameter is implicitly Boolean
//                        viewModel.togglePersonalItemChecked(item)
//                    },
//                    onDelete = { viewModel.removePersonalItem(item) }
//                )
//            }
//
//            // --- Add Entry Button as the last item ---
//            item {
//                AddEntryButton(
//                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp), // Adjust padding
//                    onClick = { showAddDialog = true }
//                )
//            }
//        } // End of LazyColumn
//    } // End of Scaffold
//
//    // --- Dialog ---
//    if (showAddDialog) {
//        AddChecklistItemDialog(
//            onDismiss = { showAddDialog = false },
//            onConfirm = { text ->
//                viewModel.addPersonalItem(text)
//                // Keep dialog open after adding? Or dismiss?
//                // If dismiss:
//                showAddDialog = false
//                // If stay open:
//                // showAddDialog = true // (it's already true, maybe clear text instead?)
//            }
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckListScreen(
    navController: NavHostController,
    viewModel: ChecklistViewModel = viewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val predefinedItemsList by viewModel.predefinedItems.collectAsStateWithLifecycle()
    val personalItemsList by viewModel.personalItems.collectAsStateWithLifecycle()
    // State for the main screen scroll
    val screenScrollState = rememberScrollState()

    Scaffold(
        topBar = { LuPathTopBar(navController = navController) },
        containerColor = Color.White, // Or MaterialTheme.colorScheme.background
        bottomBar = { HomeBottomNav(navController) }
    ) { paddingValues ->

        // --- Outer Column handles the overall screen scrolling ---
        Column(
            modifier = Modifier
                .padding(paddingValues) // Apply padding from Scaffold
                .fillMaxSize()
                .verticalScroll(screenScrollState) // <<< OUTER SCROLL ENABLED
                .padding(bottom = 16.dp) // Padding at the very bottom of scrollable area
        ) {
            // --- Card Container for the Checklist ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    // Add horizontal/vertical padding for the card itself
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(10.dp), // Adjust shape as desired
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    // Set desired card background color
                    containerColor = Color(0xFFF0F0F0) // Example: Light Gray Card
                )
            ) {
                // --- Column for content *inside* the Card ---
                // --- This inner Column is NOT scrollable itself ---
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp), // Padding inside card
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Predefined Checklist Section ---
                    Text(
                        text = "Check List",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // Align title within the inner column
                            .padding(bottom = 8.dp)
                    )

                    // Use forEach to render items inside the card's column
                    predefinedItemsList.forEach { item ->
                        ChecklistItemRow(
                            item = item, // Pass the ChecklistItem object
                            onCheckedChange = { isChecked -> // Receive boolean from Checkbox/Row click
                                // Tell ViewModel to toggle based on the object
                                viewModel.togglePredefinedItemChecked(item)
                            }
                            // No onDelete for predefined
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "My personal Check List",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // Align title
                            .padding(bottom = 8.dp)
                    )

                    // --- Personal Checklist Section ---
                    if (personalItemsList.isEmpty()) {
                        Text(
                            text = "No personal items added yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        // Use forEach to render items inside the card's column
                        personalItemsList.forEach { item ->
                            ChecklistItemRow(
                                item = item, // Pass the ChecklistItem object
                                onCheckedChange = { isChecked -> // Receive boolean from Checkbox/Row click
                                    // Tell ViewModel to toggle based on the object
                                    viewModel.togglePersonalItemChecked(item)
                                },
                                onDelete = { viewModel.removePersonalItem(item) } // Pass the object
                            )
                        }
                    }

                    // --- Add Entry Button ---
                    AddEntryButton(
                        modifier = Modifier.padding(top = 16.dp), // Padding above button inside card
                        onClick = { showAddDialog = true }
                    )

                } // End of inner Column (inside Card)
            } // End of Card
        } // End of Outer Scrollable Column
    } // End of Scaffold

    // --- Dialog remains the same ---
    if (showAddDialog) {
        AddChecklistItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text ->
                viewModel.addPersonalItem(text)
                showAddDialog = false // Dismiss after adding
            }
        )
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
fun ChecklistItemRow(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!item.isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = GreenDark
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        // Show delete button only if callback is provided
        if (onDelete != null) {
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item ${item.text}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddEntryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(start = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = GreenDark
        )
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Add entry",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = GreenDark
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChecklistItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = GreenLight,
        onDismissRequest = onDismiss,
        title = { Text("Add Personal Checklist Item") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Item name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    // Colors when the TextField is focused (being typed in)
                    focusedBorderColor = GreenDark,
                    focusedLabelColor = GreenDark,
                    cursorColor = GreenDark,

                    // Colors when the TextField is not focused
                    unfocusedBorderColor = GreenDark,
                    unfocusedLabelColor = GreenDark,

                    // Text color
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text)
                        onDismiss()
                    }
                },
                enabled = text.isNotBlank() // Enable button only if text is entered
            ) {
                Text("Add", color = GreenDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GreenDark)
            }
        }
    )
}