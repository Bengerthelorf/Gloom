package com.materiiapps.gloom.ui.widgets.repo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.materiiapps.gloom.Res
import com.materiiapps.gloom.gql.fragment.ReleaseItem
import com.materiiapps.gloom.ui.components.Label
import com.materiiapps.gloom.ui.screens.release.ReleaseScreen
import com.materiiapps.gloom.ui.theme.DarkGreen
import com.materiiapps.gloom.ui.utils.TimeUtils.getTimeSince
import com.materiiapps.gloom.utils.ifNullOrBlank
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ReleaseItem(
    repoOwner: String,
    repoName: String,
    release: ReleaseItem
) {
    val nav = LocalNavigator.currentOrThrow

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable { nav.push(ReleaseScreen(repoOwner, repoName, release.tagName)) }
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = release.name.ifNullOrBlank { release.tagName },
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = getTimeSince(release.createdAt),
                color = LocalContentColor.current.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelLarge
            )

            if (release.isLatest) {
                Label(
                    text = stringResource(Res.strings.label_latest),
                    textColor = DarkGreen
                )
            }

            if (release.isPrerelease) {
                Label(
                    text = stringResource(Res.strings.label_prerelease),
                    textColor = Color(0xFFFF9800)
                )
            }
        }
    }
}