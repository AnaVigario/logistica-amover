package com.example.myamover.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myamover.R
import com.example.myamover.model.LoginViewModel
import com.example.myamover.navigation.NavRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    email: String,
    name: String,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    onOpenTasksClick: () -> Unit,
    onBackClick: () -> Unit,
    onOpenHistoryClick: () -> Unit,
    navController: NavController,
    //user: UserRemote,

) {
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
        }

        WindowWidthSizeClass.Medium -> {
        }

        WindowWidthSizeClass.Expanded -> {
        }

        else -> {
        }
    }

    val loginViewModel: LoginViewModel = viewModel()




    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {

            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(18.dp), // “pill”
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Francisco Peixoto",
                    //${user.name ?:
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.scrim,
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
//            shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.background),
//            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // header "flat"
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // LOGO
                    Card(
                        modifier = Modifier.size(width = 200.dp, height = 80.dp),
                        shape = RoundedCornerShape(30.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.outlineVariant)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo_verde),
                            contentDescription = "Logo AmoVeR",
                            modifier = Modifier.fillMaxSize(), // sem clip (já está cortado pela shape do Card)
                            contentScale = ContentScale.Fit
                        )

                    }

                    Spacer(Modifier.width(16.dp))


                    // TEXTO
                    Column {

                        Text(
                            "",
                            style = MaterialTheme.typography.titleLarge,
                            color = colorScheme.onTertiaryContainer
                        )
                        Text(
                            "",
                            style = MaterialTheme.typography.titleLarge,
                            color = colorScheme.onTertiaryContainer
                        )

                        Text(
                            text = "Logistic",
                            style = MaterialTheme.typography.titleLarge,
                            color = colorScheme.surfaceVariant
                        )


                    }

                }
            }

            Spacer(modifier = Modifier.height(120.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                //color = colorScheme.background
            ) {


                //TASKS
                Button(
                    onClick = onOpenTasksClick,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 40.dp)
                        .shadow(10.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.scrim
                    ),

                    ) {
                    Text(
                        text = stringResource(id = R.string.tasks),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = modifier.padding(8.dp)
                    )

                }

                Spacer(modifier = Modifier.height(60.dp))


                //History
                Button(
                    onClick = onOpenHistoryClick,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 40.dp)
                        .shadow(10.dp),  // mais alto que o normal
                    shape = MaterialTheme.shapes.large, // usa o shape do seu Shape.kt
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.scrim
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.history),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = modifier.padding(8.dp)
                    )

                }
                Spacer(modifier = Modifier.height(60.dp))


                //Logouut
                Button(
                    onClick = {
                        //1) termina sessão no ViewModel
                        loginViewModel.logout()
                        // 2) limpa navegação e volta ao Login
                        navController.navigate(NavRoutes.Login) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },

                    modifier = modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 40.dp)
                        .shadow(10.dp),   // mais alto que o normal
                    shape = MaterialTheme.shapes.large, // usa o shape do seu Shape.kt
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.scrim
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = modifier.padding(8.dp)
                    )

                }
            }
        }
    }
}





