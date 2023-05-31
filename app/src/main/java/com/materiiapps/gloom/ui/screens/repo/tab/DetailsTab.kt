package com.materiiapps.gloom.ui.screens.repo.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.materiiapps.gloom.R
import com.materiiapps.gloom.ui.components.LargeSegmentedButton
import com.materiiapps.gloom.ui.components.LargeSegmentedButtonRow
import com.materiiapps.gloom.ui.components.RefreshIndicator
import com.materiiapps.gloom.ui.viewmodels.repo.tab.RepoDetailsViewModel
import com.materiiapps.gloom.ui.widgets.Markdown
import com.materiiapps.gloom.ui.widgets.repo.ContributorsRow
import com.materiiapps.gloom.ui.widgets.repo.LanguageMakeup
import org.koin.core.parameter.parametersOf
import java.util.UUID

class DetailsTab(
    private val owner: String,
    private val name: String
): Tab {

    override val key = "$owner/$name-${UUID.randomUUID()}"
    override val options: TabOptions
        @Composable get() = TabOptions(1u, stringResource(id = R.string.repo_tab_details))

    @Composable
    override fun Content() = Screen()

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Screen(
        viewModel: RepoDetailsViewModel = getScreenModel { parametersOf(owner to name) }
    ) {
        val refreshState = rememberPullRefreshState(
            refreshing = viewModel.detailsLoading,
            onRefresh = { viewModel.loadDetails() })
        val repoDetails = viewModel.details

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
                .clipToBounds()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                repoDetails?.let { details ->
                    if (!details.description.isNullOrBlank()) {
                        Text(
                            text = details.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                            thickness = 0.5.dp,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        LargeSegmentedButtonRow {
                            LargeSegmentedButton(
                                icon = if (repoDetails.viewerHasStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                text = pluralStringResource(
                                    id = R.plurals.stars,
                                    count = repoDetails.stargazerCount,
                                    repoDetails.stargazerCount
                                )
                            )
                            repoDetails.licenseInfo?.let {
                                LargeSegmentedButton(
                                    icon = painterResource(R.drawable.ic_balance_24),
                                    text = it.nickname ?: it.key.uppercase()
                                )
                            }
                            LargeSegmentedButton(
                                icon = painterResource(R.drawable.ic_fork_24),
                                text = pluralStringResource(
                                    id = R.plurals.forks,
                                    count = repoDetails.forkCount,
                                    repoDetails.forkCount
                                )
                            )
                        }
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                        thickness = 0.5.dp,
                    )

                    if (!(details.readme?.contentHTML).isNullOrBlank()) {
                        Text(
                            buildAnnotatedString {
                                append("README")
                                withStyle(
                                    SpanStyle(MaterialTheme.colorScheme.onBackground.copy(0.5f))
                                ) {
                                    append(".md")
                                }
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(16.dp)
                        )
                        Markdown(
                            text = details.readme!!.contentHTML.toString(),
                            Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    repoDetails.contributors.let {
                        if (it.nodes?.isNotEmpty() == true) Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                            thickness = 0.5.dp,
                        )

                        ContributorsRow(contributors = it)
                    }

                    repoDetails.languages?.languages?.let {
                        if (it.edges?.isNotEmpty() == true) Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                            thickness = 0.5.dp,
                        )
                        LanguageMakeup(it)
                    }
                }
            }
            RefreshIndicator(state = refreshState, isRefreshing = viewModel.detailsLoading)
        }
    }

}