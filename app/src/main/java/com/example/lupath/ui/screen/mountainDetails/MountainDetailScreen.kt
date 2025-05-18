package com.example.lupath.ui.screen.mountainDetails

import android.content.Context
import android.inputmethodservice.Keyboard.Row
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.database.entity.CampsiteEntity
import com.example.lupath.data.database.entity.GuidelineEntity
import com.example.lupath.data.database.entity.MountainEntity
import com.example.lupath.data.model.MountainDetailViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.screen.lupathList.LuPathTopBar
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.Lato
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MountainDetailScreen(
    mountainIdFromNav: String,
    navController: NavHostController
) {

    val tabTitles = listOf("Details", "Camping Spot", "Guidelines")
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val viewModel: MountainDetailViewModel = hiltViewModel()
    val mountainDetailsState by viewModel.mountainWithDetails.collectAsStateWithLifecycle()

    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent, // Start transparent
        scrolledContainerColor = MaterialTheme.colorScheme.surface, // Becomes opaque on scroll
        // You might need to adjust title/icon colors too if the scrolled color clashes
        // titleContentColor = MaterialTheme.colorScheme.onSurface,
        // navigationIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            LuPathTopBar(navController = navController)
//            TopAppBar(
//                title = { },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                colors = topAppBarColors
//            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("datepicker/$mountainIdFromNav") },
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 16.dp)
            ) {

                Spacer(Modifier.height(12.dp))

                val currentMountainData = mountainDetailsState

                if (currentMountainData == null) {
                    // --- Loading State or Error State ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp), // Give it some space
                        contentAlignment = Alignment.Center
                    ) {
                        // You could check for an error state in your ViewModel
                        // if (viewModel.hasError) { Text("Failed to load details.") } else {
                        CircularProgressIndicator() // Show loading indicator
                        // }
                        Text("Loading details for $mountainIdFromNav...")
                    }
                } else {
                    val mountain: MountainEntity = currentMountainData.mountain

                    // Prepare images for the carousel from MountainEntity
                    val imageResourceIds = mutableListOf<Int>()

                    // Get carousel image 1 (can be the same as main pictureReference)
                    getDrawableResIdFromString(context, mountain.mountainImageRef1 ?: mountain.pictureReference)?.let {
                        imageResourceIds.add(it)
                    }
                    // Get carousel image 2
                    getDrawableResIdFromString(context, mountain.mountainImageRef2)?.let {
                        imageResourceIds.add(it)
                    }
                    // Get carousel image 3
                    getDrawableResIdFromString(context, mountain.mountainImageRef3)?.let {
                        imageResourceIds.add(it)
                    }

                    // If after trying to load specific images, the list is still empty,
                    // or you want to ensure a minimum number of images, add placeholders.
                    // For this example, if no specific images are found, we'll use one placeholder.
                    // If some specific images are found, we won't add more placeholders unless you want to guarantee 3.
                    if (imageResourceIds.isEmpty()) {
                        imageResourceIds.add(R.drawable.mt_pulag_ex) // Default placeholder
                    }
                    // To ensure always 3 images, you could fill up with placeholders:
                    // val placeholders = listOf(R.drawable.mt_pulag_ex_2, R.drawable.mt_pulag_ex_3)
                    // var currentPlaceholderIndex = 0
                    // while (imageResourceIds.size < 3 && currentPlaceholderIndex < placeholders.size) {
                    //    if (!imageResourceIds.contains(placeholders[currentPlaceholderIndex])) { // Avoid duplicate placeholders
                    //        imageResourceIds.add(placeholders[currentPlaceholderIndex])
                    //    }
                    //    currentPlaceholderIndex++
                    // }


                    ImageCarouselSection(imageResIds = imageResourceIds.distinct())



                    Spacer(Modifier.height(12.dp))

                    // --- Mountain Title, Location, Difficulty Row ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(0.6f)) { // Give more weight to title/location
                            Text(
                                mountain.mountainName, // <<< FROM FETCHED DATA
                                fontWeight = FontWeight.Bold, fontSize = 20.sp,
                                fontFamily = Lato
                            )
                            Text(
                                mountain.location, // <<< FROM FETCHED DATA
                                fontSize = 14.sp, color = Color.Black,
                                fontFamily = Lato
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.4f) // Less weight for difficulty
                        ) {
                            Row { // Placeholder for rating icons
                                repeat(3) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp, // Error likely here
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(Modifier.width(6.dp))
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                // Assuming difficultyText is the field in MountainEntity
                                mountain.difficultyText, // <<< FROM FETCHED DATA
                                fontSize = 12.sp, fontFamily = Lato,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // --- Tabs Row ---
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
                                Text(title, color = Color.Black, fontSize = 12.sp, maxLines = 1,
                                    fontFamily = Lato)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // --- Tab Content ---
                    // Pass relevant data (e.g., mountain.introduction, campsites, trails, guidelines)
                    // to your tab content composables.
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        when (selectedTab) {
                            0 -> DetailsTabContent(
                                introduction = mountain.introduction,
                                typeVolcano = mountain.typeVolcano,
                                masl = mountain.masl,
                                trekDuration = mountain.trekDurationDetails ?: mountain.hoursToSummit,
                                trailType = mountain.trailTypeDescription,
                                scenery = mountain.sceneryDescription,
                                views = mountain.viewsDescription,
                                wildlife = mountain.wildlifeDescription,
                                features = mountain.featuresDescription,
                                hikingSeason = mountain.hikingSeasonDetails ?: mountain.bestMonthsToHike
                                // Pass other details as needed
                            )
                            1 -> CampingSpotTabContent(
                                campsites = currentMountainData.campsites // Pass the list of campsites
                            )
                            2 -> GuidelinesTabContent(
                                guidelines = currentMountainData.guidelines // Pass the list of guidelines
                            )
                        }
                    }
                } // End of else (data loaded)
            } // End Scrollable Inner Column
        } // End Outer Column
    } // End Scaffold
}

