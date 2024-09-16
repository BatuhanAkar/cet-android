package com.batuscode.hosbes.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.MainActivityVM

@Composable
fun ConnectionCorridor(mainActivityVM: MainActivityVM){
    val streamChannelType by mainActivityVM.streamChannelType.collectAsState()
    val selectedChannel by mainActivityVM.selectedChannel.collectAsState()

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text =
                if (streamChannelType?.equals("video") == true) selectedChannel + " " + stringResource(id = R.string.connectingvideochannel)
                else if (streamChannelType?.equals("voice") == true) selectedChannel + " " + stringResource(id = R.string.connectingvoicechannel)
                else "Hobbalaaa , bir şeyler ters gitti." ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    fontSize = 20.sp ,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier

                    .weight(1f , false)
            )

            OutlinedButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }

    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ConnectionCorridorPreview(){
    HoşbeşTheme {
        ConnectionCorridor(MainActivityVM())
    }
}