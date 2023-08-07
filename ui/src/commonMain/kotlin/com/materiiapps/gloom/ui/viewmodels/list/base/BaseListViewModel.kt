package com.materiiapps.gloom.ui.viewmodels.list.base

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.apollographql.apollo3.api.Query

abstract class BaseListViewModel<I : Any, D : Query.Data?> : ScreenModel {

    val items = Pager(PagingConfig(pageSize = 30)) {
        object : PagingSource<String, I>() {
            override suspend fun load(params: LoadParams<String>): LoadResult<String, I> {
                val page = params.key

                val response = loadPage(page)

                val nextKey = getCursor(response)

                val nodes = createItems(response)

                return LoadResult.Page(
                    data = nodes,
                    nextKey = nextKey,
                    prevKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<String, I>): String? =
                state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.prevKey
                }
        }
    }.flow.cachedIn(coroutineScope)

    abstract suspend fun loadPage(cursor: String? = null): D

    abstract fun createItems(data: D): List<I>

    abstract fun getCursor(data: D): String?

}