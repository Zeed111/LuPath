package com.example.lupath.ui.screen.mountainDetails

import android.content.Context
import android.inputmethodservice.Keyboard.Row
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.data.database.entity.CampsiteEntity
import com.example.lupath.data.database.entity.GuidelineEntity
import com.example.lupath.data.database.entity.MountainEntity
import com.example.lupath.data.database.entity.TrailEntity
import com.example.lupath.data.model.MountainDetailViewModel
import com.example.lupath.ui.screen.home.HomeBottomNav
import com.example.lupath.ui.screen.lupathList.LuPathTopBar
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.Lato
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.lazy.items

private data class CharacteristicDisplayInfo(
    val customDrawableName: String,
    val label: String,
)

/**
 * Helper function to convert a drawable name string (without extension)
 * to its corresponding Resource ID.
 * Returns null if the resource is not found or the name is blank.
 */
@DrawableRes
fun getDrawableResIdFromString(context: Context, drawableName: String?): Int? {
    if (drawableName.isNullOrBlank()) return null
    // The name passed to getIdentifier should not include the file extension.
    val nameWithoutExtension = drawableName.substringBeforeLast('.')
    val resId = context.resources.getIdentifier(nameWithoutExtension, "drawable", context.packageName)
    return if (resId != 0) resId else null // Return null if not found (resId will be 0)
}

data class LegendItemData(
    val customDrawableName: String,
    val label: String,
    val detailedDescription: String
)

fun getAllLegendItems(): List<LegendItemData> {
    return listOf(
        LegendItemData("trail_icon", "Established Trail", "Indicates a well-defined and established " +
                "path, generally easy to follow."),
        LegendItemData("rocky_icon", "Rocky", "Trail has many rocks, uneven surfaces, or " +
                "scree. Sturdy footwear with ankle support is recommended."),
        LegendItemData("slippery_icon", "Slippery", "Trail may be slick due to mud, wet " +
                "rocks, or loose gravel, especially after rain. Exercise caution and consider trekking poles."),
        LegendItemData("steep_icon", "Steep", "Contains significant uphill or downhill " +
                "sections. May require good stamina and careful footing."),
        LegendItemData("wildlife_icon", "Wildlife", "Possibility of encountering " +
                "local fauna. Observe from a distance and do not feed animals.")
        // Add entries for ALL other characteristic icons you use, with their detailed descriptions.
        // Ensure the customDrawableName matches the filenames in res/drawable.
    )
}

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
    var showLegendDialog by remember { mutableStateOf(false) }

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
//                            val characteristics = mutableListOf<CharacteristicDisplayInfo>()
//
//                            if (mountain.isEstablishedTrail == true) {
//                                // Replace "your_trail_icon_name" with the actual filename of your custom trail icon
//                                characteristics.add(CharacteristicDisplayInfo("trail_icon", "Established Trail"))
//                            }
//                            if (mountain.isRocky == true) {
//                                // Replace "your_rocky_icon_name" with the actual filename
//                                characteristics.add(CharacteristicDisplayInfo("rocky_icon", "Rocky"))
//                            }
//                            if (mountain.isSlippery == true) {
//                                // Replace "your_slippery_icon_name" with the actual filename
//                                characteristics.add(CharacteristicDisplayInfo("slippery_icon", "Slippery"))
//                            }
//                            if (mountain.hasSteepSections == true) {
//                                // Replace "your_steep_icon_name" with the actual filename
//                                characteristics.add(CharacteristicDisplayInfo("steep_icon", "Steep"))
//                            }
//                            if (!mountain.notableWildlife.isNullOrBlank()) {
//                                // Replace "your_generic_wildlife_icon_name" with the actual filename
//                                characteristics.add(CharacteristicDisplayInfo("wildlife_icon", "Wildlife"))
//                            }

                            val characteristics = remember(mountain) { // Recompute if mountain changes
                                listOfNotNull(
                                    if (mountain.isEstablishedTrail == true) CharacteristicDisplayInfo("trail_icon", "Established Trail") else null,
                                    if (mountain.isRocky == true) CharacteristicDisplayInfo("rocky_icon", "Rocky") else null,
                                    if (mountain.isSlippery == true) CharacteristicDisplayInfo("your_slippery_icon_name", "Slippery") else null,
                                    if (mountain.hasSteepSections == true) CharacteristicDisplayInfo("steep_icon", "Steep") else null,
                                    if (!mountain.notableWildlife.isNullOrBlank()) CharacteristicDisplayInfo("wildlife_icon", "Wildlife") else null
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                // Center the group of (InfoButton + Icons) if the Row takes full width
                                // If you want InfoButton strictly left and icons next to it, use Arrangement.Start
                                horizontalArrangement = Arrangement.Start // Align InfoButton to the start
                            ) {
                                // Info Button on the left
                                IconButton(
                                    onClick = { showLegendDialog = true },
                                    modifier = Modifier.size(22.dp) // Adjust size for touchability
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "View trail condition legend",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp)) // Space between info button and the first icon

                                // Row for the actual characteristic icons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between each characteristic icon
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState())
                                ) {
                                    if (characteristics.isNotEmpty()) {
                                        characteristics.forEach { characteristicInfo ->
                                            CharacteristicIcon( // Your composable for displaying custom drawables
                                                drawableName = characteristicInfo.customDrawableName,
                                                label = characteristicInfo.label
                                            // The CustomCharacteristicIcon can decide whether to show the label or not
                                            // For a compact view like rating icons, you might omit the text label within CustomCharacteristicIcon
                                            )
                                        }
                                    } else {
                                        // Optional: Add a small spacer if no icons are present,
                                        // so the difficulty text alignment remains consistent.
//                                        Spacer(Modifier.height(20.dp)) // Approximate height of an icon row
                                    }
                                }
                            }

                            // --- Difficulty Text ---
                            Text(
                                text = mountain.difficultyText ?: "N/A", // Using the detailed difficulty
                                fontSize = 12.sp,
                                fontFamily = Lato, // Ensure Lato is set up
                                textAlign = TextAlign.Center, // Ensures the text itself is centered if it wraps
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme colors
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
                                campsites = currentMountainData.campsites, trails = currentMountainData.trails // Pass the list of campsites
                            )
                            2 -> GuidelinesTabContent(
                                guidelines = currentMountainData.guidelines // Pass the list of guidelines
                            )
                        }
                    }
                } // End of else (data loaded)
            } // End Scrollable Inner Column
        } // End Outer Column
        if (showLegendDialog) {
            CharacteristicLegendDialog(
                onDismissRequest = { showLegendDialog = false },
                legendItems = getAllLegendItems()
            )
        }
    } // End Scaffold
}

