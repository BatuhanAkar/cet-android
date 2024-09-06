package com.batuscode.hosbes.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.delay

@Composable
fun ShowMeetInfo(mainActivityVM: MainActivityVM){
    val context = LocalContext.current

    val randomParticipant by mainActivityVM.randomParticipant.collectAsState()

    var rImage by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    /**
     * karşılaşma bulundu ise ...
     * */



    GlideApp
        .with(context)
        .asBitmap()
        .load(randomParticipant?.photoUrl)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                rImage = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }


        })


    Scaffold {
        innerPadding ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
        {

            val (background , info ) = createRefs()


            if (rImage != null){

                Image(
                    bitmap = rImage!! ,
                    contentDescription = "" ,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(
                            radiusX = 7.dp,
                            radiusY = 7.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .constrainAs(background) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

            }




            ConstraintLayout(
                modifier = Modifier
                    .constrainAs(info){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                val (pp , nameSurface , timer) = createRefs()


                if (rImage != null) {

                    Image(
                        bitmap = rImage!! ,
                        contentDescription = "" ,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp)
                            .width(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .constrainAs(pp) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                }




                Surface(
                    color = colorResource(id = R.color.e).copy(0.5f) ,
                    shadowElevation = 20.dp ,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .constrainAs(nameSurface) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(pp.bottom)
                        }
                )
                {

                    if (randomParticipant?.displayName != null){
                        Text(
                            text = randomParticipant?.displayName!! ,
                            textAlign = TextAlign.Center ,
                            style = TextStyle(
                                fontSize = 20.sp ,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }

                }

                Text(
                    text = com.batuscode.hosbes.views.timer(startValue = 10).toString() ,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold ,
                        fontSize = 30.sp
                    ) ,
                    modifier = Modifier
                        .wrapContentSize()
                        .constrainAs(timer){
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(nameSurface.bottom)

                        }
                )
            }





        }

    }


}

@Composable
fun timer(
    startValue: Int
):Int {
    var currentValue by remember {
        mutableStateOf(startValue)
    }

    LaunchedEffect(Unit) {
        while (currentValue > 0){
            delay(1000L)
            currentValue -= 1
        }
    }

    return currentValue
}
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ShowMeetInfoPreview(){
    HoşbeşTheme {
        ShowMeetInfo(MainActivityVM())
    }
}