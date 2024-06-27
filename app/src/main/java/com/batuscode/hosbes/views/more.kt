package com.batuscode.hosbes.views

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
        .padding(20.dp)
    ) {



        val (userInfo , menu , sessionControl) = createRefs()




        // user info
        Row ( verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding()
                .constrainAs(userInfo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ) {


            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                {

                    val (editButton, profileImage, username) = createRefs()

                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .width(80.dp)
                            .height(80.dp)
                            .constrainAs(profileImage) {
                                start.linkTo(parent.start)
                                end.linkTo(username.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)

                            },
                        contentScale = ContentScale.FillBounds
                    )

                    OutlinedIconButton(onClick = {
                                                 mainActivityVM.updateShowEditProfileCard(true)
                    },
                        border = BorderStroke(0.dp, Color.Transparent)
                        ,
                        modifier = Modifier
                            .constrainAs(editButton) {
                                top.linkTo(profileImage.top)
                                bottom.linkTo(profileImage.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(end = 8.5.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
                    }





                    Text(
                        text = displayName!!,
                        style = TextStyle(
                            fontSize = 23.sp
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(8.dp)
                            .constrainAs(username) {
                                start.linkTo(profileImage.end)
                                end.linkTo(editButton.start)
                                top.linkTo(profileImage.top)
                                bottom.linkTo(profileImage.bottom)
                                width = Dimension.fillToConstraints
                            }
                    )

                }


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
        }
        Column (
            modifier = Modifier
                .constrainAs(sessionControl){
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {

            // delete account button
            TextButton(
                onClick = { /*TODO*/ } ,
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
