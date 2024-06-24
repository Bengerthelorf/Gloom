package com.materiiapps.gloom.ui.viewmodels.home

import androidx.compose.runtime.mutableStateMapOf
import app.cash.paging.compose.LazyPagingItems
import cafe.adriel.voyager.core.model.screenModelScope
import com.materiiapps.gloom.api.repository.GraphQLRepository
import com.materiiapps.gloom.api.utils.getOrNull
import com.materiiapps.gloom.api.utils.ifSuccessful
import com.materiiapps.gloom.gql.FeedQuery
import com.materiiapps.gloom.ui.viewmodels.list.base.BaseListViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: GraphQLRepository
) : BaseListViewModel<FeedQuery.Node, FeedQuery.Data?>() {

    val starredRepos = mutableStateMapOf<String, Pair<Boolean, Int>>()
    val followedUsers = mutableStateMapOf<String, Pair<Boolean, Int>>()

    fun refresh(pagingData: LazyPagingItems<FeedQuery.Node>) {
        starredRepos.clear()
        followedUsers.clear()
        pagingData.refresh()
    }

    override suspend fun loadPage(cursor: String?): FeedQuery.Data? =
        repo.getFeed(cursor).getOrNull()

    override fun getCursor(data: FeedQuery.Data?): String? =
        data?.viewer?.dashboard?.feed?.items?.pageInfo?.endCursor

    override fun createItems(data: FeedQuery.Data?): List<FeedQuery.Node> {
        return data?.viewer?.dashboard?.feed?.items?.nodes?.filterNotNull() ?: emptyList()
    }

    fun starRepo(id: String) = screenModelScope.launch {
        repo.starRepo(id).ifSuccessful {
            starredRepos[id] = it
        }
    }

    fun unstarRepo(id: String) = screenModelScope.launch {
        repo.unstarRepo(id).ifSuccessful {
            starredRepos[id] = it
        }
    }

    fun followUser(id: String) = screenModelScope.launch {
        repo.followUser(id).ifSuccessful {
            followedUsers[id] = it
        }
    }

    fun unfollowUser(id: String) = screenModelScope.launch {
        repo.unfollowUser(id).ifSuccessful {
            followedUsers[id] = it
        }
    }

}