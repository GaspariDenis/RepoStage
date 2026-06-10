package com.example.esercizioapi.network

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import coil.network.HttpException
import com.example.esercizioapi.PokeViewModel
import kotlinx.coroutines.flow.Flow
import okio.IOException
import kotlin.time.Duration.Companion.minutes

class PokePagingSource(
    private val backend: PokeViewModel,
    val elementPage : Int,
    val query: String
) : PagingSource<Int, Pokemon>()
{
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        try{
            val nextPageNumber = params.key ?: 1
            val response = backend.getInfoPokemons((nextPageNumber - 1) * elementPage, elementPage)

            return LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        }
        catch (e: IOException){
            return LoadResult.Error(e)
        }
        catch (e : HttpException){
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

class UserViewModel(
    val backend: PokeViewModel,
    val query : String
) : ViewModel() {

    val userPagingFlow : Flow<PagingData<Pokemon>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            PokePagingSource(backend, 20, query)
        }
    ).flow
        .cachedIn(viewModelScope)
}