package com.batuscode.hosbes.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@Composable
fun SplashScreen(){
    var isVisible by remember {
        mutableStateOf(false)
    }
    val visibilityAlpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f ,
        animationSpec = tween(durationMillis = 5000)
    )
    LaunchedEffect(Unit) {
        isVisible = true
    }
    Scaffold (
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
            innerPadding ->

        AnimatedVisibility(
            visible = isVisible ,
            enter = slideInVertically(initialOffsetY = {-40} ) + fadeIn(initialAlpha = 0.3f) ,
            exit = slideOutVertically(targetOffsetY = {-40}) + fadeOut()
        ) {

            Column ( modifier = Modifier
                .alpha(visibilityAlpha)
                .padding(innerPadding)
                .fillMaxSize(), verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {

                Text(text = stringResource(id = R.string.app_name) ,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                        fontSize = 80.sp
                    )
                )


                Text(
                    text = stringResource(id = R.string.fornewfriends) ,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                        fontSize = 40.sp ,
                    )
                )

            }


        }
    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun SplashScreenPreview(){
    HoşbeşTheme {
        SplashScreen()
    }
}