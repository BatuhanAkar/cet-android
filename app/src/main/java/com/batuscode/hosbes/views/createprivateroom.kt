package com.batuscode.hosbes.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.views.ui.Progress
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.UUID


fun scheckPermisson() : Boolean{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        return Environment.isExternalStorageManager()
    } else {
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePrivateRoom(mainActivityVM: MainActivityVM){

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { /*TODO*/ mainActivityVM.updateShowCreatePrivateRoom(false) } ,
        sheetState = sheetState ,
        modifier = Modifier.fillMaxSize() ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,


        ) {

        CreatePrivateRoomContent(mainActivityVM)

    }
}

@Composable
fun CreatePrivateRoomContent(mainActivityVM: MainActivityVM){

    var context = LocalContext.current

    var roomName by remember {
        mutableStateOf(String())
    }

    var sliderState by remember {
        mutableStateOf(0f)
    }

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }

    var roomId by remember {
        mutableStateOf(String())
    }

    var createButton by remember {
        mutableStateOf(true)
    }

    var newRoomBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var defRoomBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var defPhotoReady by remember {
        mutableStateOf(false)
    }

    val createRoom by mainActivityVM.createPrivateRoom.collectAsState()

    val writePrivateRoom by mainActivityVM.uploadComplated.collectAsState()

    val creatingPrivateRoom by mainActivityVM.creatingPrivateRoom.collectAsState()
    val privateRoomPhotoUrl by mainActivityVM.photoUrl.collectAsState()


    val newPrivateRoomImage by mainActivityVM.newPrivateRoomImage.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        it.data.let {
            if (it != null) {

                Log.d("pickerResult" , "path :: " + it.data)

                Glide.with(context)
                    .asBitmap()
                    .load(it.data)
                    .into(object: CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            var bitmap = resource.asImageBitmap()
                            newRoomBitmap = resource
                            mainActivityVM.updateNewPrivateRoomImage(bitmap)

/*
                            mainActivityVM.updatenewPhoto(resource)
                            newPP = bitmap
                            changeImage = true*/

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }


                    })

            }

        }

    }


    Scaffold (
        modifier = Modifier
            .fillMaxSize()
    ){

        if (showPermissionDialog){

            AlertDialog(
                onDismissRequest = {
                    showPermissionDialog = false
                } ,
                title = {
                    DialogContent()
                } ,
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = Intent(
                                android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            val uri: Uri = Uri.fromParts("package" , context.packageName , null)
                            intent.setData(uri)
                            launcher.launch(intent)

                        } ,
                    )
                    {
                        Text(text = stringResource(id = R.string.allow))
                    }


                } ,
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPermissionDialog = false
                        } ,
                    )
                    {
                        Text(text = stringResource(id = R.string.deny))
                    }
                } ,
                properties = DialogProperties(
                    decorFitsSystemWindows = true ,
                    usePlatformDefaultWidth = true ,
                    dismissOnClickOutside = true
                )
            )

        }

        if (creatingPrivateRoom){
            createButton = false

            Progress()


        }
        else {
            createButton = true
        }

        if (defPhotoReady){

            MainActivity.fm.uploadPrivateRoomPhoto(bitmap = defRoomBitmap, mainActivityVM = mainActivityVM, uid = roomId)

            defPhotoReady = false

        }

        if (createRoom == true){


            if (newRoomBitmap == null){

                Glide.with(context)
                    .asBitmap()
                    .load(R.drawable.image_gallery)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            defRoomBitmap = resource

                            defPhotoReady = true
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }


                    })
                mainActivityVM.updateCreateRoom(false)

            } else {
                MainActivity.fm.uploadPrivateRoomPhoto(bitmap = newRoomBitmap, mainActivityVM = mainActivityVM, uid = roomId)
                mainActivityVM.updateCreateRoom(false)

            }


        }

        // TODO: özel odayı veri tabanına yaz ...
        if (writePrivateRoom == true){

            val parCount = sliderState.toLong()

            val ownerId = MainActivity.PreferenceManager?.getuidShared("uid")
            MainActivity.fm.writePrivateRoom(
                roomName = roomName!!,
                roomId = roomId,
                photoUrl = privateRoomPhotoUrl,
                parCount = parCount,
                mainActivityVM = mainActivityVM ,
                ownerId = ownerId
            )

            mainActivityVM.uploadComlated(false)

        }

        Column (
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ){


            ElevatedCard(
                onClick = { /*TODO*/ } ,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .wrapContentWidth()
                    .padding(8.5.dp)
            ) {

                ConstraintLayout (
                    modifier = Modifier
                        .wrapContentWidth()
                ) {

                    val (header , roomImage) = createRefs()


                    if (newPrivateRoomImage != null){
                        Image(
                            bitmap = newPrivateRoomImage!!,
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
                            .background(Color.Gray.copy(alpha = 0.5f))
                            .constrainAs(header) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                height = Dimension.fillToConstraints
                            }
                    ){

                        Image(
                            painter = painterResource(id = R.drawable.add_photo_alternate_24px) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .padding(5.5.dp)
                                .align(Alignment.TopEnd)
                        )
                    }


                }

                Text(
                    text = roomName ,
                    style = TextStyle(
                        fontWeight = FontWeight.W700 ,
                        fontSize = 16.sp
                    ) ,
                    modifier = Modifier
                        .width(180.dp)
                        .padding(5.5.dp)
                )

            }

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    if (scheckPermisson()){


                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        launcher.launch(intent)

                    } else {

                        showPermissionDialog = true


                    }
                }) {
                    Text(text = stringResource(id = R.string.setprivateroomimage))
                }

                TextField(
                    value = roomName ,
                    onValueChange = {
                                    roomName = it
                    } ,
                    label = {
                        Text(text = stringResource(id = R.string.privateroomname))
                    } ,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent ,
                        focusedIndicatorColor = Color.Transparent
                    ) ,
                    modifier = Modifier
                        .background(Color.Gray.copy(0.5f))
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (sliderState.toInt() != 0){

                    Text(
                        text = "${sliderState.toInt()} " + stringResource(id = R.string.people) ,
                        modifier = Modifier
                            .padding(5.5.dp)
                    )
                }

                Slider(
                    value = sliderState ,
                    onValueChange = {
                    sliderState = it
                } ,
                    valueRange = 0f..20f,
                    steps = 19 ,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White ,
                        activeTrackColor = Color.LightGray ,
                        inactiveTickColor = Color.Black ,
                    ) ,
                    modifier = Modifier
                        .padding(15.dp)
                )
            }

            // TODO: özel oda oluştur butonu ...
            Button(
                onClick = {

                          roomId =  UUID.randomUUID().toString()
                    mainActivityVM.updateCreateRoom(true)
                    mainActivityVM.updateCreatingPrivateRoom(true)

                } ,
                enabled = createButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.5.dp)
            ) {
                Text(text = stringResource(id = R.string.createprivateroom))
            }


        }

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun CreatePrivateRoomContentPreview(){

    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        CreatePrivateRoomContent(mainActivityVM)
    }
}