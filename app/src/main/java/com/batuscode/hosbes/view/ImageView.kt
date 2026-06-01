package com.batuscode.hosbes.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.batuscode.hosbes.ui.theme.HoşbeşTheme


@Composable
fun card(){

    Row ( verticalAlignment = Alignment.CenterVertically

    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {


            ConstraintLayout (modifier = Modifier.fillMaxWidth()) {

                val (editButton, profileImage, username) = createRefs()


                OutlinedIconButton(onClick = {

                },
                    border = BorderStroke(0.dp, Color.Transparent)
                    ,
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                        .padding(end = 8.5.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
                }


                Image(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.5.dp)
                        .width(80.dp)
                        .height(80.dp)
                        .constrainAs(profileImage) {
                            start.linkTo(parent.start)
                            end.linkTo(username.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "displayName",
                    style = TextStyle(
                        fontSize = 23.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .constrainAs(username) {
                            start.linkTo(profileImage.end)
                            end.linkTo(editButton.start)
                            top.linkTo(profileImage.top)
                            bottom.linkTo(profileImage.bottom)

                        }
                )

            }


        }

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun cardPreview(){
    HoşbeşTheme {
        card()
    }
}