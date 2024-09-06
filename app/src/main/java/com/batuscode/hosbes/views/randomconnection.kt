package com.batuscode.hosbes.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.RandomParticipant
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.RandomActivityViewModel
import com.batuscode.hosbes.utility.RandomConnectionActivityViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun RandomConnection(randomConnectionActivityViewModel: RandomConnectionActivityViewModel){

    val uid = MainActivity.PreferenceManager?.getuidShared("uid")
    val colors = listOf( colorResource(id = R.color.de) , colorResource(id = R.color.dr) , colorResource(
        id = R.color.dt
    ) , colorResource(id = R.color.dy))
    var color by remember {
        mutableStateOf(colors[0].copy(0.6f))
    }
    var animateBackgroundColor by remember {
        mutableStateOf(false)
    }

    val lifecycle = LocalLifecycleOwner.current

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "scale"
    )
    LaunchedEffect(Unit)
    {
        while(isActive){

            delay(800)
            color = colors[1].copy(0.6f)

            delay(800)

            color = colors[2].copy(0.6f)
            delay(800)

            color = colors[3].copy(0.6f)

            delay(800)

            color = colors[0].copy(0.6f)

            delay(800)
        }

    }
    val animatedColor by animateColorAsState(
        targetValue =  color,
        label = "color"
    )

    val handler = Handler()

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver{
            _ , event ->
            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    handler.postDelayed({
                        MainActivity.fm.matchParticipants(uid = uid!!)
                    },200)
                }
                Lifecycle.Event.ON_START -> {}
                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {}
                Lifecycle.Event.ON_DESTROY -> {
                    MainActivity.fm.removeRandomParticipant(uid!!)
                }
                Lifecycle.Event.ON_ANY -> {}
            }
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }


    val match by randomConnectionActivityViewModel.matched.collectAsState()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()


    ) {
        innerPadding ->

        Box (
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {

                    drawRect(color = animatedColor, style = Fill)
                }
                .padding(innerPadding)

        )
        {
            Text(
                text = "Senin için birileri var mı etrafa bakınıyorum." ,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                    textMotion = TextMotion.Animated
                ),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    }
                    .align(Alignment.Center)
            )
        }




    }
}

@Composable
fun back(){


}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RandomConnectionPreview(){
    HoşbeşTheme {
        back()
    }
}
