package com.example.lupath.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.compose.rememberNavController
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato
import java.net.URLEncoder

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color.White,
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomNav(navController) }
    ) { padding ->
        HomeContent(Modifier.padding(padding), navController = navController)
    }
}

@Composable
fun HomeBottomNav(navController: NavHostController) {
    NavigationBar (
        containerColor = Color(0xFFC0D9C6)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("lupath_list/Mt. Pulag/No%20date") {
                launchSingleTop = true
            } },
            icon = { Icon( painter = painterResource(id = R.drawable.lupath), contentDescription = "Lupath",
                modifier = Modifier.size(37.dp), tint = Color.Black) },
            label = { Text("Lupath", color = Color.Black, fontFamily = Lato) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home"){
                launchSingleTop = true
            } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(37.dp),
                tint = Color.Black) },
            label = { Text("Home", color = Color.Black, fontFamily = Lato) },
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navigate to List */ },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List",
                modifier = Modifier.size(37.dp), tint = Color.Black) },
            label = { Text("List", color = Color.Black, fontFamily = Lato) }
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
fun HomeTopBar() {
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
            IconButton(onClick = { /* open settings */ }) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("mountainDetail/${URLEncoder.encode(name, "UTF-8")}")
            }
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFFD9D9D9))
                .padding(8.dp)
                .height(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
                    .background(Color.Gray, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
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



