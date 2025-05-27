package com.example.lupath.ui.screen.getstarted

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lupath.R
import com.example.lupath.data.model.GetStartedViewModel
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.Lato


@Composable
fun GetStartedScreen(
    viewModel: GetStartedViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val message by viewModel.welcomeMessage.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.get_started_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lupath),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(1.dp))

                Text(
                    text = message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 35.dp, end = 35.dp)
                        .width(341.dp)
                        .height(72.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = Lato
                )
            }

            Button(
                onClick = {
                    viewModel.onGetStartedClicked()
                    onNavigateToHome()
                },
                colors = buttonColors(
                    containerColor = GreenDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .width(335.dp)
                    .height(67.dp)
            ) {
                Text("Get Started", fontSize = 30.sp, fontFamily = Lato)
            }
        }
    }
}