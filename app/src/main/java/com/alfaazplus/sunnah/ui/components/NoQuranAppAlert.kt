package com.alfaazplus.sunnah.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper

@Composable
fun NoQuranAppAlert() {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(20.dp)
            .padding(bottom = 20.dp)
            .fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Image(
                painter = painterResource(R.drawable.logo_quranapp),
                contentDescription = null,
                modifier = Modifier
                    .height(60.dp)
                    .width(60.dp)
                    .padding(4.dp)
            )
        }
        Text(
            text = stringResource(R.string.install_quranapp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 15.dp)
        )
        Text(
            text = stringResource(R.string.install_quranapp_description),
            modifier = Modifier.padding(bottom = 15.dp)
        )
        Button(onClick = {
            NavigationHelper.openQuranAppPlayStoreListing(context)
        }) {
            Text(text = "Install from Google Play")
        }
    }
}