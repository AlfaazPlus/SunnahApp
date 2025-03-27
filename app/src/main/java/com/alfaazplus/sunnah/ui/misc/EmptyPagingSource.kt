package com.alfaazplus.sunnah.ui.misc

import androidx.paging.PagingSource
import androidx.paging.PagingState

class EmptyPagingSource<T : Any> : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return LoadResult.Page(
            data = emptyList(), prevKey = null, nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null
}
