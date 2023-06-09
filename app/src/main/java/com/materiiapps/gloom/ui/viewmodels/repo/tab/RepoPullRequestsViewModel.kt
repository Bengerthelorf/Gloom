package com.materiiapps.gloom.ui.viewmodels.repo.tab

import com.materiiapps.gloom.domain.repository.GraphQLRepository
import com.materiiapps.gloom.gql.RepoPullRequestsQuery
import com.materiiapps.gloom.gql.fragment.PullRequestOverview
import com.materiiapps.gloom.gql.type.PullRequestState
import com.materiiapps.gloom.rest.utils.getOrNull
import com.materiiapps.gloom.ui.viewmodels.list.base.BaseListViewModel

class RepoPullRequestsViewModel(
    private val gql: GraphQLRepository,
    nameWithOwner: Pair<String, String>
) : BaseListViewModel<PullRequestOverview, RepoPullRequestsQuery.Data?>() {

    val owner = nameWithOwner.first
    val name = nameWithOwner.second

    override suspend fun loadPage(cursor: String?): RepoPullRequestsQuery.Data? =
        gql.getRepoPullRequests(owner, name, cursor, listOf(PullRequestState.CLOSED, PullRequestState.MERGED, PullRequestState.OPEN)).getOrNull()

    override fun getCursor(data: RepoPullRequestsQuery.Data?): String? =
        data?.repository?.pullRequests?.pageInfo?.endCursor

    override fun createItems(data: RepoPullRequestsQuery.Data?): List<PullRequestOverview> =
        data?.repository?.pullRequests?.nodes?.mapNotNull { it?.pullRequestOverview } ?: emptyList()
}