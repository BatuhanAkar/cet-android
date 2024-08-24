package com.batuscode.hosbes.views

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.R
import com.batuscode.hosbes.utility.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun More(mainActivityVM: MainActivityVM){


    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { /*TODO*/ mainActivityVM.updateShowMore(false) } ,
        sheetState = sheetState ,
        modifier = Modifier.fillMaxSize() ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,
        containerColor = Color.White


    ) {

        MoreContent(scope , sheetState , mainActivityVM , context)

    }


}



@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreContent(scope: CoroutineScope , sheetState: SheetState , mainActivityVM: MainActivityVM , context: Context){
    val displayName by mainActivityVM.displayName.collectAsState()


    val imageBitmap by mainActivityVM.photo.collectAsState()


    val showEditProfileCard by mainActivityVM.showEditProfileCard.collectAsState()

    if (showEditProfileCard == true){
        EditProfileCard(mainActivityVM = mainActivityVM)
    }


    ConstraintLayout (modifier = Modifier
        .fillMaxSize()
        .padding(8.5.dp)
    ) {



        val (userInfo , menu , sessionControl) = createRefs()




        // user info


        ElevatedCard( colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(userInfo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ) {


            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
            {

                val (editButton, profileImage, username) = createRefs()

                if (imageBitmap != null){
                    Image(
                        bitmap = imageBitmap!! ,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .width(80.dp)
                            .height(80.dp)
                            .constrainAs(profileImage) {
                                start.linkTo(parent.start)
                                end.linkTo(username.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(editButton.top)

                            },
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.account_circle_24px) ,
                        contentDescription = "" ,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .width(30.dp)
                            .height(30.dp)
                            .clickable {
                                Log.d("chatProfilePicture", "clicked...")
                                mainActivityVM.updateShowMore(true)
                            }
                    )
                }


                FilledTonalButton(onClick = {
                    mainActivityVM.updateShowEditProfileCard(true)
                },
                    border = BorderStroke(0.dp, Color.Transparent) ,
                    shape = RoundedCornerShape(10.dp) ,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(id = R.color.blue)
                    ) ,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                    ,
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(profileImage.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .width(150.dp)
                        .height(40.dp)
                        .padding(top = 8.5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.editprofilecard) ,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold ,
                            color = colorResource(id = R.color.swhite)
                        )
                    )
                }





                Text(
                    text = displayName!!,
                    style = TextStyle(
                        fontSize = 23.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .constrainAs(username) {
                            start.linkTo(profileImage.end)
                            end.linkTo(parent.end)
                            top.linkTo(profileImage.top)
                            bottom.linkTo(editButton.top)
                            width = Dimension.fillToConstraints
                        }
                )

            }


        }

        // menu

        Column (
            modifier = Modifier
                .padding(top = 20.dp)
                .constrainAs(menu) {
                    top.linkTo(userInfo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {

            // hosbeslerim button
            TextButton(
                onClick = {
                          scope.launch {
                              sheetState.hide()
                              MainActivity.navigate?.navigate("whisper")
                          }.invokeOnCompletion {
                              if (!sheetState.isVisible) mainActivityVM.updateShowMore(false)
                          }
                } ,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row ( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
                    Text(
                        text = stringResource(id = R.string.hosbeslerim) ,
                        style = TextStyle(
                            fontSize = 20.sp ,
                            fontFamily = FontFamily.SansSerif
                        )
                    )

                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
                }

            }

            // private rooms button
            TextButton(
                onClick = {

                    scope.launch {
                        MainActivity.navigate?.navigate("privaterooms")
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) mainActivityVM.updateShowMore(false)
                    }

                          } ,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row ( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
                    Text(
                        text = stringResource(id = R.string.privaterooms) ,
                        style = TextStyle(
                            fontSize = 20.sp ,
                            fontFamily = FontFamily.SansSerif
                        )
                    )

                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
                }

            }
            TextButton(
                onClick = {

                    scope.launch {
                        MainActivity.navigate?.navigate("random")
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) mainActivityVM.updateShowMore(false)
                    }

                } ,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row ( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
                    Text(
                        text = stringResource(id = R.string.random) ,
                        style = TextStyle(
                            fontSize = 20.sp ,
                            fontFamily = FontFamily.SansSerif
                        )
                    )

                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
                }

            }
        }
        Column (
            modifier = Modifier
                .constrainAs(sessionControl){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {

            // TODO: Hesabı sil butonu ...

            TextButton(
                onClick = {

                    /**
                     * hesabı silme sayfasına yönlendir ...
                     * **/

                    MainActivity.navigate?.navigate("deleteaccount")

                } ,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row () {
                    Text(
                        text = stringResource(id = R.string.deleteaccount) ,
                        style = TextStyle(
                            fontSize = 20.sp ,
                            fontFamily = FontFamily.SansSerif
                        )
                    )

                    Image(
                        imageVector = Icons.Filled.ExitToApp ,
                        contentDescription = "" ,
                        modifier = Modifier.padding(start = 8.5.dp)
                        )
                }

            }
        }


    }
}


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MoreContentPreview(){
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    HoşbeşTheme {


        MoreContent(scope , sheetState , MainActivityVM() , context)

    }
}
