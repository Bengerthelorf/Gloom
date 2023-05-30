package com.materiiapps.gloom.ui.widgets.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.materiiapps.gloom.R
import com.materiiapps.gloom.gql.fragment.FollowRecommendationFeedItemFragment

@Composable
fun RecommendedFollowUserItem(
    item: FollowRecommendationFeedItemFragment,
    followData: Pair<Boolean, Int>? = null,
    onFollowPressed: (String) -> Unit = {},
    onUnfollowPressed: (String) -> Unit = {},
) {
    val user = item.followee
    val userId = user.feedUser?.id ?: user.feedOrg?.id!!

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        FeedActor(
            iconVector = Icons.Outlined.PeopleAlt,
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(0.7f))) {
                    append(stringResource(id = R.string.recommended))
                }
            },
            badgeIcon = Icons.Filled.AutoAwesome
        )

        FeedUserCard(
            user.feedUser to user.feedOrg,
            followData = followData,
            onFollowPressed = { onFollowPressed(userId) },
            onUnfollowPressed = { onUnfollowPressed(userId) }
        )
    }
}