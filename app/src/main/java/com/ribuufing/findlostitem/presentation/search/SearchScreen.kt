package com.ribuufing.findlostitem.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ribuufing.findlostitem.presentation.home.LostItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = {
                    Text(
                        "Search for items",
                        style = TextStyle(
                            color = Color(0xFF99704D),
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                prefix = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color(0xFF99704D)
                    )
                },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = Color(0xFFED822B),
                    focusedBorderColor = Color(0xFFED822B),
                    focusedPrefixColor = Color(0xFFED822B),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFED822B),
                    focusedTextColor = Color(0xFF99704D),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            if (isLoading) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(5) {
                        SearchShimmerEffect()
                    }
                }
            } else {
                if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                    Text(
                        "No results found",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(searchResults) { item ->
                            LostItemRow(
                                item = item,
                                viewModel = hiltViewModel(),
                                navController = navController,
                                user = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchShimmerEffect() {
    // Implement shimmer effect similar to the one in LostItemsScreen
}