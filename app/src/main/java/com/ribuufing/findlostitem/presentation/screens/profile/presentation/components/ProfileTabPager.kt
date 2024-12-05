package com.ribuufing.findlostitem.presentation.screens.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Tab
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ribuufing.findlostitem.data.model.LostItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileTabPager(it: PaddingValues) {
    val tabs = listOf(
        "Found items"
//        , "Tab 2", "Tab 3"
    )
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
    ) {

        TabRow(
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .height(0.2.dp),
                    color = Color.Black
                )
            },
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            tabs = {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }
        )


        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { page ->
            when (page) {
                0 -> ListContent()
                1 -> ListContent()
                2 -> ListContent()
            }
        }
    }
}

@Composable
fun ListContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        items(8) { index ->
            LostItemRow(
                LostItem(
                    title = "Macbook Air M1 Proc. 2024 Model",
                    date = "10/${index + 1}",
                    images = listOf("https://t4.ftcdn.net/jpg/03/38/11/83/360_F_338118300_Ou8AWHQ5DOumFIghTtOOJ8isc6LmmbL0.jpg")
                )
            )
        }
    }
}