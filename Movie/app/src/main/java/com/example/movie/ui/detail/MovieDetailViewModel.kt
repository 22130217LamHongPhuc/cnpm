package com.example.movie.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.movie.domain.model.Comment
import com.example.movie.ui.repository.MovieDetailRepository
import com.example.movie.movie.domain.model.Movie
import com.example.movie.movie.domain.model.MovieDetail
import com.example.movie.ui.home.HomeUiState
import com.example.movie.ui.repository.CommentRepository
import com.example.movie.util.K
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieDetailViewModel @Inject constructor(val repository: MovieDetailRepository, val repoCmt:CommentRepository) :ViewModel() {

    private var _stateMovieDetail = MutableStateFlow<UiState<MovieDetail>>(UiState.Default)

    var stateMovieDetail = _stateMovieDetail.asStateFlow()

    private var _cmtMovieDetail = MutableStateFlow<UiState<Boolean>>(UiState.Default)

    var cmtMovieDetail = _cmtMovieDetail.asStateFlow()


    private val _cmtList = MutableStateFlow<MutableList<Comment>>(mutableListOf())
    val cmtList: StateFlow<List<Comment>> = _cmtList.asStateFlow()

    private var _favoriteMovieDetail = MutableStateFlow<Boolean>(false)

    var favoriteMovieDetail = _favoriteMovieDetail.asStateFlow()


   fun getMovieDetailBySlug(slug:String,idMovie: String,idUser: String?){

        viewModelScope.launch {

            _stateMovieDetail.value =  UiState.Loading

          launch {
              // 1.8 goi toi phuong thuc fetchMovieDetailBySlug() trong repository
              val movieDetail = repository.fetchMovieDetailBySlug(slug)

              if(movieDetail !=null){
                  Log.d("detail","ss")
                  // 1.9 tra ve doi tuong moviedetail

                  _stateMovieDetail.value = UiState.Success(data = movieDetail )
              }else{
                  Log.d("detail","se")
                  _stateMovieDetail.value = UiState.Error(message = "Error")
              }
            }

            launch {
                if(!idUser.isNullOrBlank()) {
                    val result = repoCmt.isFavoriteMovie(idUser,idMovie)
                    _favoriteMovieDetail.value = result
                }
            }





        }
    }


     fun addComment(idMovie:String,idUser:String,content:String,nameUser:String){

         _cmtMovieDetail.value = UiState.Loading

        val cmt = Comment(idMovie,idUser, nameUser,content)
        repoCmt.addComment(cmt){
            result ->
           result.onSuccess {
            _cmtList.update {
                current ->
                    (current + cmt) as MutableList<Comment>
                }

           }

            result.onFailure {

            }

        }
    }

    fun loadAllComment(idMovie: String){
        Log.d("loadcmt","ss")
        repoCmt.getCommentsRealtime(idMovie){
            list ->
            _cmtList.update { current ->
                (current + list) as MutableList<Comment>
            }

            Log.d("loadcmt",_cmtList.value.size.toString())

        }
        }

    fun clickFavorite(uid: String?, id: String) {
        repoCmt.setFavoriteMovie(uid,id){
            _favoriteMovieDetail.value = it
        }
    }

}




sealed class UiState<out T> {

    object Default : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()

}