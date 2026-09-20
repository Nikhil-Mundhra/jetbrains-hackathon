package com.communityconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.communityconnect.domain.model.Post
import com.communityconnect.presentation.viewmodel.CreatePostViewModel
import com.communityconnect.presentation.viewmodel.MainViewModel
import com.communityconnect.presentation.viewmodel.PostDetailViewModel
import com.communityconnect.presentation.viewmodel.ProfileViewModel
import com.communityconnect.ui.screens.CreatePostScreen
import com.communityconnect.ui.screens.HomeScreen
import com.communityconnect.ui.screens.PostDetailScreen
import com.communityconnect.ui.screens.ProfileScreen
import com.communityconnect.ui.theme.Theme
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable
fun App(
    mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    postDetailViewModel: PostDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    createPostViewModel: CreatePostViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    profileViewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navController = rememberNavController()
    val rootScope = remember { CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { _, e -> e.printStackTrace() }) }
    
    Theme {
        Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = mainViewModel,
                        onPostClick = { post ->
                            postDetailViewModel.loadPost(post.id)
                            navController.navigate("post_detail/${post.id}")
                        },
                        onCreatePostClick = { navController.navigate("create_post") },
                        onProfileClick = { navController.navigate("profile") },
                        onCommunitiesClick = { /* TODO */ },
                        onMapClick = { /* TODO */ }
                    )
                }
                
                composable(
                    route = "post_detail/{postId}",
                    arguments = listOf(androidx.navigation.navArgument("postId") { type = androidx.navigation.NavType.StringType() })
                ) { backStackEntry ->
                    val postId = backStackEntry.getString()?.let { java.util.UUID.fromString(it) }
                    postId?.let { id ->
                        PostDetailScreen(
                            viewModel = postDetailViewModel,
                            postId = id,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                
                composable("create_post") {
                    CreatePostScreen(
                        viewModel = createPostViewModel,
                        onBackClick = { navController.popBackStack() },
                        onPostCreated = { post ->
                            navController.popBackStack()
                            mainViewModel.refreshPosts()
                        }
                    )
                }
                
                composable("profile") {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onEditProfileClick = { /* TODO */ },
                        onSettingsClick = { /* TODO */ },
                        onMyPostsClick = { /* TODO */ },
                        onMyCommunitiesClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}