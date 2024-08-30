package com.batuscode.hosbes.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.RandomActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun RandomActivityView(randomActivityViewModel: RandomActivityViewModel){

    var scrolled by remember {
        mutableStateOf(false)
    }

    val Cscope = CoroutineScope(Dispatchers.Default)

    val offsetX = remember { mutableStateOf(100f) }
    val offsetY = remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }

    var  targetOffsetX by remember {
        mutableStateOf(120f)
    }

    var animation by remember {
        mutableStateOf(true)
    }

    val animatedoffset by animateFloatAsState(targetValue = targetOffsetX , animationSpec = tween(durationMillis = 2000))

    LaunchedEffect(Unit) {
        while (isActive){

            targetOffsetX = -350f

            delay(2000)
            targetOffsetX = 120f
            delay(2000)

        }
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()

    ){

        val (TopToolBar , imageBox) = createRefs()




        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(TopToolBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                }
        )
        {
            val closeButton = createRef()


            OutlinedIconButton(onClick = {
                randomActivityViewModel.update_closeActivity(true)
            } ,
                border = null ,
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(closeButton){
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_close_24), contentDescription = "")
            }

        }


        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .constrainAs(imageBox) {
                    top.linkTo(TopToolBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->

                        Timber
                            .tag("swipeRight")
                            .d(
                                dragAmount
                                    .roundToInt()
                                    .toString()
                            )
                        randomActivityViewModel.update_swiped(true)
/*
                        val originalX = offsetX.value
                        val newValue =
                            (originalX + dragAmount).coerceIn(-100.dp.toPx(), -50.dp.toPx())
                        offsetX.value = newValue

                        Timber
                            .tag("ofstX")
                            .d(offsetX.value.toString())*/
                    }
                }


        )
        {

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .wrapContentSize()
                    .offset { IntOffset(animatedoffset.roundToInt(), 0) }
                    .padding(end = 8.dp)
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.baseline_swipe_left_alt_24) ,
                    contentDescription = "" ,
                    modifier = Modifier
                        .wrapContentSize()
                        .size(80.dp)
                )


                Text(
                    text = "Ekranı sola kaydır" ,
                    style = TextStyle(
                        fontSize = 30.sp ,
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)) ,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .wrapContentSize()

                )

            }

        }


    }



    /*ConstraintLayout(

    ) {
        val (userInfo , bottomToolBar) = createRefs()

        if (scrolled){

            Text(
                text = "merhaba" ,
                modifier = Modifier

            )
        }

        Row (
            modifier = Modifier
                .constrainAs(userInfo){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                }

        ){

            Image(
                painter = painterResource(id = R.drawable.istockphoto_517188688_612x612),
                contentDescription = "",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(80.dp)
                    .height(80.dp),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = "merhaba" ,
                modifier = Modifier

            )
        }

        Row(
            modifier = Modifier
                .constrainAs(bottomToolBar){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            IconButton(onClick = { *//*TODO*//*
                Log.d("scrolright" , "mikrofon...")
            }) {
                Icon(painter = painterResource(id = R.drawable.mic_24px), contentDescription = "")
            }
            IconButton(onClick = { *//*TODO*//*
                Log.d("scrolright" , "kamera...")
            }) {
                Icon(painter = painterResource(id = R.drawable.videocam_48px), contentDescription = "")
            }
        }

    }*/



}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RandomActivityPreview(){
    HoşbeşTheme {
        RandomActivityView(RandomActivityViewModel())
    }
}
