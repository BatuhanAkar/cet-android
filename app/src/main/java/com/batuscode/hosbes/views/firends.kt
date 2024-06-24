package com.batuscode.hosbes.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@Composable
fun Friends(){
    Scaffold {
        Column (
            modifier = Modifier.padding(it)
        ) {
            Text(text = "2")
        }
    }
}

@Preview(showSystemUi = true , showBackground = true)
@Composable
fun FriendsPreview(){
    HoşbeşTheme {
        Friends()
    }
}