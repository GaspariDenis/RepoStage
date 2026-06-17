package com.example.esercizioapi.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import okio.IOException

class PokePagingSource(
    private val backend: Backend,
    val elementPage : Int
) : PagingSource<Int, UiPokemon>()
{
    private val tag = "Paging"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiPokemon> {
        try{
            val nextPageNumber = params.key ?: 1

            Log.w(tag, nextPageNumber.toString())

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

    override fun getRefreshKey(state: PagingState<Int, UiPokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}