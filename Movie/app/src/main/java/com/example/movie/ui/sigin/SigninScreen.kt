package com.example.movie.ui.sigin

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.movie.R
import com.example.movie.movie.domain.model.MovieDetail
import com.example.movie.ui.detail.UiState
import com.example.movie.ui.repository.FacebookAuthHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun signIn(viewmodel: SiginViewmodel = hiltViewModel(),activity: ComponentActivity,navController: NavController,loginFacebook:()-> Unit) {


    val stateSignin = viewmodel.stateSignin.collectAsState()
    val coroutineScope = rememberCoroutineScope()



    val signInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                    coroutineScope.launch {
                        viewmodel.handleSignInResult(result.data) // Xử lý đăng nhập với Firebase
                }
            }
        }

    val launcherFb = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
    }

    LaunchedEffect(key1 = stateSignin.value){
    when(stateSignin.value){
        is UiState.Error -> {
            Log.d("google","ee")
        }
        is UiState.Success -> {
            val state = (stateSignin.value as UiState.Success<String>).data
            Log.d("ppp","success")

            navController.popBackStack()
            Toast.makeText(activity,"Đăng nhập thành công",Toast.LENGTH_SHORT).show()

        }
        else -> {

        }

    }

}
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (bgRef,logoRef,formRef) = createRefs()
        Background(Modifier.constrainAs(bgRef){
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end) })

        Logo(Modifier.constrainAs(logoRef){
                top.linkTo(parent.top, margin = 50.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
        })

        FormSignin(Modifier.constrainAs(formRef){
            top.linkTo(logoRef.bottom, margin = 50.dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            
        }, signinWithEmail = {
            email,pass ->
            coroutineScope.launch {
                viewmodel.siginWithEmail(email,pass)
            }

        }, signinWithFacebook = loginFacebook){
            viewmodel.openDialogLogin(activity,signInLauncher)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSignin(modifier: Modifier,signinWithEmail:(String,String) -> Unit,signinWithFacebook:() -> Unit,signin:() -> Unit) {
    var field_email by remember {
        mutableStateOf("")
    }

    var field_pass by remember {
        mutableStateOf("")
    }

    Column(modifier = modifier
        .background(
            color = Color(0xFFAFD1B6),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        )
        .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Xin chào",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFFEDF1EA),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Hãy nhập thông tin đăng nhập của bạn",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )
        )
        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Email",modifier =Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(value = field_email,modifier= Modifier
            .fillMaxWidth()
            .height(50.dp), onValueChange = {
            field_email = it
        }, placeholder = {
            Text(text = "Nhập email của bạn",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD5C4C4)))
        },
        trailingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription ="icon" )
        })



        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Mật khẩu", modifier =Modifier.fillMaxWidth(),style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(value = field_pass,modifier= Modifier
            .fillMaxWidth()
            .height(50.dp), onValueChange = {
            field_pass = it
        }, placeholder = {
            Text(text = "Nhập mật khẩu của bạn",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD5C4C4)))
        },
            trailingIcon = {
            Icon(imageVector = Icons.Default.Password, contentDescription ="icon" )
        })

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Quên mật khẩu ",
            modifier =Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.
            copy(color = Color.White, fontWeight = FontWeight.Bold))


        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {
            signinWithEmail(field_email,field_pass)
        },
            modifier= Modifier
                .width((LocalConfiguration.current.screenWidthDp*3/4).dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xffC68EFD),
            contentColor = Color.White
        )) {
            Text(text = "Đăng nhập",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFF7EDED), fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(modifier= Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center){
            Image(painter = painterResource(id = R.drawable.img_7), contentDescription ="",modifier=Modifier.size(40.dp).clickable(onClick = signin) )
            Spacer(modifier = Modifier.width(25.dp))
            Image(painter = painterResource(id = R.drawable.twitter), contentDescription ="" ,modifier= Modifier.size(40.dp).clickable (onClick = signinWithFacebook))
            Spacer(modifier = Modifier.width(25.dp))
            Image(painter = painterResource(id = R.drawable.github), contentDescription ="" ,modifier= Modifier.size(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logo(modifier: Modifier) {


    Column(modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = R.drawable.baseline_cruelty_free_24),
            contentDescription = "rabbit",
            modifier = Modifier.size(45.dp),tint = Color.Green)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Go Movie",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        )






    }
}

@Composable
fun Background(modifier: Modifier) {
  Box(modifier = modifier){
      Image(painter = painterResource(id = R.drawable.img_6), contentDescription = "logo",
          modifier = Modifier.fillMaxSize() ,
          contentScale = ContentScale.Crop)
  }
}

@Preview
@Composable
fun test(){

}