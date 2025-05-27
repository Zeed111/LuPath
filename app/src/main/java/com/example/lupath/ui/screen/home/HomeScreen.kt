package com.example.lupath.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lupath.R
import com.example.lupath.data.model.HomeViewModel
import com.example.lupath.data.model.Mountain
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato

object Routes {
    const val LUPATH_LIST = "lupath_list"
    const val HOME = "home"
    const val CHECK_LIST = "check_list"
}

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel =  hiltViewModel()) {
    val popularMountainsList by viewModel.popularMountains.collectAsStateWithLifecycle()
    val allMountainsList by viewModel.allMountains.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        topBar = { HomeTopBar(navController = navController) },
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
            popularMountains = popularMountainsList,
            allMountains = allMountainsList,
            searchQuery = searchQuery,
            onSearchQueryChange = { query -> viewModel.onSearchQueryChanged(query) },
            onMountainClick = { mountainId ->
                try {
                    navController.navigate("mountainDetail/$mountainId")
                } catch (e: Exception) {
                    println("Navigation error: $e")
                }
            },
            navController = navController
        )
    }
}

@Composable
fun HomeBottomNav(navController: NavHostController) {
    //  Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //  Get the current route name
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFC0D9C6)
    ) {
        // --- Lupath Item ---
        NavigationBarItem(
            selected = currentRoute == Routes.LUPATH_LIST,
            onClick = {
                if (currentRoute != Routes.LUPATH_LIST) {
                    navController.navigate(Routes.LUPATH_LIST) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = "Lupath",
                    modifier = Modifier.size(37.dp),
                    tint = if (currentRoute == Routes.LUPATH_LIST) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black
                )
            },
            label = { Text("Lupath", color = Color.Black, fontFamily = Lato) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Gray
            )
        )

        // --- Home Item ---
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = {
                if (currentRoute != Routes.HOME) {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                        restoreState = true
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
                        launchSingleTop = true
                         restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "List",
                    modifier = Modifier.size(37.dp),
                    tint = if (currentRoute == Routes.CHECK_LIST) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black
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
fun HomeContent(
    modifier: Modifier = Modifier,
    popularMountains: List<Mountain>,
    allMountains: List<Mountain>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMountainClick: (mountainId: String) -> Unit,
    navController: NavHostController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        PopularMountainsSection(
            popularMountains = popularMountains,
            onMountainClick = onMountainClick,
            modifier = Modifier.padding(top = 8.dp)
        )

        MountainListSection(
            allMountains = allMountains,
            navController = navController,
            modifier = Modifier.padding(top = 16.dp)
        )
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
                .statusBarsPadding()
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange, // Call the lambda to update ViewModel
        placeholder = { Text("Search for mountains...", fontFamily = Lato) },
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun PopularMountainsSection(
    popularMountains: List<Mountain>,
    onMountainClick: (mountainId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Popular Mountains", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = Lato)
        Spacer(modifier = Modifier.height(8.dp))

        if (popularMountains.isEmpty()) {
            Text("No popular mountains to display.", modifier = Modifier.padding(vertical = 8.dp))
        } else {
            LazyRow(
                contentPadding = PaddingValues(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = popularMountains,
                    key = { mountain -> mountain.id }
                ) { mountain ->
                    PopularMountainCard(
                        mountain = mountain,
                        onClick = {
                            try {
                                // Navigate using ID
                                onMountainClick(mountain.id)
                            } catch (e: Exception) {
                                println("Navigation error: $e")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MountainListSection(
    allMountains: List<Mountain>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var showAll by remember { mutableStateOf(false) }
    val visibleMountains = if (showAll) allMountains else allMountains.take(5)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All Mountains", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = Lato)
        Spacer(modifier = Modifier.height(8.dp))

        if (visibleMountains.isEmpty() && allMountains.isNotEmpty() && !showAll) {
            Text("No mountains to display in this view.", modifier = Modifier.padding(vertical = 8.dp))
        } else if (allMountains.isEmpty()){
            Text("No mountains available yet.", modifier = Modifier.padding(vertical = 8.dp))
        }
        else {
            visibleMountains.forEach { mountain ->
                MountainListCard(
                    mountain = mountain,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (allMountains.size > 5) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { showAll = !showAll },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
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
}

@Composable
fun PopularMountainCard(mountain: Mountain,  onClick: () -> Unit) {
    val cardHeight = 200.dp
    val imageHeight = 100.dp
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .width(150.dp)
            .height(cardHeight)
            .padding(end = 16.dp)
            .clickable (onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GreenLight)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mountain.imageResId != null) { // Check for actual image
                Image(
                    painter = painterResource(id = mountain.imageResId),
                    contentDescription = mountain.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                )
            } else { // Fallback placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image", color = Color.White)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(mountain.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = Lato,
                maxLines = 2, // Allow location to take up to 2 lines
                overflow = TextOverflow.Ellipsis,
                minLines = 1
            )
            Text(mountain.location,
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = Lato,
                maxLines = 2, // Allow location to take up to 2 lines
                overflow = TextOverflow.Ellipsis,
                minLines = 1
            )
        }
    }
}

@Composable
fun MountainListCard(mountain: Mountain, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                try {
                    navController.navigate("mountainDetail/${mountain.id}")
                } catch (e: Exception) {
                    println("Error navigating to detail: $e")
                }
            },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
                containerColor = GreenLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = mountain.imageResId ?: R.drawable.mt_pulag_ex
                ),
                contentDescription = "Image of ${mountain.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)

            ) {
                Text(mountain.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black,
                    fontFamily = Lato)
                Text(mountain.difficultySummary.toString(), fontSize = 14.sp, color = Color.Black, fontFamily = Lato)
                Text(
                    text = mountain.tagline ?: "No tagline available", fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = Color.Black, fontFamily = Lato)
            }
        }
    }
}