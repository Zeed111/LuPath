package com.example.lupath.ui.screen.mountainDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.theme.GreenLight
import com.google.accompanist.pager.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.lupath.ui.theme.GreenDark

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MountainDetailScreen(
    mountainName: String,
    navController: NavHostController
) {
    val pagerState = rememberPagerState()
    val tabTitles = listOf("Details", "Camping Spot", "Guidelines")
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("datepicker") },
                containerColor = Color.White,
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Pick Date", tint = Color.Black)
            }
        },
        bottomBar = {
            HomeBottomNav(navController) // bottom nav
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {

            // Swipeable Image
            Box {
                ImageCarouselSection()

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Mountain Pulag", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Lusod Kabayan, Benguet", fontSize = 14.sp, color = Color.Black)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp, // change pa sa need na icon, pansamantala lang
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Black
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Difficulty: Beginner", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Button(
                        onClick = { selectedTab = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == index) GreenDark else Color.LightGray
                        ),
                        elevation = null,
                        modifier = Modifier
                            .width(105.dp)
                            .height(40.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(title, color = Color.Black, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Tab Content
            when (selectedTab) {
                0 -> DetailsTabContent()
                1 -> CampingSpotTabContent()
                2 -> GuidelinesTabContent()
            }
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarouselSection() {
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp, // bottom-left corner
                    bottomEnd = 20.dp    // bottom-right corner
                )
            )
    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
//            Image(
//                painter = painterResource(id = R.drawable.lupath),
//                contentDescription = "Mountain Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GreenDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Image $page",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        )
    }
}

@Composable
fun DetailsTabContent() {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Deatils of the Mountain Soon to be Added",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
//        Text
    }
}

@Composable
fun CampingSpotTabContent() {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Camping Spot of the Mountain Soon to be Added",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
//        Text
    }
}

@Composable
fun GuidelinesTabContent() {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Guidelines of the Mountain Soon to be Added",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
//        Text
    }
}