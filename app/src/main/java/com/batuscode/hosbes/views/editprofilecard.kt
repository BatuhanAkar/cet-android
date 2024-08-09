package com.batuscode.hosbes.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.R.color.blue
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.MainActivityVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.AccessController.getContext


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileCard(mainActivityVM: MainActivityVM){
    val profileUpdating by mainActivityVM.profileUpdating.collectAsState()

    val sheetState = rememberModalBottomSheetState(confirmValueChange = {
        if (profileUpdating == true) it != SheetValue.Hidden else true} )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { /*TODO*/ mainActivityVM.updateShowEditProfileCard(false) } ,
        sheetState = sheetState ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,


        ) {

        Content( mainActivityVM)

    }


}



@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Content(mainActivityVM: MainActivityVM){
    val profileUpdating by mainActivityVM.profileUpdating.collectAsState()

    var context = LocalContext.current

    val scope = rememberCoroutineScope()

    val showPermissionDialog by mainActivityVM.showPermissionDialog.collectAsState()

    var changeImage by remember {
        mutableStateOf(false)
    }

    val currentPP by mainActivityVM.photo.collectAsState()

    var newPP by remember {
        mutableStateOf<ImageBitmap?>(null)
    }



    val displayName by mainActivityVM.displayName.collectAsState()




    var newUsername by remember {
        mutableStateOf(String())
    }

    var run by remember {
        mutableStateOf(false)
    }

    var updateButtonEnable by remember {
        mutableStateOf(false)
    }

    var dot1offset by remember{ mutableStateOf(0.dp) }
    var dot2offset by remember{ mutableStateOf(0.dp) }
    var dot3offset by remember{ mutableStateOf(0.dp) }

    LaunchedEffect(profileUpdating == true) {
        dot1offset = 0.dp
        delay(100)
        dot1offset = -5.dp

        delay(100)
        dot1offset = 0.dp
        dot2offset = -5.dp

        delay(100)
        dot2offset = 0.dp
        dot3offset = -5.dp

        delay(100)
        dot3offset = 0.dp
    }

    if (!newUsername.isEmpty()){
        updateButtonEnable = true
    } else if (newPP != null){
        updateButtonEnable = true
    }


    val uploadComplated by mainActivityVM.uploadComplated.collectAsState()
    val photoUrl by mainActivityVM.photoUrl.collectAsState()

    val startUpdate by mainActivityVM.startUpdate.collectAsState()

    // if upload complated new user photo to storage update on database usr pohot url

    if (startUpdate == true){


        if (newPP != null && newUsername.isNotEmpty()){
            MainActivity.fm.handleUpdateProfileCard(bitmap = newPP?.asAndroidBitmap(), mainActivityVM = mainActivityVM)
            MainActivity.fm.updateDisplayName(newUsername , mainActivityVM)

        } else if (newPP != null){
            MainActivity.fm.handleUpdateProfileCard(bitmap = newPP?.asAndroidBitmap(), mainActivityVM = mainActivityVM)

        } else if (newUsername.isNotEmpty()){
            MainActivity.fm.updateDisplayName(newUsername , mainActivityVM)
        }

    }

    if (uploadComplated == true){

        MainActivity.fm.updateOnDbUserPhotoUrl(photoUrl = photoUrl , mainActivityVM)

    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        it.data.let {
            if (it != null) {

                Log.d("pickerResult" , "path :: " + it.data)

                Glide.with(context)
                    .asBitmap()
                    .load(it.data)
                    .into(object:CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            var bitmap = resource.asImageBitmap()

                            mainActivityVM.updatenewPhoto(resource)
                            newPP = bitmap
                            changeImage = true

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }


                    })

            }

        }

    }

    if (showPermissionDialog == true){

        AlertDialog(
            onDismissRequest = {
                mainActivityVM.updateShowPermissionDialog(false)
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
                        val uri:Uri = Uri.fromParts("package" , context.packageName , null)
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
                              mainActivityVM.updateShowPermissionDialog(false)
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

    ConstraintLayout (
        modifier = Modifier
            .padding(8.5.dp)
    ) {

        val (updateButton , profileCard , progress) = createRefs()

        if (profileUpdating == false){
            updateButtonEnable = true
        }

        // profile card layout ...

        ConstraintLayout (
            modifier = Modifier
                .constrainAs(profileCard)
                {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ) {
            val (imageBox , selectButton , usernameTextfield) = createRefs()

            Box (
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .width(80.dp)
                    .height(80.dp)
                    .constrainAs(imageBox)
                    {
                        start.linkTo(usernameTextfield.start)
                        end.linkTo(usernameTextfield.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(usernameTextfield.top)
                    }
            ) {
                Image(
                    bitmap = if (changeImage) newPP!!  else currentPP!!,
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(80.dp)
                        .height(80.dp)
                    ,
                    contentScale = ContentScale.FillBounds
                    )


            }

            OutlinedIconButton(
                onClick = {

                    if (checkPermisson()){


                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        launcher.launch(intent)

                    } else {

                        mainActivityVM.updateShowPermissionDialog(true)


                    }


                } ,
                border = null ,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = colorResource(id = R.color.blue)
                ),
                modifier = Modifier
                    .absoluteOffset(y = 10.dp, x = 5.dp)
                    .padding(0.dp)
                    .constrainAs(selectButton) {
                        end.linkTo(imageBox.end)
                        bottom.linkTo(imageBox.bottom)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.change_circle_24px) ,
                    contentDescription = "" ,
                )
            }


            TextField(

                value = if (newUsername.isEmpty() && !run) displayName!! else newUsername
                ,
                onValueChange = {newText ->
                    if (newText.length == 0){
                        run = true
                    }
                    newUsername = newText
                } ,
                label = { Text(text = stringResource(id = R.string.kullaniciadi))},
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent ,
                    unfocusedIndicatorColor = Color.Transparent ,
                    containerColor = Color.DarkGray.copy(0.1f)
                ) ,

                modifier = Modifier
                    .padding(top = 5.dp)
                    .constrainAs(usernameTextfield)
                    {
                        end.linkTo(parent.end)
                        top.linkTo(imageBox.bottom)
                        bottom.linkTo(updateButton.top)
                        start.linkTo(parent.start)

                    }
            )
        }


        // update button

        Button(
            onClick = {
                      mainActivityVM.updateStartUpdate(true)
                      mainActivityVM.updateProfileUpdating(true)
                      updateButtonEnable = false
                      } ,
            enabled = updateButtonEnable,
            modifier = Modifier
                .padding(top = 8.5.dp, bottom = 8.5.dp)
                .fillMaxWidth()
                .constrainAs(updateButton)
                {
                    top.linkTo(profileCard.bottom)
                    start.linkTo(profileCard.start)
                    end.linkTo(profileCard.end)
                    height = Dimension.fillToConstraints
                }
        )
        {
            if (profileUpdating == false){
                Text(text =  stringResource(id = R.string.update))
            } else{
                Row {
                    Text(text = stringResource(id = R.string.updating))
                    Text(text = "." , modifier = Modifier
                        .padding(start = 4.dp)
                        .offset(y = dot1offset))
                    Text(text = "." , modifier = Modifier
                                .padding(start = 4.dp)
                                .offset(y = dot2offset))
                    Text(text = "." , modifier = Modifier
                                .padding(start = 4.dp)
                                .offset(y = dot3offset))
                }
            }

        }

    }


}


fun checkPermisson() : Boolean{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        return Environment.isExternalStorageManager()
    } else {
        return false
    }
}

@Composable
fun DialogContent(){

    Column ( horizontalAlignment = Alignment.CenterHorizontally ,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.perm_media_24px) ,
            contentDescription = "" ,
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
        )

        Text(
            text = stringResource(id = R.string.permissiontext) ,
            style = TextStyle(
                fontWeight = FontWeight.Bold ,
                fontSize = 18.sp ,
                textAlign = TextAlign.Center
            )
        )


    }

}

/*
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun DialogContentPreview(){
    HoşbeşTheme {
        DialogContent()
    }
}*//*
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun PermissionDialogPreview(){
    HoşbeşTheme {
        PermissionDialog()
    }
}
*/
@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ContentPreview(){
    val mainActivityVM:MainActivityVM = viewModel()
    HoşbeşTheme {
        Content(mainActivityVM)
    }
}
