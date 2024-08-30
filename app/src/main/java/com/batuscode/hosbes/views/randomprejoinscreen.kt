package com.batuscode.hosbes.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@Composable
fun RandomPreJoinScreen(){
    Scaffold(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.White)
                .fillMaxSize(),
            contentAlignment = Alignment.Center

        ) {
            CircularProgressIndicator(
                strokeWidth = 5.dp ,
                modifier = Modifier
                    .width(205.dp)
                    .height(205.dp)
            )

            //TODO: eşleş butonu ...


            Text(
                text = stringResource(id = R.string.random) ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    fontSize = 40.sp
                )
            )


        }

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RandomPreJoinScreenPreview(){
    HoşbeşTheme {
        RandomPreJoinScreen()
    }
}