@DrawableRes
fun getDrawableResIdFromString(context: Context, drawableName: String?): Int? {
    if (drawableName.isNullOrBlank()) return null
    val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    return if (resId != 0) resId else null
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarouselSection(imageResIds: List<Int>) {
    val pagerState = rememberPagerState()

    if (imageResIds.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No images available") // Or a placeholder Icon
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
    ) {
        HorizontalPager(
            count = imageResIds.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = imageResIds[page]),
                contentDescription = "Mountain Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (imageResIds.size > 1) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                activeColor = GreenDark,
                inactiveColor = GreenDark.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun DetailsTabContent(
    introduction: String?,
    typeVolcano: String?,
    masl: Int?,
    trekDuration: String?,
    trailType: String?,
    scenery: String?,
    views: String?,
    wildlife: String?,
    features: String?,
    hikingSeason: String?
    // ... other parameters
) {
    Column { // Make this scrollable if content can be very long
        introduction?.let { Text("Introduction:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=8.dp)) }
        typeVolcano?.let { Text("Type: $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        masl?.let { Text("MASL: $it m", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        trekDuration?.let { Text("Trek Duration: $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        trailType?.let { Text("Trail Type: $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        scenery?.let { Text("Scenery:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        views?.let { Text("Views:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        wildlife?.let { Text("Wildlife:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        features?.let { Text("Features:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
        hikingSeason?.let { Text("Best Season:\n$it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=4.dp)) }
    }
}

@Composable
fun CampingSpotTabContent(campsites: List<CampsiteEntity>) { // Assuming CampsiteEntity has relevant fields
    Column {
        if (campsites.isEmpty()) {
            Text("No specific campsite information available.")
        } else {
            campsites.forEach { campsite ->
                Text(campsite.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                campsite.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                // Add more campsite details as needed (trek time, water, etc.)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun GuidelinesTabContent(guidelines: List<GuidelineEntity>) { // Assuming GuidelineEntity has category & description
    Column {
        if (guidelines.isEmpty()) {
            Text("No specific guidelines available.")
        } else {
            // Group guidelines by category for better display
            guidelines.groupBy { it.category }.forEach { (category, guidelineList) ->
                Text(category, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
                guidelineList.forEach { guideline ->
                    Text("• ${guideline.description}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}