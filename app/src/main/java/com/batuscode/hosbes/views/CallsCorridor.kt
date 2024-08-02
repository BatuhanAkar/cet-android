//package com.batuscode.hosbes.views
//
//import android.content.Context
//import android.content.Intent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.IconButtonColors
//import androidx.compose.material3.OutlinedIconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.batuscode.hosbes.MainActivity
//import com.batuscode.hosbes.R
//import com.batuscode.hosbes.ui.theme.HoşbeşTheme
//
//
//fun declineCall(context: Context){
//    val intent = Intent(context , MainActivity::class.java)
//    context.startActivity(intent)
//}
//@Composable
//fun CallsCorridor(){
//    Scaffold {innerPadding ->
//        Column (
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally ,
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//        ) {
//
//            Column (
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .padding(top = 100.dp)
//                    .wrapContentSize()
//            ){
//                Image(
//                    painter = painterResource(id = R.drawable.istockphoto_517188688_612x612),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .padding(bottom = 20.dp)
//                        .clip(RoundedCornerShape(10.dp))
//                        .width(80.dp)
//                        .height(80.dp),
//                    contentScale = ContentScale.FillBounds
//                )
//
//
//                Text(
//                    text = "Batuhan"
//                )
//
//                Text(text = "Aranıyor...")
//            }
//
//
//
//            IconButton(onClick = {
//                declineCall(VoiceCalls.VoiceCallsContext)
//            } ,
//                modifier = Modifier
//                ) {
//                Icon(painter = painterResource(id = R.drawable.call_end_24px), contentDescription = "")
//            }
//        }
//    }
//
//}
//
//@Preview(showBackground = true , showSystemUi = true)
//@Composable
//fun CallsCorridorPreview(){
//    HoşbeşTheme {
//        CallsCorridor()
//    }
//}