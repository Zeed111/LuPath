package com.example.lupath.ui.screen.settings

import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lupath.R
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.Lato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val appVersion = "1.0.0"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center)
                    {
                        Text("About", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                            fontFamily = Lato) }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = GreenLight
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp), // Overall padding for the content
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.lupath), // Replace with your actual logo drawable
                    contentDescription = "LuPath App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "LuPath",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Version $appVersion",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Lato,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "About LuPath",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Hiking made easier with LuPath! Reach new " +
                            "heights by planning your upcoming trek with ease. Whether you are a seasoned " +
                            "hiker or a first-time hiker, our app helps plan your next Luzon hiking experience. " +
                            "Stay organized every step of the way with premade checklists to ensure you " +
                            "pack everything you need.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Lato,
                    textAlign = TextAlign.Justify, // Justify text for a cleaner look
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Developed By",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.Black
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenDark)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between developer names
                    ) {
                        val devList = listOf("Raven B. Viilanueva", "Daniel Guteirrez", "Mariel Yanga",
                            "Lenardo Jualo")
                        devList.forEach { devName ->
                            Text(
                                text = devName,
                                style = MaterialTheme.typography.bodyLarge, // More appropriate size
                                fontWeight = FontWeight.Medium,
                                fontFamily = Lato,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}