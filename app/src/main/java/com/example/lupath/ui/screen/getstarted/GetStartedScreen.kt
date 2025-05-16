package com.example.lupath.ui.screen.getstarted

import com.example.lupath.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lupath.ui.theme.GreenLight
import com.example.lupath.ui.theme.LuPathTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.lupath.data.model.GetStartedViewModel
import com.example.lupath.ui.theme.GreenDark
import com.example.lupath.ui.theme.Lato


@Composable
fun GetStartedScreen(
    viewModel: GetStartedViewModel = viewModel(),
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

@Preview(showBackground = true, name = "Get Started Screen Preview")
@Composable
fun GetStartedScreenPreview() {
    LuPathTheme {
        GetStartedScreen(
            viewModel = FakeGetStartedViewModel(),
            onNavigateToHome = {}
        )
    }
}

class FakeGetStartedViewModel : GetStartedViewModel() {
    private val _message = MutableStateFlow("Focus, relax and find your next adventure here in Lupath")
    override val welcomeMessage: StateFlow<String> = _message

    override fun onGetStartedClicked() {
        // lalagyan palang
    }
}