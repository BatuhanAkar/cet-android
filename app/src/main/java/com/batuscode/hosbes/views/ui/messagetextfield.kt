package com.batuscode.hosbes.views.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun MessageTextField( chatViewModel: ChatViewModel , mainActivityVM: MainActivityVM , modifier: Modifier = Modifier , newText: (String) -> Unit ){

    val message by mainActivityVM.message.collectAsState()

    val isEmpty: Boolean by derivedStateOf{ message?.text?.isEmpty() ?: true  }

    val messageSended by mainActivityVM.messageSended.collectAsState()
    val editMessageFlag by mainActivityVM.editMessageFlag.collectAsState()
    val editMessageMode by mainActivityVM.editMessageFieldMode.collectAsState()

    val messageItem by mainActivityVM.messageItem.collectAsState()

    if (editMessageFlag == true){
        mainActivityVM.updateMessage(TextFieldValue(messageItem?.message!!))
        mainActivityVM.updateEditMessageFlag(false)
        mainActivityVM.updateEditMessageFieldMode(true)
    }


    if (messageSended == true){

        mainActivityVM.updateMessage(TextFieldValue(""))
        mainActivityVM.updateMessageSended(false)

    }
    CustomTextField( chatViewModel , mainActivityVM , modifier ,message = message!!, isEmpty = isEmpty) {
        mainActivityVM.updateMessage(it)
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CustomTextField( chatViewModel: ChatViewModel , mainActivityVM: MainActivityVM , modifier: Modifier = Modifier , message: TextFieldValue , isEmpty: Boolean , onValueChange: (TextFieldValue) -> Unit){
    val context = LocalContext.current

    val channelId by mainActivityVM.channelId.collectAsState()

    val room by mainActivityVM.privateRoom.collectAsState()

    val user by mainActivityVM.user.collectAsState()

    val messageId by mainActivityVM.messageId.collectAsState()

    val prMessageWrited by mainActivityVM.prMessageWrited.collectAsState()

    val mediaUri by mainActivityVM.mediaUri.collectAsState()

    val editMessageFieldMode by mainActivityVM.editMessageFieldMode.collectAsState()

    val messageItem by mainActivityVM.messageItem.collectAsState()

    val _whisper by mainActivityVM.whisper.collectAsState()

    val whisperItem by mainActivityVM.whisperItem.collectAsState()


    var mediaSelected by remember {
        mutableStateOf(false)
    }

    if (prMessageWrited == true){
        Log.d("pickerResult" , "message writed flag on true run... " + mediaUri!!)


        MainActivity.fm.writePRMedia( mainActivityVM , room!! , context , "image" , mediaUri , messageId!!)

        mainActivityVM.updatePrMessageWrited(false)
    }


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {



        it.let {
            if (it != null) {


                Log.d("pickerResult" , "image is selected...")
                Log.d("pickerResult" , "path :: " + it)

                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(it!!)

                if (mimeType != null){

                    when{

                        mimeType.startsWith("image")-> {


                            Glide.with(context)
                                .asBitmap()
                                .load(it)
                                .into( object: CustomTarget<Bitmap>(){
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {

                                        Log.d("pickerResult" , "mime type :: image")
                                        mainActivityVM.updatePrivateChatPlaceHolderImage(resource.asImageBitmap())

                                        mainActivityVM.updateNewMediaSended(true)
                                        mainActivityVM.updateMediaUri(it)
                                        mediaSelected = true

                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        TODO("Not yet implemented")
                                    }

                                })



                        }

                        mimeType.startsWith("video")-> {

                            Glide.with(context)
                                .asBitmap()
                                .load(it)
                                .into( object: CustomTarget<Bitmap>(){
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        Log.d("pickerResult" , "mime type :: video")

                                        mainActivityVM.updatePrivateChatPlaceHolderImage(resource.asImageBitmap())

                                        mainActivityVM.updateNewMediaSended(true)
                                        mainActivityVM.updateMediaUri(it)
                                        mediaSelected = true

                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        TODO("Not yet implemented")
                                    }

                                })



/*
                            MainActivity.fm.writePRMedia( room!! , context , "video" , it.data)*/

                        }




                    }

                }







            }

        }

    }

    if (mediaSelected){

        Log.d("pickerResult" , "message now writing on database...")


        MainActivity.fm.writePRMediaMessage(mainActivityVM , "media" ,
            mediaUri!!.toString(), FirebaseManager.P1 , room!! )
        mediaSelected = false
    }


    ConstraintLayout (
        modifier = modifier
    ){

        val (mMessage , mButton) = createRefs()

        Surface (
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(mMessage) {
                    start.linkTo(parent.start)
                    end.linkTo(mButton.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            shape = RoundedCornerShape(24.dp) ,
            color = colorResource(id = R.color.white) ,
            shadowElevation = 2.dp ,
            border = BorderStroke(1.dp , colorResource(id = R.color.e))
        )
        {
            Box(modifier = Modifier
                .background(colorResource(id = R.color.white).copy(0.1f))
                .heightIn(min = 24.dp) ,
                contentAlignment = Alignment.CenterStart
            )
            {
                BasicTextField(
                    modifier = Modifier
                        .background(colorResource(id = R.color.white).copy(0.1f))
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 10.dp) ,
                    textStyle = TextStyle(
                        fontSize = 18.sp
                    ),
                    value = message ,
                    onValueChange = onValueChange ,
                    decorationBox = { textField ->
                        if (isEmpty){
                            Text(
                                text = stringResource(id = R.string.message)

                            )
                        }
                        textField()
                    }

                )
            }
        }


        if (editMessageFieldMode == false) {

            //
            //                            AnimatedVisibility(visible = isEmpty) {
            //
            //                                OutlinedIconButton(
            //                                    onClick = {
            //
            //
            //
            //
            //                                    } ,
            //                                    modifier = Modifier
            //                                        .padding(0.dp) ,
            //                                    border = null
            //                                ) {
            //                                    Icon(painter = painterResource(id = R.drawable.gif_box_24px), contentDescription = ""
            //                                    )
            //                                }
            //                            }


            // standart mesaj kutusu

            Surface(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .constrainAs(mButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(mMessage.bottom)
                    }
                ,
                shape = RoundedCornerShape(50.dp),
                color = colorResource(id = R.color.d) ,
                shadowElevation = 2.dp ,
            ){
                FilledIconButton(
                    onClick = {
                        if (!message.text.isEmpty()){
                            Log.d("sendbutton :: " , message.text)


                            if (channelId == "C1"){
                                mainActivityVM.updateMessageSended(true)
                                MainActivity.fm.writeMessage( "text" , message.text , FirebaseManager.C1)

                            } else if (channelId == "C2") {
                                mainActivityVM.updateMessageSended(true)
                                MainActivity.fm.writeMessage( "text" , message.text , FirebaseManager.C2)

                            } else if (channelId == "P1"){
                                mainActivityVM.updateMessageSended(true)
                                MainActivity.fm.writePRMessage( mainActivityVM , "text" , message.text , FirebaseManager.P1 , room = room!!)
                            } else if (channelId == "W"){
                                mainActivityVM.updateMessageSended(true)

                                if (_whisper == true){
                                    Log.d("whisperchat" , "message sended ...")
                                    mainActivityVM.update_whisper(false)
                                    // ilk defa mesaj yollandı bayrağını true ayarla .... mesajı yolla ...
                                    MainActivity.fm.writeWhisperMessage(user = user!! , "text" , message.text , mainActivityVM)
                                } else {
                                    MainActivity.fm.writeWMessage( whisperItem?.wuid!! , whisperItem?.wid!! , "text" , message.text)
                                }


                            }

                        } else {
                            Log.d("sendbutton :: " , "12")

                        }
                    } ,
                    modifier = Modifier
                        .padding(0.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colorResource(id = R.color.d)
                    ),
                    shape = RoundedCornerShape(50.dp)

                ) {
                    Icon(
                        imageVector = Icons.Filled.Send ,
                        contentDescription = "send" ,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(start = 3.dp)
                    )
                }
            }





        }
        else {

            // mesajı düzenle mesaj kutusu

            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                TextButton(
                    onClick = {
                        if (!message.text.isEmpty()){
                            Log.d("sendbutton :: " , message.text)


                            if (channelId == "C1"){
                                mainActivityVM.updateMessageSended(true)
                                mainActivityVM.updateEditMessageFieldMode(false)

                                MainActivity.fm.editMessage( "text", message.text , messageItem!! , FirebaseManager.C1)

                            } else if (channelId == "C2") {
                                mainActivityVM.updateMessageSended(true)
                                mainActivityVM.updateEditMessageFieldMode(false)

                                MainActivity.fm.editMessage( "text" , message.text , messageItem!! , FirebaseManager.C2)

                            } else if (channelId == "P1"){
                                mainActivityVM.updateMessageSended(true)
                                mainActivityVM.updateEditMessageFieldMode(false)

                                MainActivity.fm.editPrMessage( "text" , messageItem!! , message.text , FirebaseManager.P1 , room = room!!)
                            } else if (channelId == "W"){
                                mainActivityVM.updateMessageSended(true)
                                mainActivityVM.updateEditMessageFieldMode(false)

                                if (_whisper == true){
                                    Log.d("whisperchat" , "message sended ...")
                                    mainActivityVM.update_whisper(false)
                                    // ilk defa mesaj yollandı bayrağını true ayarla .... mesajı yolla ...
                                    MainActivity.fm.writeWhisperMessage(user = user!! , "text" , message.text , mainActivityVM)
                                } else {
                                    MainActivity.fm.editWMessage( whisperItem!! , message.text , FirebaseManager.W_C,messageItem!! , "text")
                                }

                            }

                        } else {
                            Log.d("sendbutton :: " , "12")

                        }
                    } ,
                    modifier = Modifier
                        .padding(0.dp) ,
                    border = null
                ) {
                    Text(text = stringResource(id = R.string.editSend))
                }

                OutlinedIconButton(
                    onClick = {
                        mainActivityVM.updateEditMessageFlag(false)
                        mainActivityVM.updateEditMessageFieldMode(false)
                        mainActivityVM.updateMessage(TextFieldValue(""))
                    } ,
                    modifier = Modifier
                        .padding(0.dp),
                    border = null ,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close ,
                        contentDescription = "send" ,
                        modifier = Modifier

                        )
                }
            }
        }
    }




}

private fun checkPhotoPermission(): Boolean {


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        return Environment.isExternalStorageManager()
    } else {
        return false
    }

   /* val activity = MainActivity.context as? Activity
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                MainActivity.context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            MainActivity.permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

            return false
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) {
            return true
        } else {
            MainActivity.permissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            return false
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                MainActivity.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            if (activity != null) {
                requestPermissions(activity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 23)
            }


            Log.w("permissionss", "denied...")

            return false
        }
    }*/
}
private fun showEmoji(context:Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED , 0)
}



@Preview
@Composable
fun MessageTextFieldPreview(){
    HoşbeşTheme {
        val mainActivityVM:MainActivityVM = viewModel()
        val chatViewModel:ChatViewModel = viewModel()
        MessageTextField( chatViewModel , mainActivityVM) {

        }
    }
}
