package com.materiiapps.gloom.ui.screens.repo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.materiiapps.gloom.R
import com.materiiapps.gloom.ui.components.BackButton
import com.materiiapps.gloom.ui.screens.profile.ProfileScreen
import com.materiiapps.gloom.ui.viewmodels.repo.RepoViewModel
import com.materiiapps.gloom.utils.navigate
import com.materiiapps.gloom.utils.shareText
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import java.util.UUID

class RepoScreen(
    private val owner: String,
    private val name: String
) : Screen {

    override val key = "$owner/$name-${UUID.randomUUID()}"

    @Composable
    override fun Content() = Screen()

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Screen(
        viewModel: RepoViewModel = getScreenModel { parametersOf(owner to name) }
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = { Toolbar(scrollBehavior, viewModel) }
        ) { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                val tabColor by animateColorAsState(
                    targetValue = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        3.dp
                    ).copy(scrollBehavior.state.collapsedFraction), label = "Tab color"
                )
                val badgeColor by animateColorAsState(
                    targetValue = Color.Black
                        .copy(scrollBehavior.state.collapsedFraction * 0.2f)
                        .compositeOver(
                            MaterialTheme.colorScheme.secondaryContainer
                        ), label = "Badge color"
                )
                val badgeTextColor by animateColorAsState(
                    targetValue = Color.White
                        .copy(scrollBehavior.state.collapsedFraction)
                        .compositeOver(
                            LocalContentColor.current
                        ), label = "Badge text color"
                )

                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    divider = {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                            thickness = 0.5.dp,
                        )
                    },
                    containerColor = tabColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.tabs.forEachIndexed { i, tab ->
                        Tab(
                            selected = pagerState.currentPage == i,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(i)
                                }
                            },
                            text = {
                                val badgeCount = viewModel.badgeCounts[i]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(tab.options.title)
                                    if (badgeCount != null && badgeCount > 0)
                                        Text(
                                            text = badgeCount.toString(),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            color = badgeTextColor,
                                            modifier = Modifier
                                                .widthIn(21.dp)
                                                .clip(CircleShape)
                                                .background(badgeColor)
                                                .padding(5.dp, 3.dp)
                                        )
                                }
                            }
                        )
                    }
                }

                HorizontalPager(
                    count = viewModel.tabs.size,
                    state = pagerState
                ) {
                    val tab = viewModel.tabs[it]

                    Box(
                        Modifier
                            .fillMaxSize()
                    ) {
                        tab.Content()
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    private fun Toolbar(
        scrollBehavior: TopAppBarScrollBehavior,
        viewModel: RepoViewModel
    ) {
        val avSize = Dp(55 - scrollBehavior.state.collapsedFraction * 55)
        val nav = LocalNavigator.current
        val ctx = LocalContext.current
        val loading by remember {
            derivedStateOf {
                viewModel.repoOverviewLoading
            }
        }

        LargeTopAppBar(
            title = {
                if (!loading) {
                    if (!viewModel.hasError && viewModel.repoOverview != null) {
                        val it = viewModel.repoOverview!!
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (avSize != 0.dp) AsyncImage(
                                model = it.owner.avatarUrl,
                                contentDescription = stringResource(
                                    R.string.noun_users_avatar,
                                    it.owner.login
                                ),
                                modifier = Modifier
                                    .size(avSize)
                                    .clip(
                                        if (it.owner.__typename == "User")
                                            CircleShape
                                        else RoundedCornerShape(15.dp)
                                    )
                                    .clickable { nav?.navigate(ProfileScreen(it.owner.login)) }
                            )
                            Column {
                                Text(
                                    text = it.owner.login,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                                )
                                Text(
                                    text = it.name,
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(Int.MAX_VALUE)
                                )
                            }
                        }
                    } else Text("Error loading repository")
                } else {
                    Box(modifier = Modifier.size(55.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            navigationIcon = { BackButton() },
            actions = {
                viewModel.repoOverview?.let {
                    IconButton(onClick = { ctx.shareText("https://github.com/${it.owner.login}/${it.name}") }) {
                        Icon(Icons.Filled.Share, stringResource(R.string.action_share))
                    }
                }
            }
        )
    }
}