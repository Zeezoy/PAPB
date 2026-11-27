package com.example.navigationlab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigationlab.data.local.DatabaseProvider
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.remote.RetrofitClient
import com.example.navigationlab.data.remote.StudentRemoteDataSource
import com.example.navigationlab.data.repository.DefaultStudentRepository
import com.example.navigationlab.data.repository.FavoriteRepository
import com.example.navigationlab.ui.detail.DetailViewModel
import com.example.navigationlab.ui.favorites.FavoritesViewModel
import com.example.navigationlab.ui.home.HomeViewModel
import com.example.navigationlab.ui.screen.AboutScreen
import com.example.navigationlab.ui.screen.DetailScreen
import com.example.navigationlab.ui.screen.FavoritesScreen
import com.example.navigationlab.ui.screen.HomeScreen
import com.example.navigationlab.ui.screen.ProfileScreen
import com.example.navigationlab.data.location.DefaultLocationRepository
import com.google.android.gms.location.LocationServices
import com.example.navigationlab.ui.location.LocationScreen
import com.example.navigationlab.ui.location.LocationViewModel

@Composable
fun NavigationLabApp() {
    val context = LocalContext.current
    val remoteDataSource = remember { StudentRemoteDataSource(RetrofitClient.apiService) }
    val repository = remember { DefaultStudentRepository(remoteDataSource) }
    val preferencesRepository = remember {
        UserPreferencesRepository(context.applicationContext)
    }
    val favoriteRepository = remember {
        FavoriteRepository(
            DatabaseProvider.getDatabase(context).favoriteStudentDao()
        )
    }

    val locationRepository = remember {
        DefaultLocationRepository(
            context = context.applicationContext,
            client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        )
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(repository, preferencesRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onStudentClick = { id -> navController.navigate("detail/$id") },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onAboutClick = { navController.navigate(Routes.ABOUT) },
                onFavoritesClick = { navController.navigate(Routes.FAVORITES) },
                onRetry = viewModel::retry,
                onRefresh = viewModel::refresh,
                onLocationClick = { navController.navigate(Routes.LOCATION) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("studentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1

            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository, favoriteRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            DetailScreen(
                uiState = uiState,
                onToggleFavorite = viewModel::toggleFavorite,
                onBack = { navController.popBackStack() },
                onRetry = { viewModel.loadStudent(studentId) }
            )
        }

        composable(Routes.FAVORITES) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.factory(favoriteRepository, preferencesRepository)
            )
            val favorites by viewModel.favorites.collectAsStateWithLifecycle()
            val sortAscending by viewModel.sortAscending.collectAsStateWithLifecycle()

            FavoritesScreen(
                favorites = favorites,
                sortAscending = sortAscending,
                onToggleSort = viewModel::toggleSort,
                onStudentClick = { id -> navController.navigate("detail/$id") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOCATION) {
            val viewModel: LocationViewModel = viewModel(
                factory = LocationViewModel.factory(locationRepository)
            )
            LocationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}