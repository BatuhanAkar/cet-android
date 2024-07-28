package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.ParticipantsViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun ParticipantsFlow(participantsViewModel: ParticipantsViewModel){
    val context = LocalContext.current
    val participants = participantsViewModel.participnats.collectAsState()
    val chunkedParticipnat = participants.value.chunked(5)
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle){
        val observe = LifecycleEventObserver{ _ , event ->

            when(event){
                Lifecycle.Event.ON_CREATE -> {

                }
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {}
                Lifecycle.Event.ON_DESTROY -> {}
                Lifecycle.Event.ON_ANY -> {}
            }

        }

        lifecycle.lifecycle.addObserver(observe)

        onDispose {
            lifecycle.lifecycle.removeObserver(observe)
        }
    }

    LazyColumn{

        chunkedParticipnat.forEachIndexed{ index, participants ->
            item(key = index){


                Row ( horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ){

                    participants.forEach{ participant ->


                        ParticipantsView(participnat = participant , context)


                    }

                }
            }
        }
    }
}

@Composable
fun ParticipantsView(participnat: Participnat , context:Context){

    var image by remember{
        mutableStateOf<ImageBitmap?>(null)
    }

    GlideApp.with(context)
        .asBitmap()
        .load(participnat.photoUrl)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                image = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                TODO("Not yet implemented")
            }


        })



    Row {
        Column {
            if (image != null){


                Image(
                    bitmap = image!!,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .width(60.dp)
                        .height(60.dp),
                    contentScale = ContentScale.FillBounds
                )

            } else {

                Image(
                    painter = painterResource(id = R.drawable.istockphoto_517188688_612x612),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .width(60.dp)
                        .height(60.dp),
                    contentScale = ContentScale.FillBounds
                )

            }

            Text(text = "Batuhan")
        }
    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ParticipnatPreview(){
    HoşbeşTheme {
        ParticipantsView(participnat = Participnat() , LocalContext.current)
    }
}