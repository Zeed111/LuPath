package com.example.lupath.ui.screen.checkList

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.model.ChecklistItem
import com.example.lupath.data.model.ChecklistViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckListScreen(
    navController: NavHostController,
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val predefinedItemsList by viewModel.predefinedItems.collectAsStateWithLifecycle()
    val personalItemsList by viewModel.personalItems.collectAsStateWithLifecycle()
    val screenScrollState = rememberScrollState()

    Scaffold(
        topBar = { LuPathTopBar(navController = navController) },
        containerColor = Color.White,
        bottomBar = { HomeBottomNav(navController) }
    ) { paddingValues ->

        // --- Outer Column handles the overall screen scrolling ---
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(screenScrollState)
                .padding(bottom = 16.dp)
        ) {
            // --- Card Container for the Checklist ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F0F0)
                )
            ) {
                // --- Column for content *inside* the Card ---
                // --- This inner Column is NOT scrollable itself ---
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Predefined Checklist Section ---
                    Text(
                        text = "Check List",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )

                    // Use forEach to render items inside the card's column
                    predefinedItemsList.forEach { item ->
                        ChecklistItemRow(
                            item = item, // Pass the ChecklistItem object
                            onCheckedChange = {
                                viewModel.toggleItemChecked(item) // ViewModel uses the item's current state
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "My personal Check List",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
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
                                onCheckedChange = {
                                    viewModel.toggleItemChecked(item)
                                },
                                onDelete = { viewModel.removePersonalItem(item) } // Pass the object
                            )
                        }
                    }

                    // --- Add Entry Button ---
                    AddEntryButton(
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = { showAddDialog = true }
                    )

                }
            }
        }
    }

    // --- Dialog remains the same ---
    if (showAddDialog) {
        AddChecklistItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text ->
                viewModel.addPersonalItem(text)
                showAddDialog = false
            }
        )
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

                    focusedBorderColor = GreenDark,
                    focusedLabelColor = GreenDark,
                    cursorColor = GreenDark,

                    unfocusedBorderColor = GreenDark,
                    unfocusedLabelColor = GreenDark,

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
                enabled = text.isNotBlank()
            ) {
                Text("Add", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Black)
            }
        }
    )
}