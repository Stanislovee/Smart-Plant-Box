package com.example.smartplantbox.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplantbox.R
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme

val LexendSemiBold = FontFamily(
    Font(R.font.lexend_semibold, FontWeight.SemiBold)
)

val LexendMedium = FontFamily(
    Font(R.font.lexend_medium, FontWeight.Medium)
)

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit
) {

    val welcomeToText = stringResource(R.string.welcome_to)
    val appNameShortText = stringResource(R.string.app_name_short)
    val welcomeDescriptionText = stringResource(R.string.welcome_description)
    val getStartedText = stringResource(R.string.get_started)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E481E))
    ) {
        Image(
            painter = painterResource(id = R.drawable.top_overlay),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.bottom_overlay),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(0.9f))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFFFFFFF),
                            fontFamily = LexendSemiBold,
                            fontSize = 20.sp
                        )
                    ) {
                        append("$welcomeToText ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF99B66F),
                            fontFamily = LexendSemiBold,
                            fontSize = 20.sp
                        )
                    ) {
                        append(appNameShortText)
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = welcomeDescriptionText,
                fontSize = 15.sp,
                color = Color(0xFF91A37F),
                fontFamily = LexendMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF569033)
                )
            ) {
                Text(
                    text = getStartedText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontFamily = LexendSemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    SmartPlantBoxTheme {
        WelcomeScreen(
            onGetStartedClick = {}
        )
    }
}