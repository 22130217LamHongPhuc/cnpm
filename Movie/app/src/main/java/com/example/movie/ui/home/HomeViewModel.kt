package com.example.movie.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.ui.repository.MovieRepository
import com.example.movie.movie.domain.model.Movie
import com.example.movie.movie.domain.model.MovieSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MovieRepository) :ViewModel() {



    private var _stateHome = MutableStateFlow(HomeUiState())
    var stateHome = _stateHome.asStateFlow()

    init {
        getDataHomeMovie()
    }

    fun getDataHomeMovie(){
        viewModelScope.launch {
            _stateHome.update { it.copy(isLoading = true) }

            try {
                coroutineScope {
                    val lastMovieDeffered = async { repository.fetchHomeMovie() }
                    val movieByTvShow = async { repository.fetchHomeTypeList("tv-shows") }
                    val movieCartoon = async { repository.fetchHomeTypeList("hoat-hinh") }
                    val seriesMovie = async { repository.fetchHomeTypeList("phim-bo") }
                    val oddMovie = async { repository.fetchHomeTypeList("phim-le") }

                    val latestMovies = lastMovieDeffered.await()
                    val tvShows = movieByTvShow.await()
                    val cartoons = movieCartoon.await()
                    val seriesMovies = seriesMovie.await()
                    val oddMovies = oddMovie.await()



                    _stateHome.update {
                        it.copy(
                            latestMovies = latestMovies,
                            tvShowMovies = tvShows,
                            cartoonMovies = cartoons,
                            seriesMovies = seriesMovies,
                            oddMovies = oddMovies,
                            isLoading = false,
                            success = true
                        )

                    }

                }
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching home movies", e)
                _stateHome.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val latestMovies: List<Movie> = emptyList(),
    val tvShowMovies: List<MovieSearch> = emptyList(),
    val cartoonMovies: List<MovieSearch> = emptyList(),
    val oddMovies: List<MovieSearch> = emptyList(),
    val seriesMovies: List<MovieSearch> = emptyList(),
    val success: Boolean = false,
    val errorMessage: String? = null
)
