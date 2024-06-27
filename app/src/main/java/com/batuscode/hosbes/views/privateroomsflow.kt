package com.batuscode.hosbes.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.PrivateRoomsViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun PrivateRoomsFlow( mainActivityVM: MainActivityVM , privateRoomsViewModel: PrivateRoomsViewModel , paddingValues: PaddingValues){

    Log.d("firedb" , "flow...")

    val context = LocalContext.current

    val rooms = privateRoomsViewModel.rooms.collectAsState()

    val chunkedRooms = rooms.value.chunked(2)

    LazyColumn (
        modifier = Modifier
            .padding(paddingValues)
    ){

        chunkedRooms.forEachIndexed { index, privateRooms ->

            items(privateRooms , key = {it.roomId!!}) { room ->

                Room(room , mainActivityVM = mainActivityVM)



            }
        }
    }
}


@Composable
fun Room(room: PrivateRoom , mainActivityVM: MainActivityVM){

    val context: Context = LocalContext.current

    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    ElevatedCard(
        onClick = {


            if (room.activePar < room.parCount){
                MainActivity.fm.handleJoinRoom( mainActivityVM = mainActivityVM ,"joined" , room)
                mainActivityVM.updatePrivateRoom(room)
                MainActivity.navigate?.navigate("privateroomchat")

            }

        } ,
        modifier = Modifier
            .wrapContentWidth()
            .padding(8.5.dp)

    ) {

        ConstraintLayout (
            modifier = Modifier
                .wrapContentWidth()

        ) {

            val (header , roomImage) = createRefs()


            if (room.photoUrl != null) {

                GlideApp.with(context)
                    .asBitmap()
                    .load(room.photoUrl)
                    .into( object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            image = resource.asImageBitmap()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }


                    })

            }

            if (image != null){


                Image(
                    bitmap = image!!,
                    contentDescription = "" ,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .constrainAs(roomImage) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        }
                        .width(180.dp)
                        .height(100.dp)
                )


            } else {



                Image(
                    painter = painterResource(id = R.drawable.image_gallery),
                    contentDescription = "" ,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .constrainAs(roomImage) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        }
                        .width(180.dp)
                        .height(100.dp)
                )

            }



            Box (
                modifier = Modifier
                    .width(180.dp)
                    .clip(RectangleShape)
                    .background(Color.Gray.copy(alpha = 0.9f))
                    .constrainAs(header) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        height = Dimension.fillToConstraints
                    }
            ){


                Text(
                    text = if (room.activePar == room.parCount) stringResource(id = R.string.full) + " ${room.activePar}/${room.parCount}" else "${room.activePar}/${room.parCount}" ,
                    style = TextStyle(
                        color = if (room.activePar == room.parCount) Color.Red else Color.Black ,
                        fontSize = 16.sp
                    ) ,
                    modifier = Modifier
                        .padding(5.5.dp)
                        .align(Alignment.TopStart)
                )

                /*
                Image(
                    imageVector = Icons.Filled.MoreVert ,
                    contentDescription = "" ,
                    modifier = Modifier
                        .padding(5.5.dp)
                        .align(Alignment.TopEnd)
                )*/
            }


        }

        Text(
            text = room.roomName!! ,
            style = TextStyle(
                fontWeight = FontWeight.W700 ,
                fontSize = 16.sp
            ) ,
            modifier = Modifier
                .padding(5.5.dp)
        )

    }

}
/*
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun RoomPreview(){
    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        Room(room = PrivateRoom() , mainActivityVM)
    }
}*/

/*

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun PrivateRoomsFlowPreview(){
    val privateRoomsViewModel:PrivateRoomsViewModel = viewModel()
    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        PrivateRoomsFlow(paddingValues = PaddingValues(1.dp) , privateRoomsViewModel = privateRoomsViewModel , mainActivityVM = mainActivityVM)
    }
}

*/
