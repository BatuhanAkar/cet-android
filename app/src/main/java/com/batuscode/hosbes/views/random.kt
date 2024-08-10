package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

/**
 * random'a girildiğinde ve rastgele butonuna bastığında bağlantı koridoruna it ...
 * */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Random(mainActivityVM: MainActivityVM){

    val context = LocalContext.current

    /**
     * Olaki karşıda karşılaşma isteği başlatan biri düşerse buraya outId'den RandomParticipant getirebilmektir ...
     *
     * Bunun içinde esas olan şey ... kendi random bilgilerini dinleyip karşılaşma oldumu bakmak ... çünkü karşının güncelleyeceği şeylerden ilki karşılaşma durumu ...
     * */

    val matched by mainActivityVM.matched.collectAsState() // karşılaşma durumu ana aktiviteden gelir ... sebebi karşılaşma isteği başlatan kişiyi biri yakalarsa işleyelim diye ...
    val outId by mainActivityVM.randomParticipantUid.collectAsState() // kişinin kendi random dinlemesi sonucu gelen outId ...

    /**
     * Bunlar tamamen kişinin kendi bilgileri ...
     * */

    val uid = MainActivity.PreferenceManager?.getuidShared("uid")
    val displayName = MainActivity.PreferenceManager?.getString("displayName")
    val photoUrl = MainActivity.PreferenceManager?.getString("photoUrl")

    /**
     *
     * RandomParticipant belli olan işlemlerin hepsi karşılaşmayı başlatan kişi birini yakalarda düşer diye ...
     *
     * */
    val randomParticipant by mainActivityVM.randomParticipant.collectAsState()

    val Ruid = randomParticipant?.uid
    val Rname = randomParticipant?.displayName
    val RphotoUrl = randomParticipant?.photoUrl

    val scope = CoroutineScope(Dispatchers.Default)
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver{
                _,event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    MainActivity.fm.addRandomParticipant(uid = uid!! , displayName = displayName!! , photoUrl = photoUrl!!) // önce random a kaydet ...
                    MainActivity.fm.ListenMatch(uid!! , mainActivityVM) // sonra random'ı dinle ...




                }
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {
                    scope.cancel()
                    if (matched == false){
                        MainActivity.fm.removeRandomParticipant(uid = uid!!)
                        MainActivity.fm.detachListenMatch()
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    scope.cancel()
                    if (matched == false){
                        MainActivity.fm.removeRandomParticipant(uid = uid!!)
                        MainActivity.fm.detachListenMatch()
                    }
                }
                Lifecycle.Event.ON_ANY -> {}
            }
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = matched) {
        when(matched){
            true -> {
                mainActivityVM.updateliveRandomParticipant(randomParticipant!!)
                MainActivity.navigate?.navigate("matchconnectcorridor")
            }
            false -> {
                if (outId?.isNotEmpty() == true){
                    /**
                     * kişi kendi yakalamadan yakalandı ve karşılaşma true RandomParticipant bilgilerini çek ... neyle ??? dinlediğimiz veriyle ... outId ile ...
                     * */
                    /**
                     * kişi kendi yakalamadan yakalandı ve karşılaşma true RandomParticipant bilgilerini çek ... neyle ??? dinlediğimiz veriyle ... outId ile ...
                     * */
                    MainActivity.fm.getRandomParticipant(outId!! , mainActivityVM)

                    if (randomParticipant != null){

                        mainActivityVM.updateliveRandomParticipant(randomParticipant!!)

                        MainActivity.navigate?.navigate("matchconnectcorridor")

                    }
                }
            }
            null -> {}
        }
    }




    Scaffold( modifier = Modifier.fillMaxSize() ,
        topBar = {
            TopAppBar(
                title = {

                    Text(text = stringResource(id = R.string.random))

                } ,
                navigationIcon = {

                    Icon(
                        imageVector = Icons.Filled.ArrowBack ,
                        contentDescription = "" ,
                        modifier = Modifier
                            .clickable {
                                MainActivity.navigate?.popBackStack()
                            }
                    )
                }
            )
        }
    ) {
        innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally ,
            verticalArrangement = Arrangement.Center ,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Text(
                text = stringResource(id = R.string.formatchclickbutton) ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    fontSize = 40.sp ,
                    textAlign = TextAlign.Center
                ) ,
                modifier = Modifier
                    .weight(1f , false)
            )

            Box(
                modifier = Modifier
                    .width(210.dp)
                    .height(210.dp) ,
                contentAlignment = Alignment.Center

            ) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp ,
                    modifier = Modifier
                        .width(205.dp)
                        .height(205.dp)
                )

                //TODO: eşleş butonu ...
                OutlinedButton(onClick = {
                    MainActivity.fm.updateMatchRequest(true , uid = uid!!) // karşılaşma isteği olduğunu belirt ...
                    MainActivity.fm.matchParticipants(uid = uid , mainActivityVM = mainActivityVM) // karşılaştır bakalım ...
                }  ,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(200.dp)
                        .height(200.dp)
                )
                {
                    Text(
                        text = stringResource(id = R.string.match) ,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                            fontSize = 40.sp
                        )
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RandomPreview(){
    HoşbeşTheme {
        Random(MainActivityVM())
    }
}