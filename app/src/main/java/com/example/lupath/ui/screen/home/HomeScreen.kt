package com.example.lupath.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lupath.R
import com.example.lupath.ui.theme.Lato
import java.net.URLEncoder

object Routes {
    const val LUPATH_LIST = "lupath_list"
    const val HOME = "home"
    const val CHECK_LIST = "check_list"
    // Add other routes...
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color.White,
        topBar = { HomeTopBar(navController = navController) },
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        HomeContent(Modifier.padding(padding), navController = navController)
    }
}

@Composable
fun HomeBottomNav(navController: NavHostController,) {
    // 1. Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // 2. Get the current route name
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFC0D9C6)
        // Consider using MaterialTheme.colorScheme.surfaceVariant or similar
    ) {
        // --- Lupath Item ---
        NavigationBarItem(
            // 3. Set selected based on current route
            selected = currentRoute == Routes.LUPATH_LIST,
            onClick = {
                // 4. Navigate only if not already on this screen
                if (currentRoute != Routes.LUPATH_LIST) {
                    navController.navigate(Routes.LUPATH_LIST) {
                        // Optional: Pop up to the start destination graph to avoid building up back stack
                        // popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        // Optional: Restore state if popping up
                        // restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = "Lupath",
                    modifier = Modifier.size(37.dp),
                    tint = if (currentRoute == Routes.LUPATH_LIST) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black // Example selected tint
                )
            },
            label = { Text("Lupath", color = Color.Black, fontFamily = Lato) },
            // Customize colors for selected/unselected states
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Gray // Example indicator color
                // unselectedIconColor = Color.Black,
                // selectedIconColor = ..., // Set explicitly if needed
                // unselectedTextColor = Color.Black,
                // selectedTextColor = ...
            )
        )

        // --- Home Item ---
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = {
                if (currentRoute != Routes.HOME) {
                    navController.navigate(Routes.HOME) {
                        // popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        // restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(37.dp),
                    tint = if (currentRoute == Routes.HOME) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black // Example selected tint
                )
            },
            label = { Text("Home", color = Color.Black, fontFamily = Lato) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Gray
            )
        )

        // --- List Item ---
        NavigationBarItem(
            selected = currentRoute == Routes.CHECK_LIST,
            onClick = {
                if (currentRoute != Routes.CHECK_LIST) {
                    navController.navigate(Routes.CHECK_LIST) {
                        // popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        // restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "List",
                    modifier = Modifier.size(37.dp),
                    tint = if (currentRoute == Routes.CHECK_LIST) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black // Example selected tint
                )
            },
            label = { Text("List", color = Color.Black, fontFamily = Lato) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Gray
            )
        )
    }
}

@Composable
fun HomeContent(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(4.dp)
    ) {
        SearchBar()
        PopularMountainsSection(navController = navController)
        MountainListSection(navController = navController)
    }
}

@Composable
fun HomeTopBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("LuPath", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                    fontFamily = Lato)
            }
            IconButton(onClick = {
                navController.navigate("settings")
            }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search for mountains...", fontFamily = Lato) },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}

@Composable
fun PopularMountainsSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Popular Mountains", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = Lato)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(5) { index ->
                PopularMountainCard(name = "Mt. Popular $index", location = "Location $index",
                    navController = navController)
            }
        }
    }
}

@Composable
fun MountainListSection(navController: NavHostController) {
    val allMountains = listOf(
        "Mt. Pulag", "Mt. Apo", "Mt. Batulao", "Mt. Ulap", "Mt. Maculot",
        "Mt. Arayat", "Mt. Mayon", "Mt. Pico de Loro"
    )
    var showAll by remember { mutableStateOf(false) }
    val visibleMountains = if (showAll) allMountains else allMountains.take(5)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All Mountains", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = Lato)
        Spacer(modifier = Modifier.height(8.dp))

        visibleMountains.forEach { mountain ->
            MountainListCard(
                name = mountain,
                difficulty = "Beginner friendly",
                description = "A beautiful mountain with scenic views and easy trails.",
                navController = navController
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { showAll = !showAll },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = (Color(0xFFD9D9D9))
            )
        ) {
            Text(
                text = if (showAll) "View Less" else "View More",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = Lato
            )
        }
    }
}

@Composable
fun PopularMountainCard(name: String, location: String, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .width(150.dp)
            .padding(end = 16.dp)
            .clickable {
                navController.navigate("mountainDetail/${URLEncoder.encode(name, "UTF-8")}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD9D9D9)) // Optional: Keeps it bright even in dark mode
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black,
                fontFamily = Lato)
            Text(location, fontSize = 12.sp, color = Color.Black, fontFamily = Lato)
        }
    }
}

@Composable
fun MountainListCard(name: String, difficulty: String, description: String, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                navController.navigate("mountainDetail/${URLEncoder.encode(name, "UTF-8")}")
            },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors( // Add the 'colors' parameter
            containerColor = Color(0xFFD9D9D9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD9D9D9)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual Image composable if imageUrl is provided
                // Example:
                // if (imageUrl != null) {
                //     Image(painter = rememberAsyncImagePainter(imageUrl), contentDescription = mountainName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                // } else {
                //     Icon(Icons.Default.Landscape, contentDescription = "Placeholder", tint = Color.White) // Placeholder Icon
                // }
                Text("Image", color = Color.White)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)

            ) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black,
                    fontFamily = Lato)
                Text(difficulty, fontSize = 14.sp, color = Color.Black, fontFamily = Lato)
                Text(description, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = Color.Black, fontFamily = Lato)
            }
        }
    }
}



