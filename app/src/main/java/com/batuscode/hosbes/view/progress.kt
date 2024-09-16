package com.batuscode.hosbes.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@Composable
fun Progress(){

    Column ( horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
    ){

        CircularProgressIndicator(
            color = Color.Blue ,
            strokeWidth = 5.dp ,
            strokeCap = StrokeCap.Round ,
            modifier = Modifier
        )
    }


}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ProgressPreview(){
    HoşbeşTheme {
        Progress()
    }
}