@Composable
fun CharacteristicIcon(
    drawableName: String,
    label: String,
    modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Use remember to avoid re-calculating on every recomposition if drawableName doesn't change
    val imageResId = remember(drawableName) {
        getDrawableResIdFromString(context, drawableName)
    }
    if (imageResId != null && imageResId != 0) { // Check if resource ID is valid
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.width(20.dp) // Give each icon a fixed width for better spacing
        ) {
            Image( // Use Image composable for drawables
                painter = painterResource(id = imageResId),
                contentDescription = label, // Important for accessibility
                modifier = Modifier.size(20.dp), // Adjust size as needed
                contentScale = ContentScale.Fit // Or Crop, depending on your icons
            )
        }
    } else {
        // Optionally, log an error or display a fallback if an icon name is provided but not found
        Log.w("CustomCharacteristicIcon", "Drawable resource not found for name: $drawableName")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacteristicLegendDialog(
    onDismissRequest: () -> Unit,
    legendItems: List<LegendItemData>
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Trail Condition Legend", style = MaterialTheme.typography.titleLarge) },
        text = {
            if (legendItems.isEmpty()) {
                Text("No legend items to display.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Use items extension with explicit key and item type
                    items(
                        items = legendItems,
                        key = { item -> item.customDrawableName + item.label } // Provide a stable and unique key
                    ) { item: LegendItemData -> // Explicitly type 'item'
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            val context = LocalContext.current
                            val imageResId = remember(item.customDrawableName) {
                                getDrawableResIdFromString(context, item.customDrawableName)
                            }

                            if (imageResId != null && imageResId != 0) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            } else {
                                Spacer(modifier = Modifier.size(32.dp)) // Placeholder
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.label,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.detailedDescription,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        // Optional: Add a Divider if it's not the last item.
                        // It's often cleaner to add spacing via Arrangement or padding in LazyColumn/Row.
                        if (legendItems.indexOf(item) < legendItems.size - 1) {
                            // Divider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("CLOSE")
            }
        },
        containerColor = Color.White,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
fun DetailItem(label: String, value: String?) {
    // Only display the item if the value is not null or blank
    if (!value.isNullOrBlank()) {
        // Use buildAnnotatedString to combine styled text
        Text(
            text = buildAnnotatedString {
                // Append the label part with bold style
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                    append("$label: ") // Add colon and space after label
                }
                // Append the value part with regular style
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                    append(value)
                }
            },
            style = MaterialTheme.typography.bodyMedium, // Base style for the whole text
            modifier = Modifier.padding(bottom = 6.dp), // Consistent padding
            color = MaterialTheme.colorScheme.onSurface // Use theme color
        )
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
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Add some spacing between items

        // Special handling for Introduction as it might not have a "Label: Value" format
        introduction?.let {
            if (it.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                            append("Introduction:\n") // Label part
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                            append(it) // Value part
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp), // More padding after intro
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        DetailItem(label = "Type", value = typeVolcano)
        DetailItem(label = "MASL", value = masl?.let { "$it m" }) // Convert Int to String with unit
        DetailItem(label = "Trek Duration", value = trekDuration)
        DetailItem(label = "Trail Type", value = trailType)

        // For multi-line values like scenery, views, etc., the label will be bold and value normal
        scenery?.let {
            if (it.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                            append("Scenery:\n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        views?.let {
            if (it.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                            append("Views:\n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        wildlife?.let {
            if (it.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                            append("Wildlife:\n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        features?.let {
            if (it.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = Lato)) {
                            append("Features:\n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontFamily = Lato)) {
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        DetailItem(label = "Best Season", value = hikingSeason)
    }
}

@Composable
fun CampingSpotTabContent(
    campsites: List<CampsiteEntity>,
    trails: List<TrailEntity>
) { // Assuming CampsiteEntity has relevant fields
    Column {
        if (campsites.isEmpty()) {
            Text("No specific campsite information available.")
        } else {
            campsites.forEachIndexed { index, campsite ->
                Column {
                    Text(
                        // Provide a more descriptive fallback for null names
                        text = campsite.name ?: " ",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall, // Consider titleMedium if it's a primary heading for the item
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Handle nullable description
                    campsite.description?.let { desc ->
                        if (desc.isNotBlank()) { // Only show if description is not blank
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (index < campsites.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp)) // << YOUR DESIRED SPACE BETWEEN CAMPSITE ITEMS
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (trails.isEmpty()) {
            Text("No specific trails information available.")
        } else {
            trails.forEachIndexed { index, trail ->
                Column {
                    Text(
                        // Provide a more descriptive fallback for null names
                        text = trail.name ?: " ",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall, // Consider titleMedium if it's a primary heading for the item
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Handle nullable description
                    trail.description?.let { desc ->
                        if (desc.isNotBlank()) { // Only show if description is not blank
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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