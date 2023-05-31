package com.materiiapps.gloom.ui.widgets.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.materiiapps.gloom.ui.screens.repo.RepoScreen
import com.materiiapps.gloom.utils.navigate
import com.materiiapps.gloom.R
import com.materiiapps.gloom.gql.fragment.FeedRepository
import com.materiiapps.gloom.rest.dto.user.User
import com.materiiapps.gloom.ui.components.Avatar
import com.materiiapps.gloom.utils.parsedColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedRepoCard(
    repo: FeedRepository,
    starData: Pair<Boolean, Int>? = null,
    onStarPressed: () -> Unit = {},
    onUnstarPressed: () -> Unit = {},
) {
    val nav = LocalNavigator.currentOrThrow
    val viewerHasStarred = starData?.first ?: repo.viewerHasStarred
    val starCount = starData?.second ?: repo.stargazerCount
    val (starColor, starIcon) = if (viewerHasStarred) Color(0xFFF1E05A) to Icons.Filled.Star else LocalContentColor.current to Icons.Outlined.StarBorder

    ElevatedCard(
        onClick = { nav.navigate(RepoScreen(repo.owner.login, repo.name)) },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (repo.openGraphImageUrl.toString()
                .startsWith("https://repository-images.githubusercontent.com")
        )
            AsyncImage(
                model = repo.openGraphImageUrl,
                null,
                modifier = Modifier
                    .aspectRatio(2f)
                    .fillMaxWidth()
            )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Avatar(
                    url = repo.owner.avatarUrl,
                    contentDescription = stringResource(
                        R.string.noun_users_avatar,
                        repo.owner.login
                    ),
                    type = User.Type.fromTypeName(repo.owner.__typename),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    buildAnnotatedString {
                        append(repo.owner.login)
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(0.5f))) {
                            append(" / ")
                        }
                        append(repo.name)
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }

            repo.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            starIcon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = starColor
                        )
                        Text(text = starCount.toString())
                    }

                    if (repo.primaryLanguage != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Circle,
                                contentDescription = repo.primaryLanguage.name,
                                modifier = Modifier.size(15.dp),
                                tint = repo.primaryLanguage.color?.parsedColor
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(text = repo.primaryLanguage.name)
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = pluralStringResource(
                        id = R.plurals.noun_contributors,
                        count = repo.contributorsCount,
                        repo.contributorsCount
                    ), style = MaterialTheme.typography.labelLarge
                )
            }

            FilledTonalButton(
                onClick = {
                    if (viewerHasStarred) onUnstarPressed() else onStarPressed()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    starIcon,
                    contentDescription = null,
                    tint = starColor
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    if (viewerHasStarred) stringResource(R.string.action_unstar) else stringResource(
                        R.string.action_star
                    )
                )
            }
        }
    }
}