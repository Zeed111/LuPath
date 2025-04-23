package com.example.lupath.ui.screen.toBringList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: ChecklistViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { LuPathTopBar(navController) },
        containerColor = Color.White,
        bottomBar = { HomeBottomNav(navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .width(325.dp)
                    .height(629.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Predefined Checklist Section ---
                    item {
                        Text(
                            text = "Check List",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,

                            )
                    }

                    items(viewModel.predefinedItems, key = {it.id}) {
                        item ->
                        ChecklistItemRow(
                            item = item,
                            onCheckedChange = { isChecked ->
                                // Pass the original item to find it by ID
                                viewModel.toggleItemChecked(viewModel.predefinedItems, item)
                            },
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        Text(
                            text = "My personal Check List",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,

                            )
                    }

                    // --- Personal Checklist Section ---
                    if (viewModel.personalItems.isEmpty()) {
                        item {
                            Text(
                                text = "No personal items added yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(viewModel.personalItems, key = {it.id}) {
                            item ->
                        ChecklistItemRow(
                            item = item,
                            onCheckedChange = { isChecked ->
                                // Pass the original item to find it by ID
                                viewModel.toggleItemChecked(viewModel.personalItems, item)
                            },
                            onDelete = { viewModel.removePersonalItem(item) }
                        )
                    }

                    // --- Add Entry Button ---
                    item {
                        AddEntryButton(
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = { showAddDialog = true }
                        )
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddChecklistItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text ->
                viewModel.addPersonalItem(text)
                showAddDialog = true
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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Image(
            painter = painterResource(id = R.drawable.lupath),
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
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Add entry",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
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
        onDismissRequest = onDismiss,
        title = { Text("Add Personal Checklist Item") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Item name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
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
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}