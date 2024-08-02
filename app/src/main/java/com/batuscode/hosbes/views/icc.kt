package com.batuscode.hosbes.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@Composable
fun ICC(){
    Scaffold {innerPadding ->
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .wrapContentSize()
            ){
                Image(
                    painter = painterResource(id = R.drawable.istockphoto_517188688_612x612),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .width(80.dp)
                        .height(80.dp),
                    contentScale = ContentScale.FillBounds
                )


                Text(
                    text = "Batuhan"
                )

                Text(text = "Gelen sesli arama...")


            }


            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                IconButton(onClick = {
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_24px), contentDescription = "")
                }

                IconButton(onClick = {
                   // MainActivity.fm.declineCall()
                    MainActivity.navigate?.popBackStack()
                } ,
                    modifier = Modifier
                ) {
                    Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
                }
            }
        }
    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ICCPreview(){
    HoşbeşTheme {
        ICC()
    }
}