package com.materiiapps.gloom.ui.viewmodels.profile

import com.materiiapps.gloom.api.models.ModelUser
import com.materiiapps.gloom.api.repository.GraphQLRepository
import com.materiiapps.gloom.api.utils.getOrNull
import com.materiiapps.gloom.gql.FollowingQuery
import com.materiiapps.gloom.ui.viewmodels.list.base.BaseListViewModel

class FollowingViewModel(
    private val repo: GraphQLRepository,
    private val username: String
) : BaseListViewModel<ModelUser, FollowingQuery.Data?>() {

    override suspend fun loadPage(cursor: String?) = repo.getFollowing(username, cursor).getOrNull()

    override fun getCursor(data: FollowingQuery.Data?) = data?.user?.following?.pageInfo?.endCursor

    override fun createItems(data: FollowingQuery.Data?): List<ModelUser> {
        val nodes = mutableListOf<ModelUser>()
        data?.user?.following?.nodes?.forEach {
            if (it != null) nodes.add(ModelUser.fromFollowingQuery(it))
        }
        return nodes
    }

}