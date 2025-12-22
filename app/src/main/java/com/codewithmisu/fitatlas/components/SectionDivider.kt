package com.codewithmisu.fitatlas.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SectionDivider(
    title: String,
    modifier: Modifier = Modifier,
    infoAction: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.W600)
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            )
            if (infoAction != null) {
                IconButton(
                    onClick = infoAction
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Info Item",
                    )
                }
            }
        }
    }